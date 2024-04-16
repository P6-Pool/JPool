package org.CueCraft.ShotEvaluator;

import JFastfiz.*;
import org.CueCraft.Pool.Table;
import org.CueCraft.ShotGenerator.ShotStep;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.function.Predicate;

public class ShotEvaluator {

    public interface ShotDecider {
        Triplet<Double, ShotStep, ShotParams> decideShot(ShotParamsGenerator shotParamsGenerator, TableState table, Table.PlayerPattern playerPattern);
    }

    public interface ShotGenerator {
        ArrayList<ShotStep> generateShots();
    }

    public interface ShotParamsGenerator {
        ArrayList<Pair<ShotStep, ShotParams>> generateShotParams();
    }

    public interface ShotRewarder {
        double rewardShot(Shot shot, Table.PlayerPattern playerPattern, TableState tableStateBefore);
    }

    public static Triplet<Double, ShotStep, ShotParams> getBestShot(TableState tableState, int depth, Table.PlayerPattern playerPattern, ShotDecider shotDecider, ShotRewarder shotRewarder) {
        ShotGenerator sg = () -> org.CueCraft.ShotGenerator.ShotGenerator.generateShots(tableState, depth, playerPattern);
        ShotParamsGenerator spg = () -> shotVelocitySampling(tableState, 50, playerPattern, sg, shotRewarder);
        return shotDecider.decideShot(spg, tableState, playerPattern);
    }

    public static ArrayList<Pair<ShotStep, ShotParams>> shotVelocitySampling(TableState tableState, int numSamples, Table.PlayerPattern playerPattern, ShotGenerator shotGenerator, ShotRewarder shotRewarder) {
        ArrayList<ShotStep> shots = shotGenerator.generateShots();
        ArrayList<Pair<ShotStep, ShotParams>> shotRewards = new ArrayList<>();
        ShotParams params = new ShotParams(0, 0, 20, 0, 0);

        for (ShotStep shot : shots) {
            double bestVelocity = 1;
            double bestReward = -1;
            double angle = shot.getShotAngle();
            params.setPhi(angle);

            for (int i = 0; i < numSamples; i++) {
                double velocity = (double) i / (double) numSamples * 10.0;
                params.setV(velocity);

                TableState tableStateCopy = new TableState(tableState);
                if (!(tableStateCopy.isPhysicallyPossible(params) == TableState.OK_PRECONDITION)) {
                    continue;
                }

                Shot shotEvent;
                try {
                    shotEvent = tableStateCopy.executeShot(params);
                } catch (Exception e) {
                    continue;
                }
                double reward = shotRewarder.rewardShot(shotEvent, playerPattern, new TableState(tableState));

                if (reward > bestReward) {
                    bestReward = reward;
                    bestVelocity = velocity;
                }
            }

            if (bestReward > 0) {
                ShotParams bestParams = new ShotParams(params);
                bestParams.setV(bestVelocity);
                shotRewards.add(new Pair<>(shot, bestParams));
            }
        }

        return shotRewards;
    }

    public static Triplet<Double, ShotStep, ShotParams> monteCarloTreeSearch(TableState tableState, int depth, int numSamples, ShotParamsGenerator shotParamsGenerator, Table.PlayerPattern playerPattern) {
        ArrayList<Pair<ShotStep, ShotParams>> shots = shotParamsGenerator.generateShotParams();

        if (shots.isEmpty()) {
            return null;
        }

        double bestRating = -1000;
        ShotParams bestParams = new ShotParams(0, 0, 0, 0, 0);
        ShotStep bestShotStep = shots.getFirst().getValue0();

        for (Pair<ShotStep, ShotParams> shotParams : shots) {
            double rating = 0;

            for (int i = 0; i < numSamples; i++) {
                TableState tableStateCopy = new TableState(tableState);
                ShotParams noisyParams = new ShotParams(shotParams.getValue1());
                GaussianNoise noise = new GaussianNoise(0.5);
                noise.applyNoise(noisyParams);

                Shot shotEvent;
                try {
                    shotEvent = tableStateCopy.executeShot(noisyParams);
                } catch (Exception ignored) {
                    continue;
                }

                if (isSuccessShot(shotEvent, playerPattern, new TableState(tableState))) {
                    if (depth == 1) {
                        rating++;
                    } else {
                        rating += monteCarloTreeSearch(tableState, depth - 1, numSamples, shotParamsGenerator, playerPattern).getValue0();
                    }
                }
            }

            rating /= numSamples;
            if (rating > bestRating) {
                bestRating = rating;
                bestShotStep = shotParams.getValue0();
                bestParams = shotParams.getValue1();
            }
        }

        return new Triplet<>(bestRating, bestShotStep, bestParams);
    }

    public static double rewardShotSimple(Shot shotEvent, Table.PlayerPattern playerPattern, TableState tableStateBefore) {
        Table table = Table.fromTableState(tableStateBefore);

        int numFriendlyPocketed = 0;
        boolean unfriendlyPocketed = false;
        boolean illegalShot = false;
        boolean foundFirstCollision = false;

        EventVector events = shotEvent.getEventList();
        for (Event event : events) {
            Ball b1 = event.getBall1Data();
            Ball b2 = event.getBall2Data();

            if (b1.isPocketed()) {
                if (isFriendlyBall(b1, table, playerPattern)) {
                    numFriendlyPocketed++;
                } else {
                    unfriendlyPocketed = true;
                    break;
                }
            }

            if (b2.isPocketed()) {
                if (isFriendlyBall(b2, table, playerPattern)) {
                    numFriendlyPocketed++;
                } else {
                    unfriendlyPocketed = true;
                    break;
                }
            }

            if (!foundFirstCollision && event.getType() == Event.Type.BALL_COLLISION) {
                foundFirstCollision = true;
                illegalShot = !isFriendlyBall(event.getBall2Data(), table, playerPattern);
            }
        }

        if (unfriendlyPocketed || illegalShot) {
            return -1;
        }

        return numFriendlyPocketed * 0.1;
    }

    private static boolean isFriendlyBall(Ball b, Table table, Table.PlayerPattern playerPattern) {
        Ball.Type bType = b.getID();

        if (bType == Ball.Type.CUE){
            return false;
        }

        boolean onlyEightBallLeft = true;
        for (org.CueCraft.Pool.Ball ob : table.balls) {
            if (ob.number != 0 && ob.number != 8 && ob.state == 1 && (ob.pattern == playerPattern || playerPattern == Table.PlayerPattern.NONE)) {
                onlyEightBallLeft = false;
                break;
            }
        }

        if (bType == Ball.Type.EIGHT) {
            return onlyEightBallLeft;
        }

        return org.CueCraft.Pool.Ball.fromJBall(b).pattern == playerPattern || playerPattern == Table.PlayerPattern.NONE;
    }

    public static boolean isSuccessShot(Shot shotEvent, Table.PlayerPattern playerPattern, TableState tableStateBefore) {
        return rewardShotSimple(shotEvent, playerPattern, tableStateBefore) > 0;
    }

}
