package org.CueCraft.Pool;

import org.CueCraft.ShotGenerator.Vector2d;

public class Ball {
    public int number;
    public int state; // 1 if in play, 0 otherwise
    public static double radius = 0.028575;
    public Vector2d pos;
    public Table.PlayerPattern pattern;

    public Ball(int number, int state, Vector2d pos) {
        this.number = number;
        this.state = state;
        this.pos = pos.copy();
        this.pattern = number > 8 ? Table.PlayerPattern.STRIPED : Table.PlayerPattern.SOLID;
    }

    public static boolean notInPlay(Ball b) {
        return b.state == 0;
    }

    public Ball copy() {
        return new Ball(number, state, pos.copy());
    }

    public static Ball fromJBall(JFastfiz.Ball b) {
        int newState = b.getState().swigValue() == 1 ? 1 : 0;
        Vector2d newPos = new Vector2d(b.getPos().getX(), b.getPos().getY());
        return new Ball(b.getID().swigValue(), newState, newPos);
    }
}
