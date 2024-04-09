package org.CueCraft.FastFiz;

import org.CueCraft.Geometry.Vector2d;

public class Ball {
    public int number;
    public int state; // 1 if in play, 0 otherwise
    public static double radius = 0.028575;
    public Vector2d pos;
    public TableState.playerPattern pattern;

    public Ball(int number, int state, Vector2d pos) {
        this.number = number;
        this.state = state;
        this.pos = pos.copy();
        this.pattern = number > 8 ? TableState.playerPattern.STRIPED : TableState.playerPattern.SOLID;
    }

    public static boolean notInPlay(Ball b) {
        return b.state == 2;
    }

    public Ball copy() {
        return new Ball(number, state, pos.copy());
    }
}
