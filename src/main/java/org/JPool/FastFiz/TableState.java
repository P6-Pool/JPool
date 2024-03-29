package org.JPool.FastFiz;

import org.JPool.JGeometry.Vector2d;

import java.util.ArrayList;
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
    public enum playerPattern { STRIPED, SOLID, NONE }

    public TableState(ArrayList<Ball> balls) {
        this.balls = balls;

        double diagonalWidth = cornerPocketWidth / Math.sqrt(2);
        double straightWidth = sidePocketWidth / 2.0;

        NE_Pocket = new Pocket(
                new Vector2d(width - diagonalWidth, length),
                new Vector2d(width, length - diagonalWidth));

        E_Pocket = new Pocket(
                new Vector2d(width, length / 2.0 + straightWidth),
                new Vector2d(width, length / 2.0 - straightWidth));

        SE_Pocket = new Pocket(
                new Vector2d(width, diagonalWidth),
                new Vector2d(width - diagonalWidth, 0.0));

        SW_Pocket = new Pocket(
                new Vector2d(diagonalWidth, 0.0),
                new Vector2d(0.0, diagonalWidth));

        W_Pocket = new Pocket(
                new Vector2d(0.0, length / 2.0 - straightWidth),
                new Vector2d(0.0, length / 2.0 + straightWidth));

        NW_Pocket = new Pocket(
                new Vector2d(0.0, length - diagonalWidth),
                new Vector2d(diagonalWidth, length));
    }

    public static TableState randomTableState(int numBalls) {
        Random rand = new Random(numBalls);
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < numBalls + 1; i++) {
            balls.add(new Ball(i, 1, new Vector2d(Ball.radius + rand.nextDouble()*(TableState.width - Ball.radius * 2), TableState.length / 18 * (i + 1))));
        }
        return new TableState(balls);
    };

    public static TableState linedTableState(int numBalls) {
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < numBalls + 1; i++) {
            balls.add(new Ball(i, 1, new Vector2d(TableState.width / 2, TableState.length / 18 * (i + 1))));
        }
        return new TableState(balls);
    };

    public static TableState noClearPathTableState() {
        return new TableState(
                new ArrayList<>(){{
                    add(new Ball(0, 1, new Vector2d(0.28, 0.28)));
                    add(new Ball(1, 1, new Vector2d(0.2, 0.2)));
                    add(new Ball(2, 1, new Vector2d(0.1, 0.04)));
                }}
        );
    };
}
