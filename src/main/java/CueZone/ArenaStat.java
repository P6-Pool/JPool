package CueZone;

import org.CueCraft.Grpc.Client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public record ArenaStat(
        GameParams params,
        ArrayList<GameSummary> summaries,
        int numTotalGames,
        int numGamesPlayed,
        int numWinsPlayer1,
        int numWinsPlayer2,
        Map<Game.WinType, Integer> winTypesPlayer1,
        Map<Game.WinType, Integer> winTypesPlayer2,
        double avgTimePerGame,
        double arenaTotalTime
) {
    public double getCompletion() {
        return (double) numGamesPlayed / (double) numTotalGames * 100.0;
    }

    public double getWinRatePlayer1() {
        return (double) numWinsPlayer1 / (double) numGamesPlayed * 100.0;
    }

    public double getWinRatePlayer2() {
        return (double) numWinsPlayer2 / (double) numGamesPlayed * 100.0;
    }

    public double getOffBreakWinRatePlayer1() {
        int offBreakWins = 0;
        for (GameSummary summary : summaries) {
            boolean allTurnsBySamePlayer = summary.turnHistory().stream().allMatch((t) -> t.player() == params.player1());
            boolean gameWonByPocketingEightBall = summary.wonBy() == Game.WinType.WON_BY_POCKETING_EIGHTBALL;
            if (allTurnsBySamePlayer && gameWonByPocketingEightBall) {
                offBreakWins++;
            }
        }
        return (double) offBreakWins / (double) numGamesPlayed * 100.0;
    }

    public double getOffBreakWinRatePlayer2() {
        int offBreakWins = 0;
        for (GameSummary summary : summaries) {
            boolean allTurnsBySamePlayer = summary.turnHistory().stream().allMatch((t) -> t.player() == params.player2());
            boolean gameWonByPocketingEightBall = summary.wonBy() == Game.WinType.WON_BY_POCKETING_EIGHTBALL;
            if (allTurnsBySamePlayer && gameWonByPocketingEightBall) {
                offBreakWins++;
            }
        }
        return (double) offBreakWins / (double) numGamesPlayed * 100.0;
    }

    @Override
    public String toString() {
        String completion = String.format(Locale.US, "%.2f", getCompletion());
        String winRatePlayer1 = String.format(Locale.US, "%.2f", getWinRatePlayer1());
        String winRatePlayer2 = String.format(Locale.US, "%.2f", getWinRatePlayer2());
        String offBreakWinRatePlayer1 = String.format(Locale.US, "%.2f", getOffBreakWinRatePlayer1());
        String offBreakWinRatePlayer2 = String.format(Locale.US, "%.2f", getOffBreakWinRatePlayer2());
        String avgGameTime = String.format(Locale.US, "%.2f", avgTimePerGame);
        String arenaTime = String.format(Locale.US, "%.2f", arenaTotalTime);

        StringBuilder sb = new StringBuilder();

        // Append params and summaries
        sb.append("Params: ").append("\n").append(params).append("\n");

        // Append basic stats
        sb.append("Number of games played: ").append(numGamesPlayed).append(" of ").append(numTotalGames).append(" (").append(completion).append("%)\n");
        sb.append("Number of wins for Player 1: ").append(numWinsPlayer1).append(" (").append(winRatePlayer1).append("% / ").append(offBreakWinRatePlayer1).append("%)\n");
        sb.append("Number of wins for Player 2: ").append(numWinsPlayer2).append(" (").append(winRatePlayer2).append("% / ").append(offBreakWinRatePlayer2).append("%)\n");
        sb.append("Average time per game: ").append(avgGameTime).append("s\n");
        sb.append("Arena time: ").append(arenaTime).append("s\n\n");

        // Append win type statistics as ASCII bar diagrams
        sb.append("Win types for Player 1:\n");
        sb.append(formatWinTypeBarDiagram(winTypesPlayer1, numGamesPlayed)).append("\n");
        sb.append("Win types for Player 2:\n");
        sb.append(formatWinTypeBarDiagram(winTypesPlayer2, numGamesPlayed)).append("\n");

        return sb.toString();
    }

    private String formatWinTypeBarDiagram(Map<Game.WinType, Integer> winTypes, int numGamesPlayed) {
        StringBuilder sb = new StringBuilder();

        // Find the maximum count to determine the length of the longest bar
        Game.WinType longestWinTypeStr = Arrays.stream(Game.WinType.values())
                .max(Comparator.comparingInt(val -> val.toString().length()))
                .orElse(Game.WinType.WON_BY_OPPONENT_CONCEDING);

        int totalCount = winTypes.values().stream().mapToInt(Integer::intValue).sum();
        int maxBarLength = 50;

        // Format each win type
        for (Map.Entry<Game.WinType, Integer> entry : winTypes.entrySet()) {
            Game.WinType winType = entry.getKey();
            int count = entry.getValue();

            // Calculate the number of spaces needed to pad the bar
            int padding = longestWinTypeStr.toString().length() + 3 - (winType.toString().length() + 2);

            // Build the bar with padding
            sb.append(winType);
            sb.append(" ".repeat(padding)); // Padding
            sb.append("|");
            sb.append(generateBar(count, numGamesPlayed, maxBarLength));
            sb.append(" ").append(count).append("\n");
        }
        return sb.toString();
    }

    private String generateBar(int count, int totalCount, int maxBarLength) {
        char[] unicodeChars = {'▏', '▎', '▍', '▌', '▋', '▊', '▉', '█'};

        double numChars = (double) count / (double) totalCount * (double) maxBarLength;
        double remainder = numChars - Math.floor(numChars);

        int numFullChars = (int) Math.floor(numChars);
        char remainderChar = unicodeChars[(int) Math.round(remainder * (double) (unicodeChars.length - 1))];

        return "█".repeat(numFullChars) + remainderChar;
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

        logger.log(fileName, this.toString(), ".log");
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
                + params.player1().getName() + "-"
                + params.player2().getName() + "-"
                + numGamesPlayed + "-"
                + params.noiseMag();

        // Write the bytes to a file
        try {
            Files.createDirectories(Paths.get(outDirPath));
            String path = outDirPath + "/" + fileName;
            FileOutputStream outputStream = new FileOutputStream(path);
            outputStream.write(bytes);
            System.out.println("Highlights outputted to: " + path);
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

        return topGames;
    }

}
