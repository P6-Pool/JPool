package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.DoubleFunction;

public class PathFinder {
    public static ArrayList<Vector2d> getTableIdxes(int d) {
        ArrayList<Vector2d> coords = new ArrayList<>();
        int x = 0, y = -d;
        for (int i = 0; i < 4 * d; i++) {
            coords.add(new Vector2d(x, y));
            x += i >= 3 * d ? 1 : i >= 2 * d ? -1 : i >= d ? -1 : 1;
            y += i >= 2 * d ? -1 : 1;
        }
        return coords;
    }

    public static Vector2d getProjectedBallPos(Vector2d tableIdx, Ball ball, TableState tableState) {
        Vector2d ghostBallPos = ball.pos.copy();

        if (tableIdx.x % 2 == 0) {
            ghostBallPos.x += tableIdx.x * tableState.width;
        } else {
            ghostBallPos.x = (tableIdx.x + 1) * tableState.width - ball.pos.x;
        }

        if (tableIdx.y % 2 == 0) {
            ghostBallPos.y += tableIdx.y * tableState.length;
        } else {
            ghostBallPos.y = (tableIdx.y + 1) * tableState.length - ball.pos.y;
        }

        ghostBallPos.x -= tableIdx.x * ball.radius * 2;
        ghostBallPos.y -= tableIdx.y * ball.radius * 2;

        return ghostBallPos;
    }

    public static ArrayList<Vector2d> getProjectedBallPoses(int d, Ball ball, TableState tableState) {
        ArrayList<Vector2d> tableIdxes = getTableIdxes(d);
        ArrayList<Vector2d> projectedGhostBallPoses = getTableIdxes(d);

        for (Vector2d tableIdx : tableIdxes) {
            projectedGhostBallPoses.add(getProjectedBallPos(tableIdx, ball, tableState));
        }

        return projectedGhostBallPoses;
    }

    public static Vector2d getKissTargetPos(Ball ball, Ball target, boolean isLeft) {
        Vector2d diff = target.pos.sub(ball.pos);
        double hyp = diff.mag();
        double adj = Ball.radius * 2;

        double angle = Math.acos(adj / hyp);
        angle *= isLeft ? -1 : 1;

        return diff.norm().rotateClockwise(angle).mult(adj / 2);
    }

    public static Vector2d adjustTarget(Ball mainBall, Vector2d target, boolean isLeft, ArrayList<Ball> balls, JShotStep step, int depth) {
        double angle = isLeft ? -Math.PI / 2 : Math.PI / 2;
        Vector2d rightMostOffset = target.sub(mainBall.pos).norm().rotateClockwise(angle).mult(Ball.radius);
        Vector2d lineSegStartPoint = mainBall.pos.add(rightMostOffset);
        Vector2d lineSegEndPoint = target.add(rightMostOffset);

        Ball intersectingBall = null;

        for (Ball ball : balls) {
            if (ball.number == mainBall.number || ball.state == 0 || ball.number == step.b1) {
                continue;
            }
            if (isLineSegmentAndBallColliding(lineSegStartPoint, lineSegEndPoint, ball.pos, Ball.radius)) {
                intersectingBall = ball;
                break;
            }
        }

        if (intersectingBall == null) {
            return target;
        }

        Vector2d kiss = getKissTargetPos(mainBall, intersectingBall, isLeft);
        Vector2d adjustedLinePointQ = intersectingBall.pos.sub(kiss);
        Vector2d adjustedLinePointP = mainBall.pos.add(kiss);
        Vector2d adjustedTarget;

        if (step.type == JShotStep.JShotStepType.POCKET || step.type == JShotStep.JShotStepType.RAIL) {
            Vector2d adjustedLineDiff = adjustedLinePointQ.sub(adjustedLinePointP);
            Vector2d otherTarget = isLeft ? step.rightMost : step.leftMost;
            Vector2d leftRightLineDiff = otherTarget.sub(target);
            Vector2d adjustedEdge = getLineLineIntersection(adjustedLinePointQ, adjustedLineDiff, target, leftRightLineDiff);
            adjustedTarget = adjustedEdge.add(leftRightLineDiff.norm().mult(Ball.radius));
        } else {
            ArrayList<Vector2d> intersections = getLineCircleIntersections(mainBall.pos, mainBall.pos.add(adjustedLinePointQ.sub(adjustedLinePointP)), step.posB1, Ball.radius * 2);
            if (intersections.size() < 2) {
                return null;
            }
            adjustedTarget = intersections.get(intersections.indexOf(Collections.min(intersections)));
        }

        if (depth > 0) {
            return adjustTarget(mainBall, adjustedTarget, isLeft, balls, step, depth - 1);
        }

        return adjustedTarget;
    }

    public static Vector2d getLineLineIntersection(Vector2d lPoint, Vector2d lLineVec, Vector2d mPoint, Vector2d mLineVec) {
        double a1 = lLineVec.y / lLineVec.x;
        double b1 = lPoint.y - a1 * lPoint.x;

        double a2 = mLineVec.y / mLineVec.x;
        double b2 = mPoint.y - a2 * mPoint.x;

        double x = (b2 - b1) / (a1 - a2);
        double y = a1 * x + b1;

        return new Vector2d(x, y);
    }

    public static boolean isLineSegmentAndBallColliding(Vector2d lineStart, Vector2d lineEnd, Vector2d ballOrigin, double ballRadius) {
        // https://www.baeldung.com/cs/circle-line-segment-collision-detection

        Vector2d se = lineEnd.sub(lineStart);
        Vector2d es = lineStart.sub(lineEnd);
        Vector2d os = lineStart.sub(ballOrigin);
        Vector2d oe = lineEnd.sub(ballOrigin);

        double minDist;
        double maxDist = Math.max(os.mag(), oe.mag());

        if (os.dot(es) > 0 && oe.dot(se) > 0) {
            double triangleArea = Math.abs(os.cross(oe)) / 2;
            minDist = 2 * triangleArea / se.mag();
        } else {
            minDist = Math.min(os.mag(), oe.mag());
        }

        return minDist <= ballRadius && maxDist >= ballRadius;
    }

    public static ArrayList<Vector2d> getLineCircleIntersections(Vector2d linePointP, Vector2d linePointQ, Vector2d ballOrigin, double ballRadius) {
        ArrayList<Vector2d> intersections = new ArrayList<>();

        Vector2d lineVec = linePointQ.sub(linePointP);
        Vector2d ballToP = linePointP.sub(ballOrigin);

        double a = lineVec.dot(lineVec);
        double b = 2 * lineVec.dot(ballToP);
        double c = ballToP.dot(ballToP) - ballRadius * ballRadius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant >= 0) {
            double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            intersections.add(linePointP.add(lineVec.mult(t1)));

            if (discriminant > 0) {
                double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                intersections.add(linePointP.add(lineVec.mult(t2)));
            }
        }

        return intersections;
    }
}
