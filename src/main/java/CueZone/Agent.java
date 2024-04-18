package CueZone;

import JFastfiz.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

public interface Agent {

    Triplet<ShotParams, Vector, Decision> getBreakShot();

    Quartet<ShotParams, Ball.Type, Table.Pocket, Decision> getShot(GameState gameState);

    Pair<Vector, Decision> getBallInHandPlacement(GameState gameState);

    Pair<Vector, Decision> getBallBehindLinePlacement(GameState gameState);

    String getName();

}
