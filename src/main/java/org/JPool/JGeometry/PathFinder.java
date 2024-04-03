package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;
import org.javatuples.Pair;

import java.util.*;

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

    public static Vector2d getProjectedBallPos(Vector2d tableIdx, Vector2d ballPos) {
        Vector2d ghostBallPos = ballPos.copy();

        if (tableIdx.x % 2 == 0) {
            ghostBallPos.x += tableIdx.x * TableState.width;
        } else {
            ghostBallPos.x = (tableIdx.x + 1) * TableState.width - ballPos.x;
        }

        if (tableIdx.y % 2 == 0) {
            ghostBallPos.y += tableIdx.y * TableState.length;
        } else {
            ghostBallPos.y = (tableIdx.y + 1) * TableState.length - ballPos.y;
        }

        ghostBallPos = ghostBallPos.sub(tableIdx.mult(Ball.radius * 2));

        return ghostBallPos;
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

        Ball intersectingBall = intersectingBalls.getFirst();
        Vector2d kiss = getKissTargetPos(mainBallPos, intersectingBall.pos, isLeft);
        Vector2d adjustedLinePointQ = intersectingBall.pos.add(kiss);
        Vector2d adjustedLinePointP = mainBallPos.sub(kiss);
        Vector2d adjustedTarget;

        if (step.type == JShotStep.JShotStepType.POCKET || step.type == JShotStep.JShotStepType.RAIL) {
            Vector2d adjustedLineDiff = adjustedLinePointQ.sub(adjustedLinePointP);
            Vector2d otherTarget = isLeft ? step.rightMost : step.leftMost;
            Vector2d leftRightLineDiff = otherTarget.sub(target);
            Vector2d adjustedEdge = getLineLineIntersection(adjustedLinePointQ, adjustedLineDiff, target, leftRightLineDiff);
            Vector2d offset = leftRightLineDiff.mag() == 0 ? new Vector2d(0, 0) : leftRightLineDiff.normalize().mult(Ball.radius);
            adjustedTarget = adjustedEdge.add(offset);
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
            Vector2d spanLeftMost = next.rightMost.sub(next.next.leftMost);
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
        if (Double.isInfinite(lLineSlope) || Double.isNaN(lLineSlope)) {
            double x = lPoint.x;
            double y = mLineSlope * x + mPoint.y - mLineSlope * mPoint.x;
            return new Vector2d(x, y);
        }

        if (Double.isInfinite(mLineSlope) || Double.isNaN(mLineSlope)) {
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


    public static ArrayList<Vector2d> getHitProjections(Vector2d startPos, Vector2d endPos) {
        enum HitType {RIGHT_VERT_HIT, LEFT_VERT_HIT, TOP_HOR_HIT, BOTTOM_HOR_HIT}

        int numPosVertHits = (int) ((endPos.x - Ball.radius) / TableState.innerWidth);
        int numNegVertHits = (int) (1 + (-endPos.x + Ball.radius) / TableState.innerWidth);

        int numPosHorHits = (int) ((endPos.y - Ball.radius) / TableState.innerLength);
        int numNegHorHits = (int) (1 + (-endPos.y + Ball.radius) / TableState.innerLength);

        ArrayList<Pair<Vector2d, HitType>> intersections = new ArrayList<>() {{
            add(new Pair<>(endPos, null));
        }};
        Vector2d diff = endPos.sub(startPos);

        for (int i = 0; i < numPosVertHits; i++) {
            Vector2d linePoint = new Vector2d(TableState.width - Ball.radius + TableState.innerWidth * i, 0);
            Vector2d lineVec = new Vector2d(0, 1);
            Vector2d intersection = getLineLineIntersection(linePoint, lineVec, startPos, diff);
            intersections.add(new Pair<>(intersection, i % 2 == 0 ? HitType.RIGHT_VERT_HIT : HitType.LEFT_VERT_HIT));
        }

        for (int i = 0; i < numNegVertHits; i++) {
            Vector2d linePoint = new Vector2d(Ball.radius - TableState.innerWidth * i, 0);
            Vector2d lineVec = new Vector2d(0, 1);
            Vector2d intersection = getLineLineIntersection(linePoint, lineVec, startPos, diff);
            intersections.add(new Pair<>(intersection, i % 2 == 0 ? HitType.LEFT_VERT_HIT : HitType.RIGHT_VERT_HIT));
        }

        for (int i = 0; i < numPosHorHits; i++) {
            Vector2d linePoint = new Vector2d(0, TableState.length - Ball.radius + TableState.innerLength * i);
            Vector2d lineVec = new Vector2d(1, 0);
            Vector2d intersection = getLineLineIntersection(linePoint, lineVec, startPos, diff);
            intersections.add(new Pair<>(intersection, i % 2 == 0 ? HitType.TOP_HOR_HIT : HitType.BOTTOM_HOR_HIT));
        }

        for (int i = 0; i < numNegHorHits; i++) {
            Vector2d linePoint = new Vector2d(0, Ball.radius - TableState.innerLength * i);
            Vector2d lineVec = new Vector2d(1, 0);
            Vector2d intersection = getLineLineIntersection(linePoint, lineVec, startPos, diff);
            intersections.add(new Pair<>(intersection, i % 2 == 0 ? HitType.BOTTOM_HOR_HIT : HitType.TOP_HOR_HIT));
        }

        intersections.sort(Comparator.comparing((Pair<Vector2d, HitType> a) -> a.getValue0().sub(startPos)));

        ArrayList<Vector2d> hitVectors = new ArrayList<>() {{
            add(intersections.getFirst().getValue0().sub(startPos));
        }};

        boolean flipVert = intersections.getFirst().getValue1() == HitType.LEFT_VERT_HIT || intersections.getFirst().getValue1() == HitType.RIGHT_VERT_HIT;
        boolean flipHor = intersections.getFirst().getValue1() == HitType.BOTTOM_HOR_HIT || intersections.getFirst().getValue1() == HitType.TOP_HOR_HIT;;

        for (int i = 1; i < intersections.size(); i++) {
            Pair<Vector2d, HitType> prevHit = intersections.get(i - 1);
            Pair<Vector2d, HitType> nextHit = intersections.get(i);

            Vector2d hitDiff = nextHit.getValue0().sub(prevHit.getValue0());

            hitDiff.x *= flipVert ? -1 : 1;
            hitDiff.y *= flipHor ? -1 : 1;

            hitVectors.add(hitDiff);

            if (nextHit.getValue1() == HitType.LEFT_VERT_HIT || nextHit.getValue1() == HitType.RIGHT_VERT_HIT) {
                flipVert = !flipVert;
            }

            if (nextHit.getValue1() == HitType.BOTTOM_HOR_HIT || nextHit.getValue1() == HitType.TOP_HOR_HIT) {
                flipHor = !flipHor;
            }
        }

        return hitVectors;
    }
}
