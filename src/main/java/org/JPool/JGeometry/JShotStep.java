package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.Pocket;
import org.JPool.Main;

public class JShotStep {
    public static int idCounter = 0;
    public enum JShotStepType {CUE_STRIKE, POCKET, RAIL, STRIKE, KISS_LEFT, KISS_RIGHT, BALL_BOTH}

    public JShotStep(JShotStepType type, JShotStep next, JShotStep branch, Vector2d posB1, Vector2d ghostBall, Vector2d leftMost, Vector2d rightMost, int b1, int b2, int depth) {
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

    public static JShotStep JShotPocketStep(Pocket pocket) {

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

        return new JShotStep(
                JShotStepType.POCKET,
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

    public JShotStep copy() {
        return new JShotStep(
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

    public static boolean ballInvolvedInShot(JShotStep shot, int ballNumber) {
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

    public static boolean ballBothInvolvedInShot(JShotStep shot) {
        if (shot.type == JShotStepType.BALL_BOTH) {
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

    public JShotStepType type;
    public JShotStep next;
    public JShotStep branch;
    public Vector2d posB1;
    public Vector2d ghostBallPos;
    public Vector2d leftMost;
    public Vector2d rightMost;
    public int b1;
    public int b2;
    public int id;
    public int depth;
}
