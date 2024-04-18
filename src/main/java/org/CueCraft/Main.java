package org.CueCraft;

import CueZone.*;
import org.CueCraft.Agent.CueCraft;


public class Main {
    private static final String workingDir = System.getProperty("user.dir");

    static {
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    public static void main(String[] args) {
        String logDir = workingDir + "/logs";
        String highlightsDir = workingDir + "/highlights";

        Agent player1 = new CueCraft("William", 3, 50, 1, 50);
        Agent player2 = new CueConcede();

        GameParams params = new GameParams(player1, player2, 0, 0, 0);

        ArenaStat stats = Arena.pvpAsync(params, 100, 10);

        stats.logToFile(logDir);
//        stats.saveTopTen(highlightsDir)

        System.out.println(stats);

//        Client client = new Client("localhost", 50051);
//        client.showGame(summary);
    }
}
