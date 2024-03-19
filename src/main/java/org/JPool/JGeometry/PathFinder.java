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

    public static ArrayList<Vector2d> getKissTargetPoses(Ball ball, Ball target) {
        Vector2d diff = target.pos.sub(ball.pos);
        double hyp = diff.mag();
        double adj = ball.radius * 2;

        double angle = Math.acos(adj / hyp);

        Vector2d leftKiss = diff.norm().rotateClockwise(-angle).mult(adj);
        Vector2d rightKiss = diff.norm().rotateClockwise(angle).mult(adj);

        return new ArrayList<>() {{
            add(ball.pos.add(leftKiss));
            add(ball.pos.add(rightKiss));
        }};
    }

    public static Vector2d adjustLeftMostTarget(Ball mainBall, ArrayList<Ball> balls, JShotStep step, int depth) {
        step = step.copy();

        if (mainBall.number == 0) {
            int b1 = step.b1;
            if (step.b1 == 2) {
                System.out.println("jeee");
            }
        }

        Vector2d adjustedTargetPos = step.leftMost;
        Vector2d leftMostOffset = step.leftMost.sub(mainBall.pos).norm().rotateClockwise(-Math.PI / 2).mult(Ball.radius);
        Vector2d lineSegStartPoint = mainBall.pos.add(leftMostOffset);
        Vector2d lineSegEndPoint = step.leftMost.add(leftMostOffset);

        for (Ball ball : balls) {
            if (ball.number == mainBall.number || ball.state == 0 || ball.number == step.b1) {
                continue;
            }

            if (!isLineSegmentAndBallColliding(lineSegStartPoint, lineSegEndPoint, ball.pos, Ball.radius)) {
                continue;
            }

            double c = ball.pos.sub(mainBall.pos).mag();
            double a = Ball.radius * 2;
            double angleAC = Math.acos(a / c);

            Vector2d ballTangentOffset = mainBall.pos.sub(ball.pos).norm().rotateClockwise(-angleAC).mult(Ball.radius);
            Vector2d adjustedLinePointQ = ball.pos.add(ballTangentOffset);
            Vector2d adjustedLinePointP = mainBall.pos.sub(ballTangentOffset);

            if (step.type == JShotStep.JShotStepType.POCKET || step.type == JShotStep.JShotStepType.RAIL) {
                Vector2d adjustedLineDiff = adjustedLinePointQ.sub(adjustedLinePointP);
                double a1 = adjustedLineDiff.y / adjustedLineDiff.x;
                double b1 = adjustedLinePointQ.y - a1 * adjustedLinePointQ.x;

                Vector2d leftRightLineDiff = step.rightMost.sub(step.leftMost);
                double a2 = leftRightLineDiff.y / leftRightLineDiff.x;
                double b2 = step.rightMost.y - a2 * step.rightMost.x;

                double x = (b2 - b1) / (a1 - a2);
                double y = a1 * x + b1;

                Vector2d adjustedLeftMostLeftMost = new Vector2d(x, y);

                adjustedTargetPos = adjustedLeftMostLeftMost.add(step.rightMost.sub(step.leftMost).norm().mult(Ball.radius));

                if (depth > 0) {
                    step.leftMost = adjustedTargetPos;
                    return adjustLeftMostTarget(mainBall, balls, step, depth - 1);
                }
            } else {
                if (isLineAndBallColliding(adjustedLinePointP, adjustedLinePointQ, step.leftMost, Ball.radius) ||
                        isLineAndBallColliding(adjustedLinePointP, adjustedLinePointQ, step.posB1, Ball.radius)) {
                    ArrayList<Vector2d> intersections = getLineCircleIntersections(
                            mainBall.pos,
                            mainBall.pos.add(adjustedLinePointQ.sub(adjustedLinePointP)),
                            step.posB1,
                            Ball.radius * 2
                    );
                    System.out.println(intersections);
                    adjustedTargetPos = intersections.get(intersections.indexOf(Collections.min(intersections)));
                    if (depth > 0) {
                        step.leftMost = adjustedTargetPos;
                        return adjustLeftMostTarget(mainBall, balls, step, depth - 1);
                    }
                }
                else {
                    return null;
                }

            }
        }

        return adjustedTargetPos;
    }

    public static Vector2d adjustRightMostTarget(Ball mainBall, ArrayList<Ball> balls, JShotStep step, int depth) {
        step = step.copy();

        if (mainBall.number == 1) {
            int b1 = step.b1;
            if (step.b1 == -1) {
                System.out.println("jeee");
            }
        }

        Vector2d adjustedTargetPos = step.rightMost;
        Vector2d rightMostOffset = step.rightMost.sub(mainBall.pos).norm().rotateClockwise(Math.PI / 2).mult(Ball.radius);
        Vector2d lineSegStartPoint = mainBall.pos.add(rightMostOffset);
        Vector2d lineSegEndPoint = step.rightMost.add(rightMostOffset);

        for (Ball ball : balls) {
            if (ball.number == mainBall.number || ball.state == 0 || ball.number == step.b1) {
                continue;
            }

            if (!isLineSegmentAndBallColliding(lineSegStartPoint, lineSegEndPoint, ball.pos, Ball.radius)) {
                continue;
            }

            double c = ball.pos.sub(mainBall.pos).mag();
            double a = Ball.radius * 2;
            double angleAC = Math.acos(a / c);

            Vector2d ballTangentOffset = mainBall.pos.sub(ball.pos).norm().rotateClockwise(angleAC).mult(Ball.radius);
            Vector2d adjustedLinePointQ = ball.pos.add(ballTangentOffset);
            Vector2d adjustedLinePointP = mainBall.pos.sub(ballTangentOffset);

            if (step.type == JShotStep.JShotStepType.POCKET || step.type == JShotStep.JShotStepType.RAIL) {
                Vector2d adjustedLineDiff = adjustedLinePointQ.sub(adjustedLinePointP);
                double a1 = adjustedLineDiff.y / adjustedLineDiff.x;
                double b1 = adjustedLinePointQ.y - a1 * adjustedLinePointQ.x;

                Vector2d leftRightLineDiff = step.leftMost.sub(step.rightMost);
                double a2 = leftRightLineDiff.y / leftRightLineDiff.x;
                double b2 = step.leftMost.y - a2 * step.leftMost.x;

                double x = (b2 - b1) / (a1 - a2);
                double y = a1 * x + b1;

                Vector2d adjustedRightMostRightMost = new Vector2d(x, y);

                adjustedTargetPos = adjustedRightMostRightMost.add(step.leftMost.sub(step.rightMost).norm().mult(Ball.radius));

                if (depth > 0) {
                    step.rightMost = adjustedTargetPos;
                    return adjustRightMostTarget(mainBall, balls, step, depth - 1);
                }
            } else {
                if (isLineAndBallColliding(adjustedLinePointP, adjustedLinePointQ, step.rightMost, Ball.radius) ||
                        isLineAndBallColliding(adjustedLinePointP, adjustedLinePointQ, step.posB1, Ball.radius)) {
                    ArrayList<Vector2d> intersections = getLineCircleIntersections(
                            mainBall.pos,
                            mainBall.pos.add(adjustedLinePointQ.sub(adjustedLinePointP)),
                            step.posB1,
                            Ball.radius * 2
                    );
                    System.out.println(intersections);
                    adjustedTargetPos = intersections.get(intersections.indexOf(Collections.min(intersections)));
                    if (depth > 0) {
                        step.rightMost = adjustedTargetPos;
                        return adjustRightMostTarget(mainBall, balls, step, depth - 1);
                    }
                }
                else {
                    return null;
                }

            }
        }

        return adjustedTargetPos;
    }

    public static boolean isLineAndBallColliding(Vector2d linePointP, Vector2d linePointQ, Vector2d ballOrigin, double ballRadius) {
        // https://www.baeldung.com/cs/circle-line-segment-collision-detection

        Vector2d pq = linePointQ.sub(linePointP);
        Vector2d op = linePointP.sub(ballOrigin);
        Vector2d oq = linePointQ.sub(ballOrigin);

        double triangleArea = Math.abs(op.cross(oq)) / 2;
        double minDist = 2 * triangleArea / pq.mag();

        return minDist <= ballRadius;
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
