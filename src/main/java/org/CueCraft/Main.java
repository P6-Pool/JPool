package org.CueCraft;

import CueZone.*;
import org.CueCraft.Agent.CueCraft;
import org.CueCraft.Grpc.Client;


public class Main {
    static {
        String workingDir = System.getProperty("user.dir");
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    public static void main(String[] args) {
        String logDir = System.getProperty("user.dir") + "/logs";

        Agent player1 = new CueCraft("William", 3, 1, 50);
        Agent player2 = new CueCraft("Mikkel", 3, 1, 50);

        GameParams params = new GameParams(player1, player2, 0, 0, 0);

        ArenaStat stats = Arena.pvpAsync(params, 500, 5);
        Arena.LogToFile(stats, logDir);
        System.out.println(stats);

//        Client client = new Client("localhost", 50051);
//        client.showGame(summary);
    }
}
