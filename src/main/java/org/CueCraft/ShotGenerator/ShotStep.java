package org.CueCraft.ShotGenerator;

import org.CueCraft.Pool.Ball;
import org.CueCraft.Pool.Pocket;

public class ShotStep {
    public static int idCounter = 0;
    public enum ShotStepType {CUE_STRIKE, POCKET, RAIL, STRIKE, KISS_LEFT, KISS_RIGHT, BALL_BOTH}

    public ShotStep(ShotStepType type, ShotStep next, ShotStep branch, Vector2d posB1, Vector2d ghostBall, Vector2d leftMost, Vector2d rightMost, int b1, int b2, int depth) {
        this.type = type;
        this.next = next;
        this.branch = branch;
        this.posB1 = posB1;
        this.ghostBallPos = ghostBall;
        this.leftMost = leftMost;
        this.rightMost = rightMost;
        this.b1 = b1;
        this.b2 = b2;
        this.id = idCounter++;
        this.depth = depth;
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
                null,
                pocket.center,
                pocket.leftMost.add(leftMostOffset),
                pocket.rightMost.add(rightMostOffset),
                -1,
                -1,
                0
        );
    }

    public ShotStep copy() {
        return new ShotStep(
                type,
                next,
                branch,
                posB1 == null ? null : posB1.copy(),
                ghostBallPos.copy(),
                leftMost.copy(),
                rightMost.copy(),
                b1,
                b2,
                depth
        );
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
}
