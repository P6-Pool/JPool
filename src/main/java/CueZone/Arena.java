package CueZone;

import JFastfiz.RealTimeStopwatch;
import JFastfiz.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Arena {
    public static ArenaStat pvpAsync(GameParams gameParams, int numGames, int maxConcurrently) {
        Stopwatch stopwatch = new RealTimeStopwatch();
        stopwatch.restart();

        ArrayList<GameSummary> gameSummaries = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(numGames, maxConcurrently));

        Semaphore concurrencyCap = new Semaphore(maxConcurrently);

        AtomicInteger finished = new AtomicInteger();

        // Create a list to hold the Future objects for each game
        ArrayList<Future<GameSummary>> futures = new ArrayList<>();

        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(arenaStatFromSummaries(gameParams, gameSummaries, stopwatch.getElapsed(), numGames));
        System.out.flush();

        // Submit tasks to the thread pool
        for (int i = 0; i < numGames; i++) {
            Callable<GameSummary> task = () -> {
                try {
                    concurrencyCap.acquire(); // Acquire permit before executing the task
                    Game game = new Game(gameParams);
                    return game.play();
                } finally {
                    finished.getAndIncrement();
                    concurrencyCap.release(); // Release permit after executing the task
                }
            };
            futures.add(executor.submit(task));
        }

        // Wait for all tasks to complete and retrieve the results
        for (Future<GameSummary> future : futures) {
            try {
                gameSummaries.add(future.get());
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println(arenaStatFromSummaries(gameParams, gameSummaries, stopwatch.getElapsed(), numGames));
                System.out.flush();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Shutdown the executor
        executor.shutdown();

        return arenaStatFromSummaries(gameParams, gameSummaries, stopwatch.getElapsed(), numGames);
    }

    public static ArenaStat pvpSync(GameParams gameParams, int numGames) {
        Stopwatch stopwatch = new RealTimeStopwatch();
        stopwatch.restart();

        ArrayList<GameSummary> gameSummaries = new ArrayList<>();

        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(arenaStatFromSummaries(gameParams, gameSummaries, stopwatch.getElapsed(), numGames));
        System.out.flush();

        for (int i = 0; i < numGames; i++) {
            System.out.println("Playing game " + (i + 1) + " of " + numGames);
            Game game = new Game(gameParams);
            gameSummaries.add(game.play());
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println(arenaStatFromSummaries(gameParams, gameSummaries, stopwatch.getElapsed(), numGames));
            System.out.flush();
        }

        return arenaStatFromSummaries(gameParams, gameSummaries, stopwatch.getElapsed(), numGames);
    }

    private static ArenaStat arenaStatFromSummaries(GameParams gameParams, ArrayList<GameSummary> summaries, double arenaTime, int numTotalGames) {
        int numGamesPlayed = summaries.size();
        int numWinsPlayer1 = (int) summaries.stream().filter((sum) -> sum.winner() == gameParams.player1()).count();
        int numWinsPlayer2 = numGamesPlayed - numWinsPlayer1;

        Map<Game.WinType, Integer> winTypesPlayer1 = new HashMap<>();
        Map<Game.WinType, Integer> winTypesPlayer2 = new HashMap<>();

        for (Game.WinType winType : Game.WinType.values()) {
            winTypesPlayer1.put(winType, 0);
            winTypesPlayer2.put(winType, 0);
        }

        for (GameSummary summary : summaries) {
            if (summary.winner() == gameParams.player1()) {
                winTypesPlayer1.put(summary.wonBy(), winTypesPlayer1.get(summary.wonBy()) + 1);
            } else {
                winTypesPlayer2.put(summary.wonBy(), winTypesPlayer2.get(summary.wonBy()) + 1);
            }
        }

        double avgTimePerGame = summaries.stream().mapToDouble(GameSummary::gameLength).sum() / numGamesPlayed;

        return new ArenaStat(gameParams, summaries, numTotalGames ,numGamesPlayed, numWinsPlayer1, numWinsPlayer2, winTypesPlayer1, winTypesPlayer2, avgTimePerGame, arenaTime);
    }
}
