package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;

import java.util.ArrayList;

public class ShotTree {
    static public ArrayList<JShotStep> generateShotTree(TableState tableState, int depth, TableState.playerPattern playerPattern) {
        ArrayList<JShotStep> doneShots = new ArrayList<>();
        ArrayList<JShotStep> pocketWays = generatePocketingWays(tableState);
        generateShotTreeHelper(tableState, doneShots, pocketWays, depth, playerPattern);
        return doneShots;
    }

    private static ArrayList<JShotStep> generatePocketingWays(TableState tableState) {
        return new ArrayList<>() {{
            add(JShotStep.JShotPocketStep(tableState.E_Pocket));
            add(JShotStep.JShotPocketStep(tableState.NE_Pocket));
            add(JShotStep.JShotPocketStep(tableState.SW_Pocket));
            add(JShotStep.JShotPocketStep(tableState.W_Pocket));
            add(JShotStep.JShotPocketStep(tableState.SE_Pocket));
            add(JShotStep.JShotPocketStep(tableState.NW_Pocket));
        }};
    }

    private static void generateShotTreeHelper(TableState tableState, ArrayList<JShotStep> doneShots, ArrayList<JShotStep> undoneShots, int depth, TableState.playerPattern playerPattern) {
        ArrayList<JShotStep> newShots = new ArrayList<>();
        ArrayList<JShotStep> newUndoneShots = new ArrayList<>();

        for (JShotStep shot : undoneShots) {
            newShots.addAll(generateBallBoths(tableState, shot, playerPattern));
        }

        doneShots.addAll(getDoneShots(newShots));
        newUndoneShots.addAll(getUndoneShots(newShots));

        if (depth != 0) {
            generateShotTreeHelper(tableState, doneShots, newUndoneShots, depth - 1, playerPattern);
        }
    }

    private static ArrayList<JShotStep> generateBallBoths(TableState tableState, JShotStep targetStep, TableState.playerPattern playerPattern) {
        ArrayList<JShotStep> newStepTrees = new ArrayList<>();

        for (Ball ball : tableState.balls) {
            JShotStep next = targetStep.copy();

            if (invalidBallBothBall(next, tableState, ball, playerPattern)) {
                continue;
            }

            Vector2d diffLeftMostBefore = next.leftMost.sub(ball.pos);
            Vector2d diffRightMostBefore = next.rightMost.sub(ball.pos);

            if (diffLeftMostBefore.cross(diffRightMostBefore) > 0) {
                continue;
            }

            Vector2d adjustedLeftMostTarget = PathFinder.adjustTarget(ball, next.leftMost, true, tableState.balls, next, 2);
            Vector2d adjustedRightMostTarget = PathFinder.adjustTarget(ball, next.rightMost, false, tableState.balls, next, 2);

            if (adjustedLeftMostTarget == null || adjustedRightMostTarget == null) {
                continue;
            }

            Vector2d diffLeftMostAfter = adjustedLeftMostTarget.sub(ball.pos);
            Vector2d diffRightMostAfter = adjustedRightMostTarget.sub(ball.pos);

            if (diffLeftMostAfter.cross(diffRightMostAfter) > 0) {
                continue;
            }

            next.leftMost = adjustedLeftMostTarget;
            next.rightMost = adjustedRightMostTarget;
            next.ghostBallPos = getGhostBall(next.type, next.leftMost, next.rightMost, next.posB1);
            next.b1 = ball.number;

            Vector2d diffNewLeftMost = diffRightMostAfter.norm().mult(-Ball.radius * 2);
            Vector2d diffNewRightMost = diffLeftMostAfter.norm().mult(-Ball.radius * 2);

            Vector2d newLeftMost = ball.pos.add(diffNewLeftMost);
            Vector2d newRightMost = ball.pos.add(diffNewRightMost);
            JShotStep.JShotStepType newType = ball.number == 0 ? JShotStep.JShotStepType.CUE_STRIKE : JShotStep.JShotStepType.BALL_BOTH;
            Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);
            int newB1 = newType == JShotStep.JShotStepType.CUE_STRIKE ? 0 : -1;

            JShotStep newStepTree = new JShotStep(newType, next, null, ball.pos, newGhostBallPos, newLeftMost, newRightMost, newB1, ball.number);

            newStepTrees.add(newStepTree);
        }

        return newStepTrees;
    }

    private static ArrayList<JShotStep> getDoneShots(ArrayList<JShotStep> shots) {
        ArrayList<JShotStep> doneShots = new ArrayList<>();
        for (JShotStep shot : shots) {
            if (shot.type == JShotStep.JShotStepType.CUE_STRIKE) {
                doneShots.add(shot);
            }
        }
        return doneShots;
    }

    private static ArrayList<JShotStep> getUndoneShots(ArrayList<JShotStep> shots) {
        ArrayList<JShotStep> undoneShots = new ArrayList<>();
        for (JShotStep shot : shots) {
            if (shot.type != JShotStep.JShotStepType.CUE_STRIKE) {
                undoneShots.add(shot);
            }
        }
        return undoneShots;
    }

    private static boolean invalidBallBothBall(JShotStep next, TableState tableState, Ball ball, TableState.playerPattern playerPattern) {

        boolean ballNotInPlay = ball.state == 0;

        boolean pocketedCueBall = next.type == JShotStep.JShotStepType.POCKET && ball.number == 0;
        boolean pocketedOpponentBall = next.type == JShotStep.JShotStepType.POCKET && ball.pattern != playerPattern;

        int numPlayerBallsLeft = (int) tableState.balls.stream().filter(b -> b.pattern == playerPattern && b.number != 0 && b.number != 8 && b.state != 0).count();
        boolean pocketed8BallEarly = next.type == JShotStep.JShotStepType.POCKET && ball.number == 8 && numPlayerBallsLeft != 0;

        boolean ballAlreadyInShot = JShotStep.ballInvolvedInShot(next, ball.number);

        return ballNotInPlay || pocketedCueBall || pocketedOpponentBall || pocketed8BallEarly || ballAlreadyInShot;
    }

    private static Vector2d getGhostBall(JShotStep.JShotStepType type, Vector2d leftMost, Vector2d rightMost, Vector2d ballPos) {
        Vector2d centerVector = leftMost.getCenterVector(rightMost);
        if (type == JShotStep.JShotStepType.POCKET || type == JShotStep.JShotStepType.RAIL) {
            return centerVector;
        } else {
            Vector2d offset = centerVector.sub(ballPos).norm().mult(2 * Ball.radius);
            return ballPos.add(offset);
        }
    }
}
