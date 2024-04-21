package org.CueCraft;

import CueZone.*;
import org.CueCraft.Agent.CueCraft;
import org.CueCraft.Grpc.Client;

import java.util.ArrayList;


public class Main {
    private static final String workingDir = System.getProperty("user.dir");

    static {
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    public static void main(String[] args) {
        String logDir = workingDir + "/logs";
        String highlightsDir = workingDir + "/highlights";

        Agent player1 = new CueCraft("William", 3, 50, 1, 100);
        Agent player2 = new CueConcede();

        GameParams params = new GameParams(player1, player2, 0.5, 0, 0);

        ArenaStat stats = Arena.pvpAsync(params, 10, 10);
        stats.logToFile(logDir);
        System.out.println(stats);

        ArrayList<GameSummary> topGames = stats.getTopGames(10);
        stats.saveSerializedSummariesToFile(topGames, highlightsDir);

        Client client = new Client("localhost", 50051);
        client.showGames(topGames);
    }
}
