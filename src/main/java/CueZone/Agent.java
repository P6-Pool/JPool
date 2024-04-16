package CueZone;

import JFastfiz.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public interface Agent {

    Pair<ShotParams, Vector> getBreakShot();

    Triplet<ShotParams, Ball.Type, Table.Pocket> getShot(GameState gameState);

    Vector getBallInHandPlacement(GameState gameState);

    Vector getBallBehindLinePlacement(GameState gameState);

    String getName();

}
