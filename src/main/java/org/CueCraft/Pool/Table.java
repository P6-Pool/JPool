package org.CueCraft.Pool;

import JFastfiz.EightBallState;
import JFastfiz.GameType;
import JFastfiz.TableState;
import org.CueCraft.ShotGenerator.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Table {
    public final ArrayList<Ball> balls;
    public final Pocket NE_Pocket, E_Pocket, SE_Pocket, SW_Pocket, W_Pocket, NW_Pocket;

    public static double width = 1.116;
    public static double length = 2.236;
    public static double innerWidth = width - 2 * Ball.radius;
    public static double innerLength = length - 2 * Ball.radius;

    public static double cornerPocketWidth = 0.11;
    public static double sidePocketWidth = 0.12;

    public enum PlayerPattern {STRIPED, SOLID, NONE}

    public Table(ArrayList<Ball> balls) {
        this.balls = balls;

        double diagonalOffset= Math.sqrt(2) * (cornerPocketWidth / 2) / 2;

        NE_Pocket = new Pocket(new Vector2d(width - diagonalOffset, length - diagonalOffset), Pocket.PocketType.NE_Pocket, cornerPocketWidth);
        E_Pocket = new Pocket(new Vector2d(width, length / 2), Pocket.PocketType.E_Pocket, sidePocketWidth);
        SE_Pocket = new Pocket(new Vector2d(width - diagonalOffset, diagonalOffset), Pocket.PocketType.SE_Pocket, cornerPocketWidth);
        SW_Pocket = new Pocket(new Vector2d(diagonalOffset, diagonalOffset), Pocket.PocketType.SW_Pocket, cornerPocketWidth);
        W_Pocket = new Pocket(new Vector2d(0, length / 2), Pocket.PocketType.W_Pocket, sidePocketWidth);
        NW_Pocket = new Pocket(new Vector2d(diagonalOffset, length - diagonalOffset), Pocket.PocketType.NW_Pocket, cornerPocketWidth);
    }

    public static Table randomTableState(int numBalls) {
        ArrayList<Ball> balls = new ArrayList<>();
        ArrayList<Integer> ballNums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        Random rand = new Random(numBalls);

        for (int i = 0; i < numBalls; i++) {
            int randIdx = rand.nextInt(ballNums.size());
            int ballNum = ballNums.get(randIdx);
            double yPos = Table.length / 18 * (i + 1);
            double xPos = Ball.radius + rand.nextDouble() * (Table.width - Ball.radius * 2);
            balls.add(new Ball(ballNum, 1, new Vector2d(xPos, yPos)));
            ballNums.remove(randIdx);
        }

        ballNums.add(0);
        int randIdx = rand.nextInt(ballNums.size());
        int ballNum = ballNums.get(randIdx);
        double yPos = Table.length / 18 * (ballNum + 1);
        double xPos = Ball.radius + rand.nextDouble() * (Table.width - Ball.radius * 2);
        balls.add(new Ball(0, 1, new Vector2d(xPos, yPos)));

        return new Table(balls);
    }

    public static Table randomSequentialTableState(int numBalls) {
        Random rand = new Random(numBalls);
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < numBalls + 1; i++) {
            balls.add(new Ball(i, 1, new Vector2d(Ball.radius + rand.nextDouble() * (Table.width - Ball.radius * 2), Table.length / 18 * (i + 1))));
        }
        return new Table(balls);
    }

    public static Table eightBallTableState() {
        ArrayList<Ball> balls = new ArrayList<>();
        TableState ts = EightBallState.RackedState(GameType.GT_EIGHTBALL).tableState();
        for (int i = 0; i < 16 + 1; i++) {
            JFastfiz.Ball b = ts.getBall(JFastfiz.Ball.Type.swigToEnum(i));
            balls.add(new Ball(i, 1, new Vector2d(b.getPos().getX(), b.getPos().getY())));
        }
        return new Table(balls);
    }

    public static Table linedTableState(int numBalls) {
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < numBalls + 1; i++) {
            balls.add(new Ball(i, 1, new Vector2d(Table.width / 2, Table.length / 18 * (i + 1))));
        }
        return new Table(balls);
    }

    public static Table noClearPathTableState() {
        return new Table(
                new ArrayList<>() {{
                    add(new Ball(0, 1, new Vector2d(0.28, 0.28)));
                    add(new Ball(1, 1, new Vector2d(0.2, 0.2)));
                    add(new Ball(2, 1, new Vector2d(0.1, 0.04)));
                }}
        );
    }

    public TableState toTableState(){
        TableState ts = new TableState();
        for(Ball ball : this.balls){
            JFastfiz.Ball.Type bType = JFastfiz.Ball.Type.swigToEnum(ball.number);
            JFastfiz.Ball.State bState = JFastfiz.Ball.State.swigToEnum(ball.state);
            ts.setBall(bType, bState, ball.pos.x, ball.pos.y);
        }
        return ts;
    }

    public static Table fromTableState(TableState tableState){
        ArrayList<Ball> balls = new ArrayList<>();
        for(int i = 0; i <= 15; i++){
            JFastfiz.Ball ball = tableState.getBall(JFastfiz.Ball.Type.swigToEnum(i));

            int newBallNumber = ball.getID().swigValue();
            int newBallState = ball.getState() == JFastfiz.Ball.State.STATIONARY ? 1 : 0;
            Vector2d newBallPos = new Vector2d(ball.getPos().getX(), ball.getPos().getY());
            Ball newBall = new Ball(newBallNumber, newBallState, newBallPos);
            balls.add(newBall);
        }

        return new Table(balls);
    }
}
