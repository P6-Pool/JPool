package org.CueCraft.ShotEvaluator;

import JFastfiz.*;
import org.CueCraft.Pool.Pocket;
import org.CueCraft.Pool.Table;
import org.CueCraft.ShotGenerator.ShotStep;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class ShotEvaluator {

    public interface ShotDecider {
        Triplet<Double, ShotStep, ShotParams> decideShot(TableState table, Table.PlayerPattern playerPattern);
    }

    public interface ShotParamsGenerator {
        ArrayList<Pair<ShotStep, ShotParams>> generateShotParams(TableState table, Table.PlayerPattern playerPattern);
    }

    public interface ShotGenerator {
        ArrayList<ShotStep> generateShots(TableState table, Table.PlayerPattern playerPattern);
    }

    public interface ShotRewarder {
        double rewardShot(ShotStep shot, Shot shotEvent, Table.PlayerPattern playerPattern, TableState tableStateBefore);
    }

    public static ArrayList<Pair<ShotStep, ShotParams>> shotVelocitySampling(TableState tableState, Table.PlayerPattern playerPattern, int numSamples, ShotGenerator shotGenerator, ShotRewarder shotRewarder) {
        ArrayList<ShotStep> shots = shotGenerator.generateShots(tableState, playerPattern);
        ArrayList<Pair<ShotStep, ShotParams>> shotRewards = new ArrayList<>();
        ShotParams params = new ShotParams(0, 0, 20, 0, 0);

        for (ShotStep shot : shots) {
            double bestVelocity = 1;
            double bestReward = -1;
            double angle = shot.getShotAngle();
            params.setPhi(angle);

            for (int i = 0; i < numSamples; i++) {
                double velocity = 0.1 + (double) i / (double) numSamples * 9.4;
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
                double reward = shotRewarder.rewardShot(shot, shotEvent, playerPattern, new TableState(tableState));

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

    public static Triplet<Double, ShotStep, ShotParams> monteCarloTreeSearch(TableState tableState, Table.PlayerPattern playerPattern, int depth, int numSamples, ShotParamsGenerator shotParamsGenerator) {
        ArrayList<Pair<ShotStep, ShotParams>> shots = shotParamsGenerator.generateShotParams(tableState, playerPattern);

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
                ShotStep shot = shotParams.getValue0();
                GaussianNoise noise = new GaussianNoise(0.1);
                noise.applyNoise(noisyParams);

                Shot shotEvent;
                try {
                    shotEvent = tableStateCopy.executeShot(noisyParams);
                } catch (Exception ignored) {
                    continue;
                }

                if (isSuccessShot(shot, shotEvent, playerPattern, new TableState(tableState))) {
                    if (depth == 1) {
                        rating++;
                    } else {
                        rating += monteCarloTreeSearch(tableState, playerPattern, depth - 1, numSamples, shotParamsGenerator).getValue0();
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

    public static double rewardShotSimple(ShotStep shot, Shot shotEvent, Table.PlayerPattern playerPattern, TableState tableStateBefore) {
        //TODO ensure that after break we aim for the type with fewest balls

        Table table = Table.fromTableState(tableStateBefore);

        ArrayList<Pair<Ball, JFastfiz.Table.Pocket>> pocketedBalls = getPocketedBalls(shotEvent);
        Ball firstBallTouched = getFirstBallTouched(shotEvent);

        int numFriendlyPocketed = 0;
        int numOpponentPocketed = 0;
        boolean illegalBallPocketed = false;
        boolean firstTouchFriendly = false;
        boolean pocketedDesiredBallFirstInDesiredHole = false;

        if (firstBallTouched != null && isFriendlyBall(firstBallTouched, table, playerPattern)) {
            firstTouchFriendly = true;
        }

        for (int i = 0; i < pocketedBalls.size(); i++) {
            Ball b = pocketedBalls.get(i).getValue0();
            JFastfiz.Table.Pocket p = pocketedBalls.get(i).getValue1();

            if (i == 0 && b.getID().swigValue() == shot.getPocketedBallNumber() && p == Pocket.fromPocketType(shot.pocket)) {
                pocketedDesiredBallFirstInDesiredHole = true;
            }

            if (isFriendlyBall(b, table, playerPattern)) {
                numFriendlyPocketed++;
            } else if (isOpponentBall(b, table, playerPattern)) {
                numOpponentPocketed++;
            } else {
                illegalBallPocketed = true;
            }
        }

        if (illegalBallPocketed || !firstTouchFriendly || !pocketedDesiredBallFirstInDesiredHole) {
            return -1;
        }

        return numFriendlyPocketed - (numOpponentPocketed * 0.5);
    }

    private static ArrayList<Pair<Ball, JFastfiz.Table.Pocket>> getPocketedBalls(Shot shotEvent) {
        ArrayList<Pair<Ball, JFastfiz.Table.Pocket>> pocketedBalls = new ArrayList<>();

        EventVector events = shotEvent.getEventList();
        for (Event event : events) {

            if (event.getType() == Event.Type.POCKETED) {
                PocketedEvent pe = fastfiz.eventToPocketedEvent(event);
                pocketedBalls.add(new Pair<>(event.getBall1Data(), pe.getPocket()));
            }

        }

        return pocketedBalls;
    }

    private static Ball getFirstBallTouched(Shot shotEvent) {
        EventVector events = shotEvent.getEventList();
        for (Event event : events) {
            if (event.getType() == Event.Type.BALL_COLLISION) {
                return event.getBall2Data();
            }
        }
        return null;
    }


    private static boolean isFriendlyBall(Ball b, Table table, Table.PlayerPattern playerPattern) {
        Ball.Type bType = b.getID();

        if (bType == Ball.Type.CUE) {
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

    private static boolean isOpponentBall(Ball b, Table table, Table.PlayerPattern playerPattern) {
        Ball.Type bType = b.getID();

        if (playerPattern == Table.PlayerPattern.NONE || bType == Ball.Type.CUE || bType == Ball.Type.EIGHT) {
            return false;
        }

        return org.CueCraft.Pool.Ball.fromJBall(b).pattern != playerPattern;
    }

    public static boolean isSuccessShot(ShotStep shot, Shot shotEvent, Table.PlayerPattern playerPattern, TableState tableStateBefore) {
        return rewardShotSimple(shot, shotEvent, playerPattern, tableStateBefore) > 0;
    }

}
