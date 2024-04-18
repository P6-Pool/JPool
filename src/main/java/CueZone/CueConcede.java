package CueZone;

import JFastfiz.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

public class CueConcede implements Agent{
    @Override
    public Triplet<ShotParams, Vector, Decision> getBreakShot() {
        return new Triplet<>(new ShotParams(), new Vector(), Decision.DEC_EIGHTBALL_RERACK_OPP_SHOOT);
    }

    @Override
    public Quartet<ShotParams, Ball.Type, Table.Pocket, Decision> getShot(GameState gameState) {
        return new Quartet<>(new ShotParams(), Ball.Type.UNKNOWN_ID, Table.Pocket.UNKNOWN_POCKET, Decision.DEC_CONCEDE);
    }

    @Override
    public Pair<Vector, Decision> getBallInHandPlacement(GameState gameState) {
        return new Pair<>(new Vector(), Decision.DEC_CONCEDE);
    }

    @Override
    public Pair<Vector, Decision> getBallBehindLinePlacement(GameState gameState) {
        return new Pair<>(new Vector(), Decision.DEC_CONCEDE);
    }

    @Override
    public String getName() {
        return "CueConcede";
    }
}
