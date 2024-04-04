package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;

import java.util.ArrayList;
import java.util.List;

public class ShotTree {
    static public ArrayList<JShotStep> generateShotTree(TableState tableState, int depth, TableState.playerPattern playerPattern) {
        if (depth < 2) {
            return new ArrayList<>();
        }

        JShotStep.idCounter = 0;
        ArrayList<JShotStep> doneShots = new ArrayList<>();
        ArrayList<JShotStep> undoneShots = generatePocketingWays(tableState);

        while (!undoneShots.isEmpty()) {
            ArrayList<JShotStep> newShots = new ArrayList<>();
            for (JShotStep shot : undoneShots) {
                newShots.addAll(generateBallBoths(tableState, shot, playerPattern));
                newShots.addAll(generateKissBalls(tableState, shot, playerPattern));
                for (int i = 1; i < depth - shot.depth; i++) {
                    //TODO remember to not generate rail hots from shots where next is already a rail shot
                    newShots.addAll(generateRailShots(tableState, shot, playerPattern, i));
                }
            }
            doneShots.addAll(getDoneShots(newShots));
            undoneShots = getUndoneShots(newShots, depth);
        }

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

    private static ArrayList<JShotStep> generateRailShots(TableState tableState, JShotStep targetStep, TableState.playerPattern playerPattern, int numRailHits) {
        ArrayList<JShotStep> newStepTrees = new ArrayList<>();
        ArrayList<Vector2d> tableIdxes = PathFinder.getTableIdxes(numRailHits);

        for (Ball ball : tableState.balls) {

            if (invalidBallBothBall(targetStep, tableState, ball, playerPattern)) {
                continue;
            }

            tableIdxLoop:
            for (Vector2d tableIdx : tableIdxes) {

                JShotStep next = targetStep.copy();
                JShotStep prev = null;

                Vector2d projectedLeftMost = PathFinder.getProjectedBallPos(tableIdx, next.leftMost);
                Vector2d projectedRightMost = PathFinder.getProjectedBallPos(tableIdx, next.rightMost);


                Vector2d diffLeftMostBefore = projectedLeftMost.sub(ball.pos);
                Vector2d diffRightMostBefore = projectedRightMost.sub(ball.pos);

                int tableDist = (int) (Math.abs(tableIdx.x) + Math.abs(tableIdx.y));
                boolean leftRightFlipped = tableDist % 2 == 0;

                if (leftRightFlipped && diffLeftMostBefore.determinant(diffRightMostBefore) > 0) {
                    continue;
                } else if (!leftRightFlipped && diffLeftMostBefore.determinant(diffRightMostBefore) < 0) {
                    continue;
                }

                Vector2d adjustedLeftMostTarget = next.leftMost; //PathFinder.adjustTarget(ball.pos, ball.number, next.leftMost, true, tableState.balls, next, 2);
                Vector2d adjustedRightMostTarget = next.rightMost; //PathFinder.adjustTarget(ball.pos, ball.number, next.rightMost, false, tableState.balls, next, 2);

                if (adjustedLeftMostTarget == null || adjustedRightMostTarget == null) {
                    continue;
                }

                Vector2d diffLeftMostAfter = adjustedLeftMostTarget.sub(ball.pos);
                Vector2d diffRightMostAfter = adjustedRightMostTarget.sub(ball.pos);

//                if (diffLeftMostAfter.determinant(diffRightMostAfter) > 0) {
//                    continue;
//                }

                next.leftMost = adjustedLeftMostTarget;
                next.rightMost = adjustedRightMostTarget;
                next.ghostBallPos = getGhostBall(next.type, next.leftMost, next.rightMost, next.posB1);
                next.b1 = ball.number;

                ArrayList<Vector2d> railLeftMostHits = PathFinder.getHitProjections(ball.pos, projectedLeftMost);
                ArrayList<Vector2d> railRightMostHits = PathFinder.getHitProjections(ball.pos, projectedRightMost);

                assert Math.abs(railLeftMostHits.size() - railRightMostHits.size()) < 2;

                if (railLeftMostHits.size() > railRightMostHits.size()) {
                    railLeftMostHits.removeLast();
                } else if (railLeftMostHits.size() < railRightMostHits.size()) {
                    railRightMostHits.removeLast();
                } else if (railLeftMostHits.getLast().mag() == 0 && railRightMostHits.getLast().mag() == 0) {
                    railRightMostHits.removeLast();
                    railLeftMostHits.removeLast();
                }

                int numSteps = railLeftMostHits.size();

                for (int i = 0; i < numSteps; i++) {
                    int idx = numSteps - 1 - i;

                    Vector2d newLeftMostDiff = i % 2 == 0 ? railRightMostHits.get(idx) : railLeftMostHits.get(idx) ;
                    Vector2d newRightMostDiff = i % 2 == 0 ? railLeftMostHits.get(idx) : railRightMostHits.get(idx) ;

                    Vector2d newLeftMost = next.rightMost.sub(newLeftMostDiff);
                    Vector2d newRightMost = next.leftMost.sub(newRightMostDiff);

                    JShotStep.JShotStepType newType = JShotStep.JShotStepType.RAIL;
                    Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);

                    if (i == 0 && !PathFinder.isKissShotReachable(next, newGhostBallPos)) {
                        continue tableIdxLoop;
                    }

                    if (i == numSteps - 1) {
                        newLeftMost = ball.pos.add(newLeftMostDiff.normalize().mult(-Ball.radius * 2));
                        newRightMost = ball.pos.add(newRightMostDiff.normalize().mult(-Ball.radius * 2));
                        if (ball.number == 0) {
                            newType = JShotStep.JShotStepType.CUE_STRIKE;
                        }
                    }

                    prev = new JShotStep(newType, next, null, ball.pos, newGhostBallPos, newLeftMost, newRightMost, ball.number, ball.number, next.depth + 1);
                    next = prev;
                }

                newStepTrees.add(prev);
            }
        }
        return newStepTrees;
    }

    private static ArrayList<JShotStep> generateBallBoths(TableState tableState, JShotStep targetStep, TableState.playerPattern playerPattern) {
        ArrayList<JShotStep> newStepTrees = new ArrayList<>();

        for (Ball ball : tableState.balls) {
            JShotStep next = targetStep.copy();

            if (invalidBallBothBall(next, tableState, ball, playerPattern)) {
                continue;
            }

            if (next.id == 701) {
                System.out.println("asd");
            }

            Vector2d diffLeftMostBefore = next.leftMost.sub(ball.pos);
            Vector2d diffRightMostBefore = next.rightMost.sub(ball.pos);

            //TODO when next type is KISS there are cases where leftmost and rightmost crosses that should be legal
            if (diffLeftMostBefore.determinant(diffRightMostBefore) > 0) {
                continue;
            }

            if (!PathFinder.isKissShotReachable(next, ball.pos)) {
                continue;
            }

            Vector2d adjustedLeftMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, next.leftMost, true, tableState.balls, next, 2);
            Vector2d adjustedRightMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, next.rightMost, false, tableState.balls, next, 2);

            if (adjustedLeftMostTarget == null || adjustedRightMostTarget == null) {
                continue;
            }

            Vector2d diffLeftMostAfter = adjustedLeftMostTarget.sub(ball.pos);
            Vector2d diffRightMostAfter = adjustedRightMostTarget.sub(ball.pos);

            if (diffLeftMostAfter.determinant(diffRightMostAfter) > 0) {
                continue;
            }

            next.leftMost = adjustedLeftMostTarget;
            next.rightMost = adjustedRightMostTarget;
            next.ghostBallPos = getGhostBall(next.type, next.leftMost, next.rightMost, next.posB1);
            next.b1 = ball.number;

            Vector2d diffNewLeftMost = diffRightMostAfter.normalize().mult(-Ball.radius * 2);
            Vector2d diffNewRightMost = diffLeftMostAfter.normalize().mult(-Ball.radius * 2);

            Vector2d newLeftMost = ball.pos.add(diffNewLeftMost);
            Vector2d newRightMost = ball.pos.add(diffNewRightMost);
            JShotStep.JShotStepType newType = ball.number == 0 ? JShotStep.JShotStepType.CUE_STRIKE : JShotStep.JShotStepType.BALL_BOTH;
            Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);
            int newB1 = newType == JShotStep.JShotStepType.CUE_STRIKE ? 0 : -1;

            ArrayList<Ball> intersectingBalls = PathFinder.getBallsIntersectingWithLineSegment(tableState.balls, ball.pos, next.ghostBallPos, new ArrayList<>(List.of(ball.number)));
            if (!intersectingBalls.isEmpty()) {
                continue;
            }

            JShotStep newStepTree = new JShotStep(newType, next, null, ball.pos, newGhostBallPos, newLeftMost, newRightMost, newB1, ball.number, next.depth + 1);

            newStepTrees.add(newStepTree);
        }

        return newStepTrees;
    }

    private static ArrayList<JShotStep> generateKissBalls(TableState tableState, JShotStep targetStep, TableState.playerPattern playerPattern) {
        ArrayList<JShotStep> newStepTrees = new ArrayList<>();

        for (Ball ball : tableState.balls) {

            if (invalidBallBothBall(targetStep, tableState, ball, playerPattern)) {
                continue;
            }

            JShotStep step1 = generateKissBall(tableState, ball, targetStep, true);
            JShotStep step2 = generateKissBall(tableState, ball, targetStep, false);

            if (step1 != null) {
                newStepTrees.add(step1);
            }
            if (step2 != null) {
                newStepTrees.add(step2);
            }
        }

        return newStepTrees;
    }

    private static JShotStep generateKissBall(TableState tableState, Ball ball, JShotStep targetStep, boolean isLeft) {
        JShotStep next = targetStep.copy();

        if (next.id == 701) {
            System.out.println("asd");
        }

        Vector2d diffLeftMostBefore = next.leftMost.sub(ball.pos);
        Vector2d diffRightMostBefore = next.rightMost.sub(ball.pos);

        //TODO when next type is KISS there are cases where leftmost and rightmost crosses that should be legal
        if (diffLeftMostBefore.determinant(diffRightMostBefore) > 0) {
            return null;
        }

        if (!PathFinder.isKissShotReachable(next, ball.pos)) {
            return null;
        }

        Vector2d newLeftMost = ball.pos.add(PathFinder.getKissTargetPos(next.rightMost, ball.pos, !isLeft).mult(2));
        Vector2d newRightMost = ball.pos.add(PathFinder.getKissTargetPos(next.leftMost, ball.pos, !isLeft).mult(2));
        JShotStep.JShotStepType newType = isLeft ? JShotStep.JShotStepType.KISS_LEFT : JShotStep.JShotStepType.KISS_RIGHT;
        Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);

        Vector2d adjustedLeftMostTarget = PathFinder.adjustTarget(newLeftMost, ball.number, next.leftMost, true, tableState.balls, next, 2);
        Vector2d adjustedRightMostTarget = PathFinder.adjustTarget(newRightMost, ball.number, next.rightMost, false, tableState.balls, next, 2);

        if (adjustedLeftMostTarget == null || adjustedRightMostTarget == null) {
            return null;
        }

        Vector2d diffLeftMostAfter = adjustedLeftMostTarget.sub(ball.pos);
        Vector2d diffRightMostAfter = adjustedRightMostTarget.sub(ball.pos);

        if (diffLeftMostAfter.determinant(diffRightMostAfter) > 0) {
            return null;
        }

        next.leftMost = adjustedLeftMostTarget;
        next.rightMost = adjustedRightMostTarget;
        next.ghostBallPos = getGhostBall(next.type, next.leftMost, next.rightMost, next.posB1);
        next.b1 = ball.number;

        ArrayList<Ball> intersectingBalls = PathFinder.getBallsIntersectingWithLineSegment(tableState.balls, newGhostBallPos, next.ghostBallPos, new ArrayList<>(List.of(ball.number)));
        if (!intersectingBalls.isEmpty()) {
            return null;
        }

        if (!PathFinder.isKissShotReachable(next, newGhostBallPos)) {
            return null;
        }

        return new JShotStep(newType, next, null, ball.pos, newGhostBallPos, newLeftMost, newRightMost, -1, ball.number, next.depth + 1);
    }

    private static ArrayList<JShotStep> getDoneShots(ArrayList<JShotStep> shots) {
        ArrayList<JShotStep> doneShots = new ArrayList<>();
        for (JShotStep shot : shots) {
            if (shot.type == JShotStep.JShotStepType.CUE_STRIKE && JShotStep.ballBothInvolvedInShot(shot)) {
                doneShots.add(shot);
            }
        }
        return doneShots;
    }

    private static ArrayList<JShotStep> getUndoneShots(ArrayList<JShotStep> shots, int depth) {
        ArrayList<JShotStep> undoneShots = new ArrayList<>();
        for (JShotStep shot : shots) {
            if (shot.depth < depth && (shot.type != JShotStep.JShotStepType.CUE_STRIKE)) {
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
        Vector2d centerVector = leftMost.center(rightMost);
        if (type == JShotStep.JShotStepType.POCKET || type == JShotStep.JShotStepType.RAIL) {
            return centerVector;
        } else {
            Vector2d offset = centerVector.sub(ballPos).normalize().mult(2 * Ball.radius);
            return ballPos.add(offset);
        }
    }
}
