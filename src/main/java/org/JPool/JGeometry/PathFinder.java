package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static Vector2d getKissTargetPos(Vector2d ballToBeShotPos, Vector2d ballToBeHitPos, boolean isLeft) {
        Vector2d diff = ballToBeShotPos.sub(ballToBeHitPos);
        double hyp = diff.mag();
        double adj = Ball.radius * 2;

        double angle = Math.acos(adj / hyp);
        angle *= isLeft ? -1 : 1;

        return diff.normalize().rotateClockwise(angle).mult(adj / 2);
    }

    public static Vector2d adjustTarget(Vector2d mainBallPos, int mainBallNumber, Vector2d target, boolean isLeft, ArrayList<Ball> balls, JShotStep step, int depth) {
        double angle = isLeft ? -Math.PI / 2 : Math.PI / 2;
        Vector2d perpOffset = target.sub(mainBallPos).normalize().rotateClockwise(angle).mult(Ball.radius);
        Vector2d lineSegStartPoint = mainBallPos.add(perpOffset);
        Vector2d lineSegEndPoint = target.add(perpOffset);

        ArrayList<Ball> intersectingBalls = getBallsIntersectingWithLineSegment(balls, lineSegStartPoint, lineSegEndPoint, new ArrayList<>(List.of(mainBallNumber, step.b1)));
        if (intersectingBalls.isEmpty()) {
            return target;
        }

        Ball intersectingBall = intersectingBalls.get(0);
        Vector2d kiss = getKissTargetPos(mainBallPos, intersectingBall.pos, isLeft);
        Vector2d adjustedLinePointQ = intersectingBall.pos.add(kiss);
        Vector2d adjustedLinePointP = mainBallPos.sub(kiss);
        Vector2d adjustedTarget;

        if (step.type == JShotStep.JShotStepType.POCKET || step.type == JShotStep.JShotStepType.RAIL) {
            Vector2d adjustedLineDiff = adjustedLinePointQ.sub(adjustedLinePointP);
            Vector2d otherTarget = isLeft ? step.rightMost : step.leftMost;
            Vector2d leftRightLineDiff = otherTarget.sub(target);
            Vector2d adjustedEdge = getLineLineIntersection(adjustedLinePointQ, adjustedLineDiff, target, leftRightLineDiff);
            adjustedTarget = adjustedEdge.add(leftRightLineDiff.normalize().mult(Ball.radius));
        } else {
            ArrayList<Vector2d> intersections = getLineCircleIntersections(mainBallPos, mainBallPos.add(adjustedLinePointQ.sub(adjustedLinePointP)), step.posB1, Ball.radius * 2);
            if (intersections.size() < 2) {
                return null;
            }
            adjustedTarget = intersections.get(intersections.indexOf(Collections.min(intersections)));
        }

        if (depth > 0) {
            return adjustTarget(mainBallPos, mainBallNumber, adjustedTarget, isLeft, balls, step, depth - 1);
        }

        intersectingBalls = getBallsIntersectingWithLineSegment(balls, lineSegStartPoint, lineSegEndPoint, new ArrayList<>(List.of(mainBallNumber, step.b1)));
        if (intersectingBalls.isEmpty()) {
            return null;
        }

        return adjustedTarget;
    }

    public static ArrayList<Ball> getBallsIntersectingWithLineSegment(ArrayList<Ball> balls, Vector2d segmentStartPoint, Vector2d segmentEndPoint, ArrayList<Integer> excludedBalls) {
        ArrayList<Ball> intersectingBalls = new ArrayList<>();

        for (Ball ball : balls) {
            if (ball.state == 0 || excludedBalls.contains(ball.number)) {
                continue;
            }
            if (isLineSegmentAndBallColliding(segmentStartPoint, segmentEndPoint, ball.pos, Ball.radius)) {
                intersectingBalls.add(ball);
            }
        }

        return intersectingBalls;
    }

    public static boolean isKissShotReachable(JShotStep next, Vector2d ballPos) {
        if (next.type == JShotStep.JShotStepType.KISS_LEFT) {
            Vector2d spanLeftMost = next.leftMost.sub(next.next.rightMost);
            Vector2d spanRightMost = next.rightMost.sub(next.posB1);
            Vector2d intersection = PathFinder.getLineLineIntersection(next.leftMost, spanLeftMost, next.rightMost, spanRightMost);
            if (intersection == null) {
                return false;
            }
            Vector2d point = ballPos.sub(intersection);
            return PathFinder.isPointInSpan(spanLeftMost, spanRightMost, point);
        } else if (next.type == JShotStep.JShotStepType.KISS_RIGHT) {
            Vector2d spanLeftMost =  next.rightMost.sub(next.next.leftMost);
            Vector2d spanRightMost = next.leftMost.sub(next.posB1);
            Vector2d intersection = PathFinder.getLineLineIntersection(next.leftMost, spanLeftMost, next.rightMost, spanRightMost);
            if (intersection == null) {
                return false;
            }
            Vector2d point = ballPos.sub(intersection);
            return PathFinder.isPointInSpan(spanLeftMost, spanRightMost, point);
        }
        return true;
    }

    public static Vector2d getLineLineIntersection(Vector2d lPoint, Vector2d lLineVec, Vector2d mPoint, Vector2d mLineVec) {
        double lLineSlope = lLineVec.y / lLineVec.x;
        double mLineSlope = mLineVec.y / mLineVec.x;

        // Handle case when lines are parallel
        if (lLineSlope == mLineSlope) {
            return null;
        }

        // Check if either line is vertical
        if (Double.isInfinite(lLineSlope)) {
            double x = lPoint.x;
            double y = mLineSlope * x + mPoint.y - mLineSlope * mPoint.x;
            return new Vector2d(x, y);
        }

        if (Double.isInfinite(mLineSlope)) {
            double x = mPoint.x;
            double y = lLineSlope * x + lPoint.y - lLineSlope * lPoint.x;
            return new Vector2d(x, y);
        }

        // For non-vertical lines, calculate intersection point
        double lLineIntercept = lPoint.y - lLineSlope * lPoint.x;
        double mLineIntercept = mPoint.y - mLineSlope * mPoint.x;

        double x = (mLineIntercept - lLineIntercept) / (lLineSlope - mLineSlope);
        double y = lLineSlope * x + lLineIntercept;

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
            double triangleArea = Math.abs(os.determinant(oe)) / 2;
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

    public static boolean isPointInSpan(Vector2d leftMost, Vector2d rightMost, Vector2d point) {
        double angle = leftMost.angleBetween(rightMost);
        Vector2d bisector = leftMost.add(rightMost).div(leftMost.add(rightMost).mag());

        double angleToPoint = point.angleBetween(bisector);

        return angleToPoint <= angle / 2;
    }
}
