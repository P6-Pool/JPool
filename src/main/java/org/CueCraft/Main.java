package org.CueCraft;

import CueZone.Agent;
import CueZone.Game;
import JFastfiz.*;
import org.CueCraft.Agent.CueCraft;
import org.CueCraft.Grpc.Client;


public class Main {
    static {
        String workingDir = System.getProperty("user.dir");
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    public static void main(String[] args) {
        Agent player1 = new CueCraft("William", 3, 1, 50);
        Agent player2 = new CueCraft("Mikkel", 3, 1, 50);
        GaussianNoise gn = new GaussianNoise(0);

        Game game = new Game(player1, player2, gn, 0, 0);
        game.play();

        Client client = new Client("localhost", 50051);
        client.showGame(game.getTurnHistory());
    }
}
