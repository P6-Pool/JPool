package org.CueCraft.ShotGenerator;

import org.CueCraft.Pool.Ball;
import org.CueCraft.Pool.Table;

import java.util.ArrayList;

class ShotTreeTest {

    ArrayList<Ball> balls;
    Table oneBallState, fiveBallState;

//    @BeforeEach
//    void setUp() {
//        balls = new ArrayList<>();
//
//        for (int i = 0; i < 16; i++) {
//            balls.add(new Ball(i, 1, 0.2, new Vector2d(5, 20 - (i + 1) * 2.5)));
//        }
//        oneBallState = new TableState(10, 20, new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));}}, 2, 2);
//        fiveBallState = new TableState(10, 20, new ArrayList<>(){{add(balls.get(0)); add(balls.get(1));add(balls.get(2));add(balls.get(3));add(balls.get(4));add(balls.get(5));}}, 2, 2);
//    }
//
//    @Test
//    void generateShotTree() {
//        ShotTree.generateShotTree(fiveBallState, 2, TableState.playerPattern.SOLID);
//    }
}