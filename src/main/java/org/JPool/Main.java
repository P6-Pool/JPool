package org.JPool;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;
import org.JPool.Grpc.Client;
import org.JPool.JGeometry.ShotTree;
import org.JPool.JGeometry.Vector2d;

import java.util.ArrayList;


public class Main
{
    public static void main( String[] args )
    {
        Client client = new Client("localhost", 50051);

        ArrayList<Ball> balls = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            balls.add(new Ball(i, 1, new Vector2d(TableState.width / 2, TableState.length / 18 * (i + 1))));
        }
        TableState oneBallState = new TableState(new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));}});
        TableState TwoBallState = new TableState(new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));add(balls.get(2));}});
        TableState ThreeBallState = new TableState(new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));add(balls.get(2));add(balls.get(3));}});
        TableState FourBallState = new TableState(new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));add(balls.get(2));add(balls.get(3));add(balls.get(4));}});
        TableState fiveBallState = new TableState(new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));add(balls.get(2));add(balls.get(3));add(balls.get(4));add(balls.get(5));}});

        TableState noClearPathState = new TableState(
                new ArrayList<>(){{
                    add(new Ball(0, 1, new Vector2d(0.28, 0.28)));
                    add(new Ball(1, 1, new Vector2d(0.2, 0.2)));
                    add(new Ball(2, 1, new Vector2d(0.04, 0.1)));
                }}
        );

        client.showShots(ShotTree.generateShotTree(FourBallState, 5, TableState.playerPattern.SOLID), FourBallState);

    }
}
