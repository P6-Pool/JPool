package org.JPool.JGeometry;

import org.JPool.FastFiz.Pocket;

public class JShotStep {
    public enum JShotStepType {CUE_STRIKE, POCKET, RAIL, STRIKE, KISS, BALL_BOTH}

    public JShotStep(JShotStepType type, JShotStep next, JShotStep branch, Vector2d posB1, Vector2d ghostBall, Vector2d leftMost, Vector2d rightMost, int b1, int b2) {
        this.type = type;
        this.next = next;
        this.branch = branch;
        this.posB1 = posB1;
        this.ghostBallPos = ghostBall;
        this.leftMost = leftMost;
        this.rightMost = rightMost;
        this.b1 = b1;
        this.b2 = b2;
    }

    public static JShotStep JShotPocketStep(Pocket pocket) {
        return new JShotStep(
                JShotStepType.POCKET,
                null,
                null,
                null,
                pocket.center,
                pocket.leftMost,
                pocket.rightMost,
                -1,
                -1
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
                b2
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

    public JShotStepType type;
    public JShotStep next;
    public JShotStep branch;
    public Vector2d posB1;
    public Vector2d ghostBallPos;
    public Vector2d leftMost;
    public Vector2d rightMost;
    public int b1;
    public int b2;
}
