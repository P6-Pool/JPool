package org.CueCraft.ShotEvaluator;

import JFastfiz.*;
import org.CueCraft.Pool.Table;
import org.CueCraft.ShotGenerator.ShotGenerator;
import org.CueCraft.ShotGenerator.ShotStep;
import org.CueCraft.ShotGenerator.Vector2d;
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
        double rewardShot(Shot shot, Table.PlayerPattern playerPattern);
    }

    public static Triplet<Double, ShotStep, ShotParams> getBestShot(Table table, int depth, Table.PlayerPattern playerPattern, ShotDecider shotDecider, ShotRewarder shotRewarder) {
        TableState tableState = table.toTableState();
        ShotGenerator sg = () -> org.CueCraft.ShotGenerator.ShotGenerator.generateShots(table, depth, playerPattern);
        ShotParamsGenerator spg = () -> shotVelocitySampling(tableState, 200, playerPattern, sg, shotRewarder);
        return shotDecider.decideShot(spg, tableState, playerPattern);
    }

    public static ArrayList<Pair<ShotStep, ShotParams>> shotVelocitySampling(TableState tableState, int numSamples, Table.PlayerPattern playerPattern, ShotGenerator shotGenerator, ShotRewarder shotRewarder) {
        ArrayList<ShotStep> shots = shotGenerator.generateShots();
        ArrayList<Pair<ShotStep, ShotParams>> shotRewards = new ArrayList<>();
        ShotParams params = new ShotParams(0, 0, 5, 0, 0);

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
                double reward = shotRewarder.rewardShot(shotEvent, playerPattern);

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

        double bestRating = -1000;
        ShotParams bestParams = new ShotParams(0, 0, 0, 0, 0);
        ShotStep bestShotStep = shots.getFirst().getValue0();

        for (Pair<ShotStep, ShotParams> shotParams : shots) {
            double rating = 0;

            for (int i = 0; i < numSamples; i++) {
                TableState tableStateCopy = new TableState(tableState);
                ShotParams noisyParams = new ShotParams(shotParams.getValue1());
                GaussianNoise noise = new GaussianNoise(0.1);
                noise.applyNoise(noisyParams);
                Shot shotEvent = tableStateCopy.executeShot(noisyParams);

                if (isSuccessShot(shotEvent, playerPattern)) {
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

    public static double rewardShotSimple(Shot shotEvent, Table.PlayerPattern playerPattern) {
        EventVector events = shotEvent.getEventList();

        Predicate<Ball.Type> isFriendlyBall = (Ball.Type bType) -> bType.swigValue() < 8 && playerPattern == Table.PlayerPattern.SOLID || bType.swigValue() > 8 && playerPattern == Table.PlayerPattern.STRIPED;

        int numFriendlyPocketed = 0;
        boolean enemyPocketed = false;
        boolean whitePocketed = false;
        boolean illegalShot = false;

        for (Event event : events) {
            if (event.getBall1Data().isPocketed() && isFriendlyBall.test((event.getBall1Data().getID()))) {
                numFriendlyPocketed++;
            }

            if (event.getBall2Data().isPocketed() && isFriendlyBall.test((event.getBall2Data().getID()))) {
                numFriendlyPocketed++;
            }

            if (event.getBall1Data().isPocketed() && !isFriendlyBall.test((event.getBall1Data().getID()))) {
                enemyPocketed = true;
            }

            if (event.getBall2Data().isPocketed() && !isFriendlyBall.test((event.getBall2Data().getID()))) {
                enemyPocketed = true;
            }
        }

        if (enemyPocketed || whitePocketed  || illegalShot) {
            return -1;
        }

        return numFriendlyPocketed * 0.1;
    }

    public static boolean isSuccessShot(Shot shotEvent, Table.PlayerPattern playerPattern) {
        return rewardShotSimple(shotEvent, playerPattern) > 0;
    }

}
