package org.CueCraft.Pool;

import JFastfiz.Table;
import org.CueCraft.ShotGenerator.Vector2d;

public class Pocket {
    public enum PocketType {NE_Pocket, E_Pocket, SE_Pocket, SW_Pocket, W_Pocket, NW_Pocket}

    public PocketType type;
    public Vector2d leftMost;
    public Vector2d rightMost;
    public Vector2d center;

    public Pocket(Vector2d center, PocketType type, double width) {
        this.type = type;
        this.center = center;

        Vector2d leftMostOffset = new Vector2d(0, 0);
        Vector2d rightMostOffset = new Vector2d(0, 0);

        double halfWidth = width / 2;

        switch (type) {
            case NE_Pocket:
                leftMostOffset = new Vector2d(-1, 1).normalize().mult(halfWidth);
                rightMostOffset = new Vector2d(1, -1).normalize().mult(halfWidth);
                break;
            case E_Pocket:
                leftMostOffset = new Vector2d(0, 1).normalize().mult(halfWidth);
                rightMostOffset = new Vector2d(0, -1).normalize().mult(halfWidth);
                break;
            case SE_Pocket:
                leftMostOffset = new Vector2d(1, 1).normalize().mult(halfWidth);
                rightMostOffset = new Vector2d(-1, -1).normalize().mult(halfWidth);
                break;
            case SW_Pocket:
                leftMostOffset = new Vector2d(1, -1).normalize().mult(halfWidth);
                rightMostOffset = new Vector2d(-1, 1).normalize().mult(halfWidth);
                break;
            case W_Pocket:
                leftMostOffset = new Vector2d(0, -1).normalize().mult(halfWidth);
                rightMostOffset = new Vector2d(0, 1).normalize().mult(halfWidth);
                break;
            case NW_Pocket:
                leftMostOffset = new Vector2d(-1, -1).normalize().mult(halfWidth);
                rightMostOffset = new Vector2d(1, 1).normalize().mult(halfWidth);
                break;
        }

        this.leftMost = center.add(leftMostOffset);
        this.rightMost = center.add(rightMostOffset);
    }

    public static Table.Pocket fromPocketType(PocketType pocket) {
        return switch (pocket) {
            case PocketType.NE_Pocket -> JFastfiz.Table.Pocket.NE;
            case PocketType.E_Pocket -> JFastfiz.Table.Pocket.E;
            case PocketType.SE_Pocket -> JFastfiz.Table.Pocket.SE;
            case PocketType.SW_Pocket -> JFastfiz.Table.Pocket.SW;
            case PocketType.W_Pocket -> JFastfiz.Table.Pocket.W;
            case PocketType.NW_Pocket -> JFastfiz.Table.Pocket.NW;
        };
    }

}
