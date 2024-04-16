package CueZone;

import JFastfiz.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class Game {
    Agent player1, player2, activeAgent;
    Noise noise;
    EightBallState gameState;
    ArrayList<Quartet<String, TableState, GameShot, ShotResult>> turnHistory = new ArrayList<>();

    public Game(Agent player1, Agent player2, Noise noise, int timePlayer1, int timePlayer2) {
        this.player1 = player1;
        this.player2 = player2;
        this.activeAgent = player1;
        this.noise = noise;
        this.gameState = new EightBallState(timePlayer1, timePlayer2);
    }

    public void play() {
        while (!gameState.isTerminal()) {
            GameState gameStateBeforeShot = getGameStateCopy();

            GameShot gs = switch (gameState.getTurnType().toString()) {
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

            ShotResult sr = gameState.executeShot(gs);

            if (sr == ShotResult.SR_OK_LOST_TURN) {
                activeAgent = activeAgent == player1 ? player2 : player1;
            }

            turnHistory.add(new Quartet<>(activeAgent.getName(), gameStateBeforeShot.tableState(), gs, sr));
        }
    }

    public ArrayList<Quartet<String, TableState, GameShot, ShotResult>> getTurnHistory() {
        return turnHistory;
    }

    public GameState getGameStateCopy() {
        return EightBallState.Factory(gameState.toString());
    }

    public GameShot handleBreak() {
        Pair<ShotParams, Vector> sp = activeAgent.getBreakShot();

        GameShot gs = new GameShot();
        gs.setParams(sp.getValue0());
        gs.setDecision(Decision.DEC_NO_DECISION);
        gs.setBall(Ball.Type.UNKNOWN_ID);
        gs.setPocket(Table.Pocket.UNKNOWN_POCKET);
        gs.setCue_x(sp.getValue1().getX());
        gs.setCue_y(sp.getValue1().getY());
        gs.setTimeSpent(0);

        System.out.println(activeAgent.getName() + ": Break shot");
        return gs;
    }

    public GameShot handleNormal() {
        Triplet<ShotParams, Ball.Type, Table.Pocket> sp = activeAgent.getShot(getGameStateCopy());

        GameShot gs = new GameShot();

        if (sp == null) {
            gs.setDecision(Decision.DEC_CONCEDE);
            System.out.println(activeAgent.getName() + ": Conceded (no shots generated)");
            return gs;
        }

        gs.setParams(sp.getValue0());
        gs.setDecision(Decision.DEC_NO_DECISION);
        gs.setBall(sp.getValue1());
        gs.setPocket(sp.getValue2());
        gs.setCue_x(0);
        gs.setCue_y(0);
        gs.setTimeSpent(0);

        System.out.println(activeAgent.getName() + ": Normal shot");
        return gs;
    }

    public GameShot handleBallInHand() {
        Vector newCuePos = activeAgent.getBallInHandPlacement(getGameStateCopy());

        GameState tempGs = getGameStateCopy();
        tempGs.tableState().setBall(Ball.Type.CUE, Ball.State.STATIONARY, newCuePos.getX(), newCuePos.getY());

        Triplet<ShotParams, Ball.Type, Table.Pocket> sp = activeAgent.getShot(tempGs);

        GameShot gs = new GameShot();

        if (sp == null) {
            System.out.println(activeAgent.getName() + ": Conceded (no shots generated for ball in hand)");
            gs.setDecision(Decision.DEC_CONCEDE);
            return gs;
        }

        gs.setParams(sp.getValue0());
        gs.setDecision(Decision.DEC_NO_DECISION);
        gs.setBall(sp.getValue1());
        gs.setPocket(sp.getValue2());
        gs.setCue_x(newCuePos.getX());
        gs.setCue_y(newCuePos.getY());
        gs.setTimeSpent(0);

        System.out.println(activeAgent.getName() + ": Ball in hand");
        return gs;
    }

    public GameShot handleBehindLine() {
        Vector newCuePos = activeAgent.getBallBehindLinePlacement(getGameStateCopy());

        GameState tempGs = getGameStateCopy();
        tempGs.tableState().setBall(Ball.Type.CUE, Ball.State.STATIONARY, newCuePos.getX(), newCuePos.getY());

        Triplet<ShotParams, Ball.Type, Table.Pocket> sp = activeAgent.getShot(tempGs);

        GameShot gs = new GameShot();

        if (sp == null) {
            System.out.println(activeAgent.getName() + ": Conceded (no shots generated for behind line)");
            gs.setDecision(Decision.DEC_CONCEDE);
            return gs;
        }

        gs.setParams(sp.getValue0());
        gs.setDecision(Decision.DEC_NO_DECISION);
        gs.setBall(sp.getValue1());
        gs.setPocket(sp.getValue2());
        gs.setCue_x(newCuePos.getX());
        gs.setCue_y(newCuePos.getY());
        gs.setTimeSpent(0);

        System.out.println(activeAgent.getName() + ": Ball behind line");
        return gs;
    }

    public GameShot handleFoulOnBreak() {
        GameShot gs = new GameShot();
        gs.setDecision(Decision.DEC_RERACK);

        System.out.println(activeAgent.getName() + ": Foul on break");
        return gs;
    }

    public GameShot handleEightBallPocketedOnBreak() {
        GameShot gs = new GameShot();
        gs.setDecision(Decision.DEC_RERACK);

        System.out.println(activeAgent.getName() + ": Eightball pocketed on break");
        return gs;
    }

}
