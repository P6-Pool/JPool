package org.JPool.FastFiz;

import org.JPool.JGeometry.Vector2d;

public class Pocket {
    public Vector2d leftMost;
    public Vector2d rightMost;
    public Vector2d center;

    public Pocket(Vector2d leftMost, Vector2d rightMost) {
        this.leftMost = leftMost.add(rightMost.sub(leftMost).norm().mult(Ball.radius));
        this.rightMost = rightMost.add(leftMost.sub(rightMost).norm().mult(Ball.radius));
        this.center = leftMost.add(rightMost.sub(leftMost).div(2));
    }
}
