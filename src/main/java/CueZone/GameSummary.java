package CueZone;

import java.util.ArrayList;

public record GameSummary(
        GameParams gameParams,
        Game.WinType wonBy,
        Agent winner,
        ArrayList<Turn> turnHistory,
        double gameLength) {
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < turnHistory.size(); i++) {
            Turn turn = turnHistory.get(i);
            str.append(i + 1).append(") ");
            str.append("Player: ").append(turn.player().getName()).append(", ");
            str.append("Type: ").append(turn.turnType()).append(", ");
            str.append("Type: ").append(turn.shotResult()).append("\n");
        }
        str.append(winner.getName()).append(" won by ").append(switch (wonBy) {
            case WON_BY_POCKETING_EIGHTBALL -> "pocketing the eightball";
            case WON_BY_OPPONENT_POCKETING_EIGHTBALL -> "opponent pocketing the eightball early";
            case WON_BY_OPPONENT_CONCEDING -> "opponent conceding";
            case WON_BY_OPPONENT_TIMING_OUT -> "opponent timing out";
            case WON_BY_OPPONENT_BAD_PARAMS -> "opponent submitted bad params";
        }).append("\n");
        return str.toString();
    }
}
