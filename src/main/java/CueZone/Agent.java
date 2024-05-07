package CueZone;

import JFastfiz.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

public abstract class Agent {
    AgentConfig config;

    public Agent(AgentConfig config) {
        this.config = config;
    }

    public abstract Triplet<ShotParams, Vector, Decision> getBreakShot();

    public abstract Quartet<ShotParams, Ball.Type, Table.Pocket, Decision> getShot(GameState gameState);

    public abstract Pair<Vector, Decision> getBallInHandPlacement(GameState gameState);

    public abstract Pair<Vector, Decision> getBallBehindLinePlacement(GameState gameState);

    public abstract String getName();
}
