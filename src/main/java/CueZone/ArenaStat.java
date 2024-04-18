package CueZone;

import java.util.*;

public record ArenaStat(
        GameParams params,
        ArrayList<GameSummary> summaries,
        int numGamesPlayed,
        int numWinsPlayer1,
        int numWinsPlayer2,
        Map<Game.WinType, Integer> winTypesPlayer1,
        Map<Game.WinType, Integer> winTypesPlayer2,
        double avgTimePerGame
) {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Append params and summaries
        sb.append("Params: ").append(params).append("\n");

        // Append basic stats
        sb.append("Number of games played: ").append(numGamesPlayed).append("\n");
        sb.append("Number of wins for Player 1: ").append(numWinsPlayer1).append("\n");
        sb.append("Number of wins for Player 2: ").append(numWinsPlayer2).append("\n");
        sb.append("Average time per game: ").append(String.format("%.2f", avgTimePerGame)).append("\n\n");

        // Append win type statistics as ASCII bar diagrams
        sb.append("Win types for Player 1:\n");
        sb.append(formatWinTypeBarDiagram(winTypesPlayer1)).append("\n");
        sb.append("Win types for Player 2:\n");
        sb.append(formatWinTypeBarDiagram(winTypesPlayer2)).append("\n");

        return sb.toString();
    }

    private String formatWinTypeBarDiagram(Map<Game.WinType, Integer> winTypes) {
        StringBuilder sb = new StringBuilder();

        // Find the maximum count to determine the length of the longest bar
        Game.WinType longestWintype = Arrays.stream(Game.WinType.values()).max(Comparator.comparingInt(val -> val.toString().length())).orElse(Game.WinType.WON_BY_OPPONENT_CONCEDING);

        // Calculate the length of the longest bar (including the label and count)
        int maxBarLength = longestWintype.toString().length() + 3; // 3 for the label, colon, and space

        // Format each win type
        for (Map.Entry<Game.WinType, Integer> entry : winTypes.entrySet()) {
            Game.WinType winType = entry.getKey();
            int count = entry.getValue();

            // Calculate the number of spaces needed to pad the bar
            int padding = maxBarLength - (winType.toString().length() + 2); // 2 for the colon and space

            // Build the bar with padding
            sb.append(winType);
            sb.append(" ".repeat(padding)); // Padding
            sb.append(": ");
            sb.append("|".repeat(Math.max(0, count)));
            sb.append(" ").append(count).append("\n");
        }
        return sb.toString();
    }

}
