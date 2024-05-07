package CueZone;

import org.CueCraft.Grpc.Client;
import org.javatuples.Pair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public record BenchmarkStat(
        double noiseMag,
        int numRunsPerConfig,
        ArrayList<Pair<Agent, ArenaStat>> results
) {
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Benchmark:");
        sb.append("\nNoise level: ").append(noiseMag);
        sb.append("\nRuns per agent: ").append(numRunsPerConfig);
        sb.append("\nOff break win rate (%) | average time per game (s): \n");

        results.forEach((result) -> {
            Agent agent = result.getValue0();
            ArenaStat stat = result.getValue1();

            String offBreakWinRatePlayer = String.format(Locale.US, "%.2f", stat.getOffBreakWinRatePlayer1());
            String avgTimePerGame = String.format(Locale.US, "%.2f", stat.avgTimePerGame());

            sb.append(offBreakWinRatePlayer).append(" | ");
            sb.append(avgTimePerGame).append(" | ");
            sb.append(agent.config).append("\n");
        });

        sb.append("\n\n");

        results.forEach((pair) -> {
            sb.append("\n").append(pair.getValue1().toString());
        });

        return sb.toString();
    }

    public void logToFile(String outDirPath) {
        FileLogger logger = new FileLogger(outDirPath);

        String pattern = "MM-dd-yyyy-HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr = simpleDateFormat.format(new Date());

        String fileName = dateStr + "-"
                + noiseMag + "-"
                + numRunsPerConfig;

        logger.log(fileName, this.toString(), ".log");
    }

    public void saveSerializedStatsToCSV(String outDirPath) {
        StringBuilder csv = new StringBuilder("agentName,offBreakWin,avgTimePerGame");

        for (Pair<Agent, ArenaStat> result : results) {
            Agent agent = result.getValue0();
            ArenaStat stat = result.getValue1();
            csv.append("\n").append(agent.config).append(",").append(stat.getOffBreakWinRatePlayer1()).append(",").append(stat.avgTimePerGame());
        }

        String pattern = "MM-dd-yyyy-HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr = simpleDateFormat.format(new Date());

        String fileName = dateStr + "-"
                + noiseMag + "-"
                + numRunsPerConfig;

        FileLogger logger = new FileLogger(outDirPath);
        logger.log(fileName, csv.toString(), ".csv");
    }

}


