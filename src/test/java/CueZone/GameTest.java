package CueZone;

import JFastfiz.Ball;
import JFastfiz.EightBallState;
import JFastfiz.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    static {
        String workingDir = System.getProperty("user.dir");
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    @Test
    void getGameStateCopy() {
        GameState gs = new EightBallState(0, 0);
        gs.tableState().setBall(Ball.Type.CUE, Ball.State.NOTINPLAY, 0, 0);

        GameState ngs = EightBallState.Factory(gs.toString());
        Ball b = ngs.tableState().getBall(Ball.Type.CUE);
        b.getState();
    }
}