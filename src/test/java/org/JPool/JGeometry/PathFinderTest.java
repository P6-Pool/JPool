package org.JPool.JGeometry;

import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {
    @BeforeEach
    void setUp() {
    }

    @Test
    void getTableIdxes() {
        ArrayList<Vector2d> actual = PathFinder.getTableIdxes(1);
        ArrayList<Vector2d> expected = new ArrayList<>() {{
            add(new Vector2d(0, -1));
            add(new Vector2d(1, 0));
            add(new Vector2d(0, 1));
            add(new Vector2d(-1, 0));
        }};

        assertArrayEquals(expected.toArray(), actual.toArray());

        actual = PathFinder.getTableIdxes(2);
        expected = new ArrayList<>() {{
            add(new Vector2d(0, -2));
            add(new Vector2d(1, -1));
            add(new Vector2d(2, 0));
            add(new Vector2d(1, 1));
            add(new Vector2d(0, 2));
            add(new Vector2d(-1, 1));
            add(new Vector2d(-2, 0));
            add(new Vector2d(-1, -1));
        }};

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void getProjectedBallPos() {
        Vector2d tableIdx = new Vector2d(1, 0);
        Vector2d endPoint = new Vector2d(0.5, 1.0);
        Vector2d actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        Vector2d expected = new Vector2d(1.67485, 1.0);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(2, 0);
        endPoint = new Vector2d(0.5, 1.0);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(2.61771, 1.0);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(1, 0);
        endPoint = new Vector2d(0.0, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(2.17485, 1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(2, 0);
        endPoint = new Vector2d(0.028575, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(2.14628, 1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(3, 0);
        endPoint = new Vector2d(0.0, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(4.29255, 1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-1, 0);
        endPoint = new Vector2d(0.028575, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(0.028575, 1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-2, 0);
        endPoint = new Vector2d(0.0, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(-2.1177,1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-3, 0);
        endPoint = new Vector2d(0.028575, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(-2.08913, 1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-4, 0);
        endPoint = new Vector2d(0.0, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(-4.2354,1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-2, 1);
        endPoint = new Vector2d(0.0, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(-2.1177,3.32785);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(1, 0);
        endPoint = new Vector2d(1.116 - 0.028575, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(1.116 - 0.028575, 1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(2, 0);
        endPoint = new Vector2d(1.116, 1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(3.2337, 1.087);;
        assertEquals(expected, actual);

        tableIdx = new Vector2d(0, 1);
        endPoint = new Vector2d(1.0668, 2.20743);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(1.0668, 2.20743);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(0, 1);
        endPoint = new Vector2d(1.0956,2.17859);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(1.0956, 2.23626);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-1, 0);
        endPoint = new Vector2d(0.02858,1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(0.02858,1.087);
        assertEquals(expected, actual);

        tableIdx = new Vector2d(-2, 0);
        endPoint = new Vector2d(0.02858,1.087);
        actual = PathFinder.getProjectedBallPos(tableIdx, endPoint);
        expected = new Vector2d(-2.08913,1.087);
        assertEquals(expected, actual);
    }

    @Test
    void getKissTargetPoses() {
//        Ball ball = new Ball(0, 0, 2, new Vector2d(10, 10));
//        Ball target = new Ball(0, 0, 2, new Vector2d(14, 22));
//
//        ArrayList<Vector2d> actual = PathFinder.getKissTargetPoses(ball, target);
//        ArrayList<Vector2d> expected = new ArrayList<>() {{
//            add(new Vector2d(6.8, 12.4));
//            add(new Vector2d(14, 10));
//        }};
//
//        assertArrayEquals(expected.toArray(), actual.toArray());
//
//        ball.pos = new Vector2d(10, 10);
//        target.pos = new Vector2d(10, 18);
//
//        actual = PathFinder.getKissTargetPoses(ball, target);
//        expected = new ArrayList<>() {{
//            add(new Vector2d(6.5359, 12));
//            add(new Vector2d(13.4641, 12));
//        }};
//
//        assertArrayEquals(expected.toArray(), actual.toArray());
//
//        ball.pos = new Vector2d(10, 10);
//        target.pos = new Vector2d(4, 20);
//
//        actual = PathFinder.getKissTargetPoses(ball, target);
//        expected = new ArrayList<>() {{
//            add(new Vector2d(6.0722, 9.2433));
//            add(new Vector2d(12.516, 13.1096));
//        }};
//
//        assertArrayEquals(expected.toArray(), actual.toArray());
//
//        ball.pos = new Vector2d(10, 10);
//        target.pos = new Vector2d(4, 4);
//
//        actual = PathFinder.getKissTargetPoses(ball, target);
//        expected = new ArrayList<>() {{
//            add(new Vector2d(11.1611, 6.1722));
//            add(new Vector2d(6.1722, 11.1611));
//        }};
//
//        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void getLineCircleIntersectionsTest() {
        ArrayList<Vector2d> actual = PathFinder.getLineCircleIntersections(new Vector2d(2, 4), new Vector2d(6, 6), new Vector2d(10, 6), 3);
        ArrayList<Vector2d> expected = new ArrayList<>(){{add(new Vector2d(11.3541, 8.677)); add(new Vector2d(7.0459, 6.523)); }};
        assertArrayEquals(expected.toArray(), actual.toArray());

        actual = PathFinder.getLineCircleIntersections(new Vector2d(2, 4), new Vector2d(-4, 2), new Vector2d(-18, -2), 2);
        expected = new ArrayList<>(){{add(new Vector2d(-19.6, -3.2)); add(new Vector2d(-16, -2)); }};
        assertArrayEquals(expected.toArray(), actual.toArray());

        actual = PathFinder.getLineCircleIntersections(new Vector2d(2, 4), new Vector2d(-4, 2), new Vector2d(-18, -1), 1);
        expected = new ArrayList<>();
        assertArrayEquals(expected.toArray(), actual.toArray());

        actual = PathFinder.getLineCircleIntersections(new Vector2d(1, 4), new Vector2d(2, 4), new Vector2d(6, 5), 1);
        expected = new ArrayList<>(){{add(new Vector2d(6, 4));}};
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void isLineSegmentAndBallCollidingTest() {
        boolean actual = PathFinder.isLineSegmentAndBallColliding(new Vector2d(2, 4), new Vector2d(6, 6), new Vector2d(10, 6), 3);
        assertFalse(actual);

        actual = PathFinder.isLineSegmentAndBallColliding(new Vector2d(2, 4), new Vector2d(8, 6), new Vector2d(10, 6), 3);
        assertTrue(actual);

        actual = PathFinder.isLineSegmentAndBallColliding(new Vector2d(2, 4), new Vector2d(-4, 2), new Vector2d(-18, -2), 2);
        assertFalse(actual);

        actual = PathFinder.isLineSegmentAndBallColliding(new Vector2d(2, 4), new Vector2d(-4, 2), new Vector2d(-18, -1), 1);
        assertFalse(actual);

        actual = PathFinder.isLineSegmentAndBallColliding(new Vector2d(1, 4), new Vector2d(2, 4), new Vector2d(6, 4), 4);
        assertTrue(actual);
    }

    @Test
    void getRailHitsTest() {
        Vector2d startPoint = new Vector2d(0.5, 1.0);
        Vector2d endPoint = new Vector2d(0.6, 1.1);
        ArrayList<Vector2d> actual = PathFinder.getHitProjections(startPoint, endPoint);
        ArrayList<Vector2d> expected = new ArrayList<>(){{add(new Vector2d(0.1, 0.1));}};
        assertArrayEquals(expected.toArray(), actual.toArray());


        startPoint = new Vector2d(0.4, 1.4);
        endPoint = new Vector2d(2.64343, 2.03986);
        actual = PathFinder.getHitProjections(startPoint, endPoint);
        expected = new ArrayList<>(){{
            add(new Vector2d(0.6874, 0.1961));
            add(new Vector2d(-1.0589, 0.302));
            add(new Vector2d(0.4972, 0.1418));
        }};
        assertArrayEquals(expected.toArray(), actual.toArray());


        startPoint = new Vector2d(0.2, 1.2);
        endPoint = new Vector2d(3.649, 0.87282);
        actual = PathFinder.getHitProjections(startPoint, endPoint);
        expected = new ArrayList<>(){{
            add(new Vector2d(0.8874, -0.0842));
            add(new Vector2d(-1.0589, -0.1004));
            add(new Vector2d(1.0589, -0.1004));
            add(new Vector2d(-0.4439, -0.0421));
        }};
        assertArrayEquals(expected.toArray(), actual.toArray());


        startPoint = new Vector2d(0.4, 0.6);
        endPoint = new Vector2d(-0.81009, -0.96091);
        actual = PathFinder.getHitProjections(startPoint, endPoint);
        expected = new ArrayList<>(){{
            add(new Vector2d(-0.3714, -0.4791));
            add(new Vector2d(0.0716, -0.0923));
            add(new Vector2d(0.7671, 0.9895));
        }};
        assertArrayEquals(expected.toArray(), actual.toArray());


        startPoint = new Vector2d(0.1, 0.373);
        endPoint = new Vector2d(2.17485, 1.087);
        actual = PathFinder.getHitProjections(startPoint, endPoint);
        expected = new ArrayList<>(){{
            add(new Vector2d(0.98743, 0.33979));
            add(new Vector2d(-1.05885, 0.36437));
            add(new Vector2d(0.02858, 0.00983));
        }};
        assertArrayEquals(expected.toArray(), actual.toArray());


        startPoint = new Vector2d(0.70095, 2.09721);
        endPoint = new Vector2d(1.0668, 2.20743);
        actual = PathFinder.getHitProjections(startPoint, endPoint);
        expected = new ArrayList<>(){{
            add(new Vector2d(0.36586, 0.11022));
            add(new Vector2d(0, 0));
        }};
        assertArrayEquals(expected.toArray(), actual.toArray());


        startPoint = new Vector2d(0.70095, 2.09721);
        endPoint = new Vector2d(1.08743, 2.22807);
        actual = PathFinder.getHitProjections(startPoint, endPoint);
        expected = new ArrayList<>(){{
            add(new Vector2d(0.3255, 0.11022));
            add(new Vector2d(0.06098, -0.02065));
            add(new Vector2d(0, 0));
        }};
        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}