package org.CueCraft;

import CueZone.*;
import org.CueCraft.Agent.CueCraft;
import org.CueCraft.Grpc.Client;
import org.CueCraft.Pool.Ball;
import org.CueCraft.Pool.Table;
import org.CueCraft.ShotGenerator.ShotGenerator;
import org.CueCraft.ShotGenerator.Vector2d;
import cz.adamh.utils.NativeUtils;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    static {
        try {
            NativeUtils.loadLibraryFromJar("/lib/libfastfiz.so");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        double noiseMag = 0.5;

//        Agent player1 = new CueCraft("William", noiseMag, 3, 50, 1, 100);
//        Agent player2 = new CueConcede();
//
//        GameParams params = new GameParams(player1, player2, noiseMag, 0, 0);
//
//        ArenaStat stats = Arena.pvpAsync(params, 1000, 10);
//        stats.logToFile("logs");
//
//        ArrayList<GameSummary> topGames = stats.getTopGames(10);
//        stats.saveSerializedSummariesToFile(topGames, "highlights");

//        ArrayList<Ball> balls = new ArrayList<>(){{
//            add(new Ball(0, 1, new Vector2d(0.7884,Table.length - 1.86736)));
//            add(new Ball(1, 1, new Vector2d(0.90022,Table.length - 1.9632)));
//            add(new Ball(2, 1, new Vector2d(1.01170,Table.length - 2.00736)));
//        }};

//        ArrayList<Ball> balls = new ArrayList<>(){{
//            add(new Ball(0, 1, new Vector2d(0.65,Table.length / 2)));
//            add(new Ball(1, 1, new Vector2d(0.90022,Table.length / 2)));
//            add(new Ball(9, 1, new Vector2d(1.01170,Table.length / 2 + 0.045)));
//        }};

//        ArrayList<Ball> balls = new ArrayList<>(){{
//            add(new Ball(0, 1, new Vector2d(0.65,Table.length / 2)));
//            add(new Ball(1, 1, new Vector2d(0.90022,Table.length / 2)));
//            add(new Ball(10, 1, new Vector2d(1.01170,Table.length / 2 - 0.053)));
//        }};

//        ArrayList<Ball> balls = new ArrayList<>(){{
//            add(new Ball(0, 1, new Vector2d(0.78,Table.length - 2.0)));
//            add(new Ball(1, 1, new Vector2d(0.95,Table.length - 2.07)));
//            add(new Ball(10, 1, new Vector2d(0.99,Table.length - 2.19)));
//        }};
//
//        ArrayList<Ball> balls = new ArrayList<>(){{
//            add(new Ball(0, 1, new Vector2d(0.58,Table.length - 2.0)));
//            add(new Ball(1, 1, new Vector2d(0.95,Table.length - 2.07)));
//        }};


//        Table t = new Table(balls);
//
        Table t = Table.randomTableState(10, 1);

        Client client = new Client("localhost", 50051);
        Client client2 = new Client("localhost", 50052);
        client.showShots(ShotGenerator.generateShots(t.toTableState(), Table.PlayerPattern.SOLID, 2), t);
        client2.showShots(ShotGenerator.generateShots(t.toTableState(), Table.PlayerPattern.SOLID, 2), t);
    }
}
