package org.CueCraft.ShotGenerator;

import JFastfiz.TableState;
import org.CueCraft.Grpc.Client;
import org.CueCraft.Pool.Ball;
import org.CueCraft.Pool.Table;

import java.util.ArrayList;
import java.util.List;

public class ShotGenerator {

    static public ArrayList<ShotStep> generateShots(TableState tableState, Table.PlayerPattern playerPattern, int depth) {
        Table table = Table.fromTableState(tableState);

        if (depth < 2) {
            return new ArrayList<>();
        }

        ShotStep.idCounter = 0;
        ArrayList<ShotStep> doneShots = new ArrayList<>();
        ArrayList<ShotStep> undoneShots = generatePocketingWays(table);

        while (!undoneShots.isEmpty()) {
            ArrayList<ShotStep> newShots = new ArrayList<>();
            for (ShotStep shot : undoneShots) {
                newShots.addAll(generateBallBoths(table, shot, playerPattern));
                newShots.addAll(generateKissBalls(table, shot, playerPattern));
                for (int i = 1; i < depth - shot.depth; i++) {
                    newShots.addAll(generateRailShots(table, shot, playerPattern, i));
                }
            }

            ArrayList<ShotStep> reasonableShots = getReasonableShots(newShots);
            doneShots.addAll(getDoneShots(reasonableShots));
            undoneShots = getUndoneShots(reasonableShots, depth);
        }

        return doneShots;
    }

    private static ArrayList<ShotStep> generatePocketingWays(Table table) {
        return new ArrayList<>() {{
            add(ShotStep.ShotPocketStep(table.E_Pocket));
            add(ShotStep.ShotPocketStep(table.NE_Pocket));
            add(ShotStep.ShotPocketStep(table.SW_Pocket));
            add(ShotStep.ShotPocketStep(table.W_Pocket));
            add(ShotStep.ShotPocketStep(table.SE_Pocket));
            add(ShotStep.ShotPocketStep(table.NW_Pocket));
        }};
    }

    private static ArrayList<ShotStep> generateRailShots(Table table, ShotStep targetStep, Table.PlayerPattern playerPattern, int numRailHits) {
        ArrayList<ShotStep> newStepTrees = new ArrayList<>();
        ArrayList<Vector2d> tableIdxes = PathFinder.getTableIdxes(numRailHits);

        // TODO add kiss balls to list of possible balls
        for (Ball ball : table.balls) {

            if (invalidBallBothBall(targetStep, table, ball, playerPattern)) {
                continue;
            }

            tableIdxLoop:
            for (Vector2d tableIdx : tableIdxes) {

                ShotStep next = targetStep.copy();
                ShotStep prev = null;

                Vector2d projectedLeftMost = PathFinder.getProjectedBallPos(tableIdx, next.leftMost);
                Vector2d projectedRightMost = PathFinder.getProjectedBallPos(tableIdx, next.rightMost);

                Vector2d diffLeftMostBefore = projectedLeftMost.sub(ball.pos);
                Vector2d diffRightMostBefore = projectedRightMost.sub(ball.pos);

                int tableDist = (int) (Math.abs(tableIdx.x) + Math.abs(tableIdx.y));
                boolean leftRightFlipped = tableDist % 2 == 1;

                if (leftRightFlipped && diffLeftMostBefore.determinant(diffRightMostBefore) < 0) {
                    continue;
                } else if (!leftRightFlipped && diffLeftMostBefore.determinant(diffRightMostBefore) > 0) {
                    continue;
                }

                ArrayList<Ball> balls = new ArrayList<>(table.balls);
                for (int y = (int) Math.min(0, tableIdx.y); y <= (int) Math.max(tableIdx.y, 0); y++) {
                    for (int x = (int) Math.min(0, tableIdx.x); x <= (int) Math.max(tableIdx.x, 0); x++) {
                        if (x == 0 && y == 0) {
                            continue;
                        }
                        for (Ball b : table.balls) {
                            Ball projectedBall = b.copy();
                            projectedBall.pos = PathFinder.getProjectedBallPos(new Vector2d(x, y), b.pos);
                            balls.add(projectedBall);
                        }
                    }
                }

                ShotStep projectedStep = next.copy();
                Vector2d adjustedProjectedLeftMostTarget;
                Vector2d adjustedProjectedRightMostTarget;

                if (!leftRightFlipped) {
                    projectedStep.leftMost = projectedLeftMost;
                    projectedStep.rightMost = projectedRightMost;

                    adjustedProjectedLeftMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, projectedLeftMost, true, balls, projectedStep, 2);
                    adjustedProjectedRightMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, projectedRightMost, false, balls, projectedStep, 2);
                } else {
                    projectedStep.leftMost = projectedRightMost;
                    projectedStep.rightMost = projectedLeftMost;

                    adjustedProjectedLeftMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, projectedLeftMost, false, balls, projectedStep, 2);
                    adjustedProjectedRightMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, projectedRightMost, true, balls, projectedStep, 2);
                }


                if (adjustedProjectedLeftMostTarget == null || adjustedProjectedRightMostTarget == null) {
                    continue;
                }

                Vector2d diffLeftMostAfter = adjustedProjectedLeftMostTarget.sub(ball.pos);
                Vector2d diffRightMostAfter = adjustedProjectedRightMostTarget.sub(ball.pos);

                if (leftRightFlipped && diffLeftMostAfter.determinant(diffRightMostAfter) < 0) {
                    continue;
                } else if (!leftRightFlipped && diffLeftMostAfter.determinant(diffRightMostAfter) > 0) {
                    continue;
                }

                ArrayList<Vector2d> railLeftMostHits = PathFinder.getHitProjections(ball.pos, adjustedProjectedLeftMostTarget);
                ArrayList<Vector2d> railRightMostHits = PathFinder.getHitProjections(ball.pos, adjustedProjectedRightMostTarget);

                if (railLeftMostHits == null || railRightMostHits == null) {
                    continue;
                }

                assert Math.abs(railLeftMostHits.size() - railRightMostHits.size()) < 2;

                if (railLeftMostHits.size() > railRightMostHits.size()) {
                    railLeftMostHits.removeLast();
                } else if (railLeftMostHits.size() < railRightMostHits.size()) {
                    railRightMostHits.removeLast();
                } else if (railLeftMostHits.getLast().mag() <= 0.001 && railRightMostHits.getLast().mag() <= 0.001) {
                    railRightMostHits.removeLast();
                    railLeftMostHits.removeLast();
                }

                int numSteps = railLeftMostHits.size();

                next.b1 = ball.number;
                next.rightMost = ball.pos;
                next.leftMost = ball.pos;

                for (int i = 0; i < numSteps; i++) {
                    next.leftMost = next.leftMost.add(railLeftMostHits.get(i));
                    next.rightMost = next.rightMost.add(railRightMostHits.get(i));
                }

                next.ghostBallPos = getGhostBall(next.type, next.leftMost, next.rightMost, next.posB1);

                ArrayList<Ball> intersectingBalls = PathFinder.getBallsIntersectingWithLineSegment(balls, ball.pos, PathFinder.getProjectedBallPos(tableIdx, next.ghostBallPos), new ArrayList<>(List.of(ball.number)));
                if (!intersectingBalls.isEmpty()) {
                    continue;
                }

                for (int i = 0; i < numSteps; i++) {
                    int idx = numSteps - 1 - i;

                    Vector2d newLeftMostDiff = i % 2 == 0 ? railRightMostHits.get(idx) : railLeftMostHits.get(idx);
                    Vector2d newRightMostDiff = i % 2 == 0 ? railLeftMostHits.get(idx) : railRightMostHits.get(idx);

                    Vector2d newLeftMost = next.rightMost.sub(newLeftMostDiff);
                    Vector2d newRightMost = next.leftMost.sub(newRightMostDiff);

                    ShotStep.ShotStepType newType = ShotStep.ShotStepType.RAIL;
                    Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);

                    if (i == 0 && !PathFinder.isKissShotReachable(next, newGhostBallPos)) {
                        continue tableIdxLoop;
                    }

                    if (i == numSteps - 1) {
                        newLeftMost = ball.pos.add(newLeftMostDiff.normalize().mult(-Ball.radius * 2));
                        newRightMost = ball.pos.add(newRightMostDiff.normalize().mult(-Ball.radius * 2));
                        newType = ball.number == 0 ? ShotStep.ShotStepType.CUE_STRIKE : ShotStep.ShotStepType.BALL_BOTH;
                    }

                    prev = new ShotStep(newType, next, ball.pos, newGhostBallPos, newLeftMost, newRightMost, ball.number, ball.number, next.depth + 1, next.pocket);
                    next = prev;
                }

                prev.ghostBallPos = getGhostBall(ShotStep.ShotStepType.BALL_BOTH, prev.leftMost, prev.rightMost, ball.pos);
                newStepTrees.add(prev);
            }
        }
        return newStepTrees;
    }

    private static ArrayList<ShotStep> generateBallBoths(Table table, ShotStep targetStep, Table.PlayerPattern playerPattern) {
        ArrayList<ShotStep> newStepTrees = new ArrayList<>();

        for (Ball ball : table.balls) {
            ShotStep next = targetStep.copy();

            if (invalidBallBothBall(next, table, ball, playerPattern)) {
                continue;
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

            Vector2d adjustedLeftMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, next.leftMost, true, table.balls, next, 2);
            Vector2d adjustedRightMostTarget = PathFinder.adjustTarget(ball.pos, ball.number, next.rightMost, false, table.balls, next, 2);

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
            ShotStep.ShotStepType newType = ball.number == 0 ? ShotStep.ShotStepType.CUE_STRIKE : ShotStep.ShotStepType.BALL_BOTH;
            Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);
            int newB1 = newType == ShotStep.ShotStepType.CUE_STRIKE ? 0 : -1;

            ArrayList<Ball> intersectingBalls = PathFinder.getBallsIntersectingWithLineSegment(table.balls, ball.pos, next.ghostBallPos, new ArrayList<>(List.of(ball.number)));
            if (!intersectingBalls.isEmpty()) {
                continue;
            }

            ShotStep newStepTree = new ShotStep(newType, next, ball.pos, newGhostBallPos, newLeftMost, newRightMost, newB1, ball.number, next.depth + 1, next.pocket);

            newStepTrees.add(newStepTree);
        }

        return newStepTrees;
    }

    private static ArrayList<ShotStep> generateKissBalls(Table table, ShotStep targetStep, Table.PlayerPattern playerPattern) {
        ArrayList<ShotStep> newStepTrees = new ArrayList<>();

        for (Ball ball : table.balls) {

            if (invalidBallBothBall(targetStep, table, ball, playerPattern)) {
                continue;
            }

            ShotStep step1 = generateKissBall(table, ball, targetStep, true);
            ShotStep step2 = generateKissBall(table, ball, targetStep, false);

            if (step1 != null) {
                newStepTrees.add(step1);
            }
            if (step2 != null) {
                newStepTrees.add(step2);
            }
        }

        return newStepTrees;
    }

    private static ShotStep generateKissBall(Table table, Ball ball, ShotStep targetStep, boolean isLeft) {
        ShotStep next = targetStep.copy();

        Vector2d diffLeftMostBefore = next.leftMost.sub(ball.pos);
        Vector2d diffRightMostBefore = next.rightMost.sub(ball.pos);

        //TODO when next type is KISS there are cases where leftmost and rightmost crosses that should be legal
        if (diffLeftMostBefore.determinant(diffRightMostBefore) > 0) {
            return null;
        }

        if (!PathFinder.isKissShotReachable(next, ball.pos)) {
            return null;
        }

        Vector2d rightKiss = PathFinder.getKissTargetPos(next.rightMost, ball.pos, !isLeft);
        Vector2d leftKiss = PathFinder.getKissTargetPos(next.leftMost, ball.pos, !isLeft);

        if (rightKiss == null || leftKiss == null) {
            return null;
        }

        Vector2d newLeftMost = ball.pos.add(rightKiss.mult(2));
        Vector2d newRightMost = ball.pos.add(leftKiss.mult(2));
        ShotStep.ShotStepType newType = isLeft ? ShotStep.ShotStepType.KISS_LEFT : ShotStep.ShotStepType.KISS_RIGHT;
        Vector2d newGhostBallPos = getGhostBall(newType, newLeftMost, newRightMost, ball.pos);

        Vector2d adjustedLeftMostTarget = PathFinder.adjustTarget(newLeftMost, ball.number, next.leftMost, true, table.balls, next, 2);
        Vector2d adjustedRightMostTarget = PathFinder.adjustTarget(newRightMost, ball.number, next.rightMost, false, table.balls, next, 2);

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

        ArrayList<Ball> intersectingBalls = PathFinder.getBallsIntersectingWithLineSegment(table.balls, newGhostBallPos, next.ghostBallPos, new ArrayList<>(List.of(ball.number)));
        if (!intersectingBalls.isEmpty()) {
            return null;
        }

        if (!PathFinder.isKissShotReachable(next, newGhostBallPos)) {
            return null;
        }

        return new ShotStep(newType, next, ball.pos, newGhostBallPos, newLeftMost, newRightMost, -1, ball.number, next.depth + 1, next.pocket);
    }

    private static ArrayList<ShotStep> getReasonableShots(ArrayList<ShotStep> shots) {
        ArrayList<ShotStep> reasonableShots = new ArrayList<>();
        for (ShotStep shot : shots) {
            if (shot.getErrorMargin() > 0.0004) {
                reasonableShots.add(shot);
            }
        }
        return reasonableShots;
    }

    private static ArrayList<ShotStep> getDoneShots(ArrayList<ShotStep> shots) {
        ArrayList<ShotStep> doneShots = new ArrayList<>();
        for (ShotStep shot : shots) {
            if (shot.type == ShotStep.ShotStepType.CUE_STRIKE && ShotStep.ballBothInvolvedInShot(shot)) {
                doneShots.add(shot);
            }
        }
        return doneShots;
    }

    private static ArrayList<ShotStep> getUndoneShots(ArrayList<ShotStep> shots, int depth) {
        ArrayList<ShotStep> undoneShots = new ArrayList<>();
        for (ShotStep shot : shots) {
            if (shot.depth < depth && (shot.type != ShotStep.ShotStepType.CUE_STRIKE)) {
                undoneShots.add(shot);
            }
        }
        return undoneShots;
    }

    private static boolean invalidBallBothBall(ShotStep next, Table table, Ball ball, Table.PlayerPattern playerPattern) {

        boolean ballNotInPlay = ball.state == 0;

        boolean pocketedCueBall = next.type == ShotStep.ShotStepType.POCKET && ball.number == 0;

        int numPlayerBallsLeft = (int) table.balls.stream().filter(b -> b.state == 1 && isFriendlyBall(playerPattern, b)).count();
        boolean pocketed8BallEarly = next.type == ShotStep.ShotStepType.POCKET && ball.number == 8 && numPlayerBallsLeft != 0;

        boolean pocketedOpponentBall = next.type == ShotStep.ShotStepType.POCKET && isOpponentBall(playerPattern, ball);

        boolean ballAlreadyInShot = ShotStep.ballInvolvedInShot(next, ball.number);

        return ballNotInPlay || pocketedCueBall || pocketedOpponentBall || pocketed8BallEarly || ballAlreadyInShot;
    }

    private static boolean isFriendlyBall(Table.PlayerPattern playerPattern , Ball ball) {
        if (ball.number == 0 ||ball.number == 8) {
            return false;
        }
        if (playerPattern == Table.PlayerPattern.NONE) {
            return true;
        } else {
            return playerPattern == ball.pattern;
        }
    }

    private static boolean isOpponentBall(Table.PlayerPattern playerPattern , Ball ball) {
        if (ball.number == 0 || ball.number == 8) {
            return false;
        }
        if (playerPattern == Table.PlayerPattern.NONE) {
            return false;
        } else {
            return playerPattern != ball.pattern;
        }
    }

    private static Vector2d getGhostBall(ShotStep.ShotStepType type, Vector2d leftMost, Vector2d rightMost, Vector2d ballPos) {
        Vector2d centerVector = leftMost.center(rightMost);
        if (type == ShotStep.ShotStepType.POCKET || type == ShotStep.ShotStepType.RAIL) {
            return centerVector;
        } else {
            Vector2d offset = centerVector.sub(ballPos).normalize().mult(2 * Ball.radius);
            return ballPos.add(offset);
        }
    }

}
