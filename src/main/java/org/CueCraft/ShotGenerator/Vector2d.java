package org.CueCraft.ShotGenerator;

import java.util.Locale;

public class Vector2d implements Comparable<Vector2d>{
    public double x, y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(x + other.x, y + other.y);
    }

    public Vector2d sub(Vector2d other) {
        return new Vector2d(x - other.x, y - other.y);
    }

    public Vector2d mult(double val) {
        return new Vector2d(x * val, y * val);
    }

    public Vector2d div(double val) {
        return new Vector2d(x / val, y / val);
    }

    public Vector2d normalize() {
        return div(mag());
    }
    public Vector2d normal() {
        return new Vector2d(y, -x);
    }
    public double dot(Vector2d other) {
        return x * other.x + y * other.y;
    }

    public double determinant(Vector2d other) {
        return x * other.y - y * other.x;
    }

    public double angleBetween(Vector2d other) {
        return Math.acos(dot(other) / (mag() * other.mag()));
    }

    public Vector2d rotateClockwise(double angle) {
        double newX = Math.cos(angle) * x + Math.sin(angle) * y;
        double newY = -Math.sin(angle) * x + Math.cos(angle) * y;
        return new Vector2d(newX, newY);
    }

    public Vector2d center(Vector2d other) {
        return add(other.sub(this).div(2));
    }

    public Vector2d copy() {
        return new Vector2d(x, y);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.3f, %.3f)", x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Vector2d)) {
            return false;
        }
        double epsilon = 0.0001;

        double deltaX = Math.abs(x - ((Vector2d) other).x);
        double deltaY = Math.abs(y - ((Vector2d) other).y);

        return  deltaX < epsilon && deltaY < epsilon;
    }

    @Override
    public int compareTo(Vector2d other) {
        double diff = mag() - other.mag();
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}
