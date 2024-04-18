package CueZone;

import JFastfiz.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class Game {
    public enum WinType {WON_BY_POCKETING_EIGHTBALL, WON_BY_OPPONENT_POCKETING_EIGHTBALL, WON_BY_OPPONENT_CONCEDING, WON_BY_OPPONENT_TIMING_OUT, WON_BY_OPPONENT_BAD_PARAMS}
    GameParams gameParams;
    EightBallState gameState;
    Noise noise;
    ArrayList<Turn> turnHistory;
    Agent activeAgent;
    RealTimeStopwatch shotStopwatch, gameStopwatch;

    public Game(GameParams gameParams) {
        this.gameParams = gameParams;
        this.gameState = new EightBallState(gameParams.timePlayer1(), gameParams.timePlayer2());
        this.noise = new GaussianNoise(gameParams.noiseMag());
        this.turnHistory = new ArrayList<>();
        this.activeAgent = gameParams.player1();
        this.shotStopwatch = new RealTimeStopwatch();
        this.gameStopwatch = new RealTimeStopwatch();
    }

    public GameSummary play() {
        gameStopwatch.restart();
        while (!gameState.isTerminal()) {
            TurnType turnType = gameState.getTurnType();

            GameShot gs = switch (turnType.toString()) {
                case "TT_BREAK" -> handleBreak();
                case "TT_NORMAL" -> handleNormal();
                case "TT_BALL_IN_HAND" -> handleBallInHand();
                case "TT_BEHIND_LINE" -> handleBehindLine();
                case "TT_EIGHTBALL_FOUL_ON_BREAK" -> handleFoulOnBreak();
                case "TT_EIGHTBALL_8BALL_POCKETED_ON_BREAK" -> handleEightBallPocketedOnBreak();
                default -> throw new IllegalStateException("Unexpected value: " + gameState.getTurnType().toString());
            };

            ShotParams params = gs.getParams();
            noise.applyNoise(params);
            gs.setParams(params);

            TableState tableStateBeforeShot = getGameStateCopy().tableState();

            if (gameState.positionRequired()) {
                tableStateBeforeShot.setBall(Ball.Type.CUE, Ball.State.STATIONARY, gs.getCue_x(), gs.getCue_y());
            }

            ShotResult sr = gameState.executeShot(gs);
            TableState tableStateAfterShot = getGameStateCopy().tableState();

            turnHistory.add(new Turn(turnType, activeAgent, tableStateBeforeShot, tableStateAfterShot, gs, sr));

            if (sr == ShotResult.SR_OK_LOST_TURN) {
                activeAgent = activeAgent == gameParams.player1() ? gameParams.player2() : gameParams.player1();
            }

            if (sr == ShotResult.SR_BAD_PARAMS || sr == ShotResult.SR_SHOT_IMPOSSIBLE) {
                activeAgent = activeAgent == gameParams.player1() ? gameParams.player2() : gameParams.player1();
                return new GameSummary(gameParams, WinType.WON_BY_OPPONENT_BAD_PARAMS, activeAgent, turnHistory, gameStopwatch.getElapsed());
            }

            if (gameState.getTurnType() == TurnType.TT_WIN) {
                WinType wonBy = getWinType(gs, sr);
                return new GameSummary(gameParams, wonBy, activeAgent, turnHistory, gameStopwatch.getElapsed());
            }

        }

        return null;
    }

    private static WinType getWinType(GameShot gs, ShotResult sr) {
        WinType wonBy;
        if (gs.getDecision() == Decision.DEC_CONCEDE) {
            wonBy = WinType.WON_BY_OPPONENT_CONCEDING;
        } else if (sr == ShotResult.SR_OK) {
            wonBy = WinType.WON_BY_POCKETING_EIGHTBALL;
        } else if (sr == ShotResult.SR_OK_LOST_TURN) {
            wonBy = WinType.WON_BY_OPPONENT_POCKETING_EIGHTBALL;
        } else {
            wonBy = WinType.WON_BY_OPPONENT_TIMING_OUT;
        }
        return wonBy;
    }

    public GameState getGameStateCopy() {
        return EightBallState.Factory(gameState.toString());
    }

    public GameShot handleBreak() {
        shotStopwatch.restart();
        Triplet<ShotParams, Vector, Decision> sp = activeAgent.getBreakShot();
        double timeSpent = shotStopwatch.getElapsed();

        GameShot gs = new GameShot();
        gs.setParams(sp.getValue0());
        gs.setDecision(sp.getValue2());
        gs.setCue_x(sp.getValue1().getX());
        gs.setCue_y(sp.getValue1().getY());
        gs.setTimeSpent(timeSpent);
        return gs;
    }

    public GameShot handleNormal() {
        shotStopwatch.restart();
        Quartet<ShotParams, Ball.Type, Table.Pocket, Decision> sp = activeAgent.getShot(getGameStateCopy());
        double timeSpent = shotStopwatch.getElapsed();

        GameShot gs = new GameShot();
        gs.setParams(sp.getValue0());
        gs.setDecision(sp.getValue3());
        gs.setBall(sp.getValue1());
        gs.setPocket(sp.getValue2());
        gs.setTimeSpent(timeSpent);
        return gs;
    }

    public GameShot handleBallInHand() {
        shotStopwatch.restart();
        Pair<Vector, Decision> response = activeAgent.getBallInHandPlacement(getGameStateCopy());
        return getHandShot(response);
    }

    public GameShot handleBehindLine() {
        shotStopwatch.restart();
        Pair<Vector, Decision> response = activeAgent.getBallBehindLinePlacement(getGameStateCopy());
        return getHandShot(response);
    }

    private GameShot getHandShot(Pair<Vector, Decision> response) {
        Vector newCuePos = response.getValue0();

        Quartet<ShotParams, Ball.Type, Table.Pocket, Decision> sp;

        if (response.getValue1() == Decision.DEC_CONCEDE) {
            sp = new Quartet<>(new ShotParams(), Ball.Type.UNKNOWN_ID, Table.Pocket.UNKNOWN_POCKET, Decision.DEC_CONCEDE);
        } else {
            GameState tempGs = getGameStateCopy();
            tempGs.tableState().setBall(Ball.Type.CUE, Ball.State.STATIONARY, newCuePos.getX(), newCuePos.getY());
            sp = activeAgent.getShot(tempGs);
        }
        double timeSpent = shotStopwatch.getElapsed();

        GameShot gs = new GameShot();

        gs.setParams(sp.getValue0());
        gs.setDecision(response.getValue1());
        gs.setBall(sp.getValue1());
        gs.setPocket(sp.getValue2());
        gs.setCue_x(newCuePos.getX());
        gs.setCue_y(newCuePos.getY());
        gs.setTimeSpent(timeSpent);
        return gs;
    }

    public GameShot handleFoulOnBreak() {
        GameShot gs = new GameShot();
        gs.setDecision(Decision.DEC_RERACK);
        return gs;
    }

    public GameShot handleEightBallPocketedOnBreak() {
        GameShot gs = new GameShot();
        gs.setDecision(Decision.DEC_RERACK);
        return gs;
    }

}
