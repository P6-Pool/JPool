package org.CueCraft.Agent;

import CueZone.Agent;
import JFastfiz.*;
import org.CueCraft.Pool.Table;
import org.CueCraft.ShotEvaluator.ShotEvaluator;
import org.CueCraft.ShotGenerator.ShotGenerator;
import org.CueCraft.ShotGenerator.ShotStep;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.Random;

public class CueCraft implements Agent {
    String name;
    ShotEvaluator.ShotDecider shotDecider;

    public CueCraft(String name, int shotDepth, int numVelocitySamples, int monteCarloDepth, int numMonteCarloSamples) {
        this.name = name + "-" + shotDepth + "-" + numVelocitySamples + "-" + monteCarloDepth + "-" + numMonteCarloSamples;

        ShotEvaluator.ShotGenerator shotGenerator = (tableState, pattern) -> ShotGenerator.generateShots(tableState, pattern, shotDepth);
        ShotEvaluator.ShotParamsGenerator shotParamsGenerator = (tableState, pattern) -> ShotEvaluator.shotVelocitySampling(tableState, pattern, numVelocitySamples, shotGenerator, ShotEvaluator::rewardShotSimple);
        this.shotDecider = (tableState, pattern) -> ShotEvaluator.monteCarloTreeSearch(tableState, pattern, monteCarloDepth, numMonteCarloSamples, shotParamsGenerator);
    }

    @Override
    public String getName() {
        return "CueCraft-" + name;
    }

    @Override
    public Triplet<ShotParams, Vector, Decision> getBreakShot() {
        ShotParams sp = new ShotParams(2.122, 4.795, 21.027, 300.7421, 4.354);
        Vector vector = new Vector();
        vector.setX(0.756);
        vector.setY(1.808);
        return new Triplet<>(sp, vector, Decision.DEC_NO_DECISION);
    }

    @Override
    public Quartet<ShotParams, Ball.Type, JFastfiz.Table.Pocket, Decision> getShot(GameState gameState) {
        Table.PlayerPattern pattern = gameState.isOpenTable() ? Table.PlayerPattern.NONE : gameState.playingSolids() ? Table.PlayerPattern.SOLID : Table.PlayerPattern.STRIPED;
        TableState tableState = gameState.tableState();

        Triplet<Double, ShotStep, ShotParams> shotDetails = shotDecider.decideShot(tableState, pattern);

        if (shotDetails == null) {
//            return getRandomShot();
            return new Quartet<>(new ShotParams(), Ball.Type.UNKNOWN_ID, JFastfiz.Table.Pocket.UNKNOWN_POCKET, Decision.DEC_CONCEDE);
        }

        ShotParams sp = shotDetails.getValue2();
        JFastfiz.Table.Pocket pocket = switch (shotDetails.getValue1().pocket) {
            case NE_Pocket -> JFastfiz.Table.Pocket.NE;
            case E_Pocket -> JFastfiz.Table.Pocket.E;
            case SE_Pocket -> JFastfiz.Table.Pocket.SE;
            case SW_Pocket -> JFastfiz.Table.Pocket.SW;
            case W_Pocket -> JFastfiz.Table.Pocket.W;
            case NW_Pocket -> JFastfiz.Table.Pocket.NW;
        };
        Ball.Type ball = Ball.Type.swigToEnum(shotDetails.getValue1().getPocketedBallNumber());

        return new Quartet<>(sp, ball, pocket, Decision.DEC_NO_DECISION);
    }

    @Override
    public Pair<Vector, Decision> getBallInHandPlacement(GameState gameState) {
        Random rand = new Random();

        double minX = org.CueCraft.Pool.Ball.radius;
        double maxX = Table.width - org.CueCraft.Pool.Ball.radius;

        double minY = org.CueCraft.Pool.Ball.radius;
        double maxY = Table.length - org.CueCraft.Pool.Ball.radius;

        while (true) {
            double queX = rand.nextDouble(minX, maxX);
            double queY = rand.nextDouble(minY, maxY);

            gameState.tableState().setBall(Ball.Type.CUE, Ball.State.STATIONARY, queX, queY);
            if (gameState.tableState().isValidBallPlacement() == TableState.OK_PRECONDITION) {
                Vector vec = new Vector();
                vec.setX(queX);
                vec.setY(queY);
                return new Pair<>(vec, Decision.DEC_NO_DECISION);
            }
        }
    }

    @Override
    public Pair<Vector, Decision> getBallBehindLinePlacement(GameState gameState) {
        Random rand = new Random();

        double minX = org.CueCraft.Pool.Ball.radius;
        double maxX = Table.width - org.CueCraft.Pool.Ball.radius;

        double minY = org.CueCraft.Pool.Ball.radius;
        double maxY = gameState.tableState().getTable().getHeadString() - org.CueCraft.Pool.Ball.radius;

        while (true) {
            double queX = rand.nextDouble(minX, maxX);
            double queY = rand.nextDouble(minY, maxY);

            gameState.tableState().setBall(Ball.Type.CUE, Ball.State.STATIONARY, queX, queY);
            if (gameState.tableState().isValidBallPlacement() == TableState.OK_PRECONDITION) {
                Vector vec = new Vector();
                vec.setX(queX);
                vec.setY(queY);
                return new Pair<>(vec, Decision.DEC_NO_DECISION);
            }
        }
    }

    private Triplet<ShotParams, Ball.Type, JFastfiz.Table.Pocket> getRandomShot() {
        ShotParams sp = new ShotParams(0, 0, 10, 10, 1);
        Ball.Type bt = Ball.Type.SEVEN;
        JFastfiz.Table.Pocket pocket = JFastfiz.Table.Pocket.NE;
        return new Triplet<>(sp, bt, pocket);
    }
}
