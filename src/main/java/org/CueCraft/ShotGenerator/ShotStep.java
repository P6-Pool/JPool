package org.CueCraft.ShotGenerator;

import org.CueCraft.Pool.Ball;
import org.CueCraft.Pool.Pocket;

public class ShotStep {
    public static int idCounter = 0;

    public enum ShotStepType {CUE_STRIKE, POCKET, RAIL, STRIKE, KISS_LEFT, KISS_RIGHT, BALL_BOTH}

    public ShotStep(ShotStepType type, ShotStep next, Vector2d posB1, Vector2d ghostBall, Vector2d leftMost, Vector2d rightMost, int b1, int b2, int depth, Pocket.PocketType pocket) {
        this.type = type;
        this.next = next;
        this.posB1 = posB1;
        this.ghostBallPos = ghostBall;
        this.leftMost = leftMost;
        this.rightMost = rightMost;
        this.b1 = b1;
        this.b2 = b2;
        this.id = idCounter++;
        this.depth = depth;
        this.pocket = pocket;
    }

    public static ShotStep ShotPocketStep(Pocket pocket) {

        double len = Math.sqrt(2 * Ball.radius * Ball.radius);

        Vector2d leftMostOffset = (switch (pocket.type) {
            case NE_Pocket -> new Vector2d(1, -1);
            case E_Pocket -> new Vector2d(-1, -1);
            case SE_Pocket -> new Vector2d(-1, -1);
            case SW_Pocket -> new Vector2d(-1, 1);
            case W_Pocket -> new Vector2d(1, 1);
            case NW_Pocket -> new Vector2d(1, 1);
        }).normalize().mult(len);

        Vector2d rightMostOffset = (switch (pocket.type) {
            case NE_Pocket -> new Vector2d(-1, 1);
            case E_Pocket -> new Vector2d(-1, 1);
            case SE_Pocket -> new Vector2d(1, 1);
            case SW_Pocket -> new Vector2d(1, -1);
            case W_Pocket -> new Vector2d(1, -1);
            case NW_Pocket -> new Vector2d(-1, -1);
        }).normalize().mult(len);

        return new ShotStep(
                ShotStepType.POCKET,
                null,
                null,
                pocket.center,
                pocket.leftMost.add(leftMostOffset),
                pocket.rightMost.add(rightMostOffset),
                -1,
                -1,
                0,
                pocket.type
        );
    }

    public ShotStep copy() {
        return new ShotStep(
                type,
                next,
                posB1 == null ? null : posB1.copy(),
                ghostBallPos.copy(),
                leftMost.copy(),
                rightMost.copy(),
                b1,
                b2,
                depth,
                pocket
        );
    }

    public double getShotAngle() {
        Vector2d diff = ghostBallPos.sub(posB1);
        Vector2d xAxis = new Vector2d(1, 0);

        double angle = (Math.acos(diff.dot(xAxis) / (diff.mag() * xAxis.mag())) * 180 / Math.PI) + 180 % 360;

        if (diff.y < 0) {
            angle = 360 - angle;
        }

        return angle;
    }

    public double getErrorMargin() {
        if (next != null) {
            Vector2d leftMostDiff = next.leftMost.sub(posB1);
            Vector2d rightMostDiff = next.rightMost.sub(posB1);

            return leftMostDiff.angleBetween(rightMostDiff);
        } else {
            return Math.PI / 4;
        }
    }

    public int getPocketedBallNumber() {
        ShotStep curShot = this;
        int number = -1;

        while (curShot.next != null) {
            if (curShot.type == ShotStepType.BALL_BOTH) {
                number = curShot.b2;
            }
            curShot = curShot.next;
        }

        return number;
    }

    public static boolean ballInvolvedInShot(ShotStep shot, int ballNumber) {
        if (shot.b1 == ballNumber || shot.b2 == ballNumber) {
            return true;
        } else if (shot.next == null) {
            return false;
        }

        if (shot.branch != null) {
            return ballInvolvedInShot(shot.next, ballNumber) || ballInvolvedInShot(shot.branch, ballNumber);
        } else {
            return ballInvolvedInShot(shot.next, ballNumber);
        }
    }

    public static boolean ballBothInvolvedInShot(ShotStep shot) {
        if (shot.type == ShotStepType.BALL_BOTH) {
            return true;
        } else if (shot.next == null) {
            return false;
        }

        if (shot.branch != null) {
            return ballBothInvolvedInShot(shot.next) || ballBothInvolvedInShot(shot.branch);
        } else {
            return ballBothInvolvedInShot(shot.next);
        }
    }

    public ShotStepType type;
    public ShotStep next;
    public ShotStep branch;
    public Vector2d posB1;
    public Vector2d ghostBallPos;
    public Vector2d leftMost;
    public Vector2d rightMost;
    public int b1;
    public int b2;
    public int id;
    public int depth;
    public Pocket.PocketType pocket;
}
