package CueZone;

import org.CueCraft.Grpc.Client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public record ArenaStat(
        GameParams params,
        ArrayList<GameSummary> summaries,
        int numGamesPlayed,
        int numWinsPlayer1,
        int numWinsPlayer2,
        Map<Game.WinType, Integer> winTypesPlayer1,
        Map<Game.WinType, Integer> winTypesPlayer2,
        double avgTimePerGame,
        double arenaTotalTime
) {
    @Override
    public String toString() {
        String winRatePlayer1 = String.format(Locale.US, "%.2f", (double) numWinsPlayer1 / (double) numGamesPlayed * 100.0);
        String winRatePlayer2 = String.format(Locale.US, "%.2f", (double) numWinsPlayer2 / (double) numGamesPlayed * 100.0);
        String avgGameTime = String.format(Locale.US, "%.2f", avgTimePerGame);
        String arenaTime = String.format(Locale.US, "%.2f", arenaTotalTime);


        StringBuilder sb = new StringBuilder();

        // Append params and summaries
        sb.append("Params: ").append(params).append("\n");

        // Append basic stats
        sb.append("Number of games played: ").append(numGamesPlayed).append("\n");
        sb.append("Number of wins for Player 1: ").append(numWinsPlayer1).append(" (").append(winRatePlayer1).append("%)\n");
        sb.append("Number of wins for Player 2: ").append(numWinsPlayer2).append(" (").append(winRatePlayer2).append("%)\n");
        sb.append("Average time per game: ").append(avgGameTime).append("s\n");
        sb.append("Arena time: ").append(arenaTime).append("s\n\n");

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
            sb.append("|");
            sb.append("▓".repeat(Math.max(0, count)));
            sb.append(" ").append(count).append("\n");
        }
        return sb.toString();
    }

    public void logToFile(String outDirPath) {
        FileLogger logger = new FileLogger(outDirPath);

        String pattern = "MM-dd-yyyy-HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr = simpleDateFormat.format(new Date());

        String fileName = dateStr + "-"
                + this.params.player1().getName() + "-"
                + this.params().player2().getName() + "-"
                + this.numGamesPlayed() + "-"
                + this.params().noiseMag();

        logger.log(fileName, this.toString());
    }

    public void saveSerializedSummariesToFile(ArrayList<GameSummary> summaries, String outDirPath) {
        ArrayList<org.CueCraft.protobuf.Game> serGames = new ArrayList<>();

        for (var summary : summaries) {
            serGames.add(Client.serializeGame(summary));
        }

        org.CueCraft.protobuf.ShowGamesRequest req = org.CueCraft.protobuf.ShowGamesRequest.newBuilder()
                .addAllGames(serGames)
                .build();

        // Serialize the protobuf object to bytes
        byte[] bytes = req.toByteArray();

        // Generate file name
        String pattern = "MM-dd-yyyy-HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr = simpleDateFormat.format(new Date());

        String fileName = dateStr + "-"
                + "playlist(" + summaries.size() + ") - "
                + this.params().player1().getName() + "-"
                + this.params().player2().getName() + "-"
                + this.numGamesPlayed() + "-"
                + this.params().noiseMag();

        // Write the bytes to a file
        try (FileOutputStream outputStream = new FileOutputStream(outDirPath + "/" + fileName + ".proto")) {
            outputStream.write(bytes);
            System.out.println("Protobuf object saved to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<GameSummary> getTopGames(int maxNumGames) {
        ArrayList<GameSummary> topGames = new ArrayList<>();

        summaries.sort(Comparator.comparingInt(a -> a.turnHistory().size()));

        for (GameSummary summary : summaries) {
            if (summary.winner() == summary.gameParams().player1() && summary.wonBy() == Game.WinType.WON_BY_POCKETING_EIGHTBALL) {
                topGames.add(summary);
            }
            if (topGames.size() == maxNumGames) {
                break;
            }
        }

        return  topGames;
    }

}
