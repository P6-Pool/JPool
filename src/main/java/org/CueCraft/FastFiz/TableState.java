package org.CueCraft.FastFiz;

import org.CueCraft.Geometry.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableState {
    public final ArrayList<Ball> balls;
    public final Pocket NE_Pocket, E_Pocket, SE_Pocket, SW_Pocket, W_Pocket, NW_Pocket;

    public static double width = 1.116;
    public static double length = 2.236;
    public static double innerWidth = width - 2 * Ball.radius;
    public static double innerLength = length - 2 * Ball.radius;

    public static double cornerPocketWidth = 0.11;
    public static double sidePocketWidth = 0.12;

    public enum playerPattern {STRIPED, SOLID, NONE}

    public TableState(ArrayList<Ball> balls) {
        this.balls = balls;

        double diagonalOffset= Math.sqrt(2) * (cornerPocketWidth / 2) / 2;

        NE_Pocket = new Pocket(new Vector2d(width - diagonalOffset, length - diagonalOffset), Pocket.PocketType.NE_Pocket, cornerPocketWidth);
        E_Pocket = new Pocket(new Vector2d(width, length / 2), Pocket.PocketType.E_Pocket, sidePocketWidth);
        SE_Pocket = new Pocket(new Vector2d(width - diagonalOffset, diagonalOffset), Pocket.PocketType.SE_Pocket, cornerPocketWidth);
        SW_Pocket = new Pocket(new Vector2d(diagonalOffset, diagonalOffset), Pocket.PocketType.SW_Pocket, cornerPocketWidth);
        W_Pocket = new Pocket(new Vector2d(0, length / 2), Pocket.PocketType.W_Pocket, sidePocketWidth);
        NW_Pocket = new Pocket(new Vector2d(diagonalOffset, length - diagonalOffset), Pocket.PocketType.NW_Pocket, cornerPocketWidth);
    }

    public static TableState randomTableState(int numBalls) {
        ArrayList<Ball> balls = new ArrayList<>();
        ArrayList<Integer> ballNums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        Random rand = new Random(numBalls);

        for (int i = 0; i < numBalls; i++) {
            int randIdx = rand.nextInt(ballNums.size());
            int ballNum = ballNums.get(randIdx);
            double yPos = TableState.length / 18 * (i + 1);
            double xPos = Ball.radius + rand.nextDouble() * (TableState.width - Ball.radius * 2);
            balls.add(new Ball(ballNum, 1, new Vector2d(xPos, yPos)));
            ballNums.remove(randIdx);
        }

        ballNums.add(0);
        int randIdx = rand.nextInt(ballNums.size());
        int ballNum = ballNums.get(randIdx);
        double yPos = TableState.length / 18 * (ballNum + 1);
        double xPos = Ball.radius + rand.nextDouble() * (TableState.width - Ball.radius * 2);
        balls.add(new Ball(0, 1, new Vector2d(xPos, yPos)));

        return new TableState(balls);
    }

    public static TableState randomSequentialTableState(int numBalls) {
        Random rand = new Random(numBalls);
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < numBalls + 1; i++) {
            balls.add(new Ball(i, 1, new Vector2d(Ball.radius + rand.nextDouble() * (TableState.width - Ball.radius * 2), TableState.length / 18 * (i + 1))));
        }
        return new TableState(balls);
    }

    public static TableState linedTableState(int numBalls) {
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < numBalls + 1; i++) {
            balls.add(new Ball(i, 1, new Vector2d(TableState.width / 2, TableState.length / 18 * (i + 1))));
        }
        return new TableState(balls);
    }

    public static TableState noClearPathTableState() {
        return new TableState(
                new ArrayList<>() {{
                    add(new Ball(0, 1, new Vector2d(0.28, 0.28)));
                    add(new Ball(1, 1, new Vector2d(0.2, 0.2)));
                    add(new Ball(2, 1, new Vector2d(0.1, 0.04)));
                }}
        );
    }
}
