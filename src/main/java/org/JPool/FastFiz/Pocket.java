package org.JPool.FastFiz;

import org.JPool.JGeometry.Vector2d;

public class Pocket {
    public Vector2d leftMost;
    public Vector2d rightMost;
    public Vector2d center;

    public Pocket(Vector2d leftMost, Vector2d rightMost) {
        this.leftMost = leftMost;
        this.rightMost = rightMost;
        this.center = leftMost.center(rightMost);
    }
}
