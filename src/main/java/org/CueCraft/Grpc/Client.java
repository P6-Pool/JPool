package org.CueCraft.Grpc;

import CueZone.GameSummary;
import CueZone.Turn;
import JFastfiz.GameShot;
import JFastfiz.ShotParams;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.CueCraft.Pool.Ball;
import org.CueCraft.Pool.Table;
import org.CueCraft.ShotGenerator.ShotStep;
import org.CueCraft.ShotGenerator.Vector2d;
import org.CueCraft.protobuf.CueCanvasAPIGrpc;
import org.CueCraft.protobuf.ShowShotsRequest;
import org.CueCraft.protobuf.Shot;
import org.CueCraft.protobuf.ShotType;
import org.CueCraft.protobuf.Point;

import java.util.ArrayList;

public class Client {
    ManagedChannel channel;
    CueCanvasAPIGrpc.CueCanvasAPIBlockingStub stub;

    public Client(String address, int port) {
        channel = ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
        stub = CueCanvasAPIGrpc.newBlockingStub(channel);
    }

    public Empty showShots(ArrayList<ShotStep> shots, Table table) {

        ArrayList<Shot> serShots = new ArrayList<>();

        for (ShotStep shot : shots) {
            serShots.add(serializeShot(shot));
        }

        ShowShotsRequest req = ShowShotsRequest.newBuilder()
                .addAllShots(serShots)
                .setTableState(serializeTable(table))
                .build();

        return stub.showShots(req);
    }

    public Empty showGames(ArrayList<GameSummary> summaries) {
        ArrayList<org.CueCraft.protobuf.Game> serGames = new ArrayList<>();

        for (var summary : summaries) {
            serGames.add(serializeGame(summary));
        }

        org.CueCraft.protobuf.ShowGamesRequest req = org.CueCraft.protobuf.ShowGamesRequest.newBuilder()
                .addAllGames(serGames)
                .build();

        return stub.showGames(req);
    }

    public void shutdown() {
        channel.shutdown();
    }

    private Shot serializeShot(ShotStep shot) {
        Shot.Builder builder = Shot.newBuilder()
                .setType(serializeShotType(shot.type))
                .setB1(shot.b1)
                .setB2(shot.b2)
                .setId(shot.id);

        if (shot.next != null) builder.setNext(serializeShot(shot.next));
        if (shot.branch != null) builder.setBranch(serializeShot(shot.branch));
        if (shot.posB1 != null) builder.setPosB1(serializeVector2d(shot.posB1));
        if (shot.ghostBallPos != null) builder.setGhostBall(serializeVector2d(shot.ghostBallPos));
        if (shot.leftMost != null) builder.setLeftMost(serializeVector2d(shot.leftMost));
        if (shot.rightMost != null) builder.setRightMost(serializeVector2d(shot.rightMost));

        return builder.build();
    }

    private static ShotType serializeShotType(ShotStep.ShotStepType type) {
        return ShotType.forNumber(type.ordinal());
    }

    private static Point serializeVector2d(Vector2d vec) {
        return Point.newBuilder().setX(vec.x).setY(vec.y).build();
    }

    private static org.CueCraft.protobuf.TableState serializeTable(Table table) {
        ArrayList<org.CueCraft.protobuf.Ball> serBalls = new ArrayList<>();

        for (Ball ball : table.balls) {
            serBalls.add(serializeBall(ball));
        }

        return org.CueCraft.protobuf.TableState.newBuilder()
                .addAllBalls(serBalls)
                .build();
    }

    private static org.CueCraft.protobuf.Ball serializeBall(Ball ball) {
        return org.CueCraft.protobuf.Ball.newBuilder()
                .setPos(serializeVector2d(ball.pos))
                .setNumber(ball.number)
                .setState(ball.state)
                .build();
    }

    public static org.CueCraft.protobuf.Game serializeGame(GameSummary summary) {
        ArrayList<org.CueCraft.protobuf.GameTurn> serTurns = new ArrayList<>();

        for (var turn : summary.turnHistory()) {
            serTurns.add(serializeGameTurn(turn));
        }

        return org.CueCraft.protobuf.Game.newBuilder()
                .addAllTurnHistory(serTurns)
                .build();
    }

    private static org.CueCraft.protobuf.GameTurn serializeGameTurn(Turn turn) {
        return org.CueCraft.protobuf.GameTurn.newBuilder()
                .setTurnType(turn.turnType().toString())
                .setAgentName(turn.player().getName())
                .setTableStateBefore(serializeTable(Table.fromTableState(turn.tableStateBefore())))
                .setTableStateAfter(serializeTable(Table.fromTableState(turn.tableStateAfter())))
                .setGameShot(serializeGameShot(turn.gameShot()))
                .setShotResult(turn.shotResult().toString())
                .build();
    }

    private static org.CueCraft.protobuf.GameShot serializeGameShot(GameShot gameShot) {
        return org.CueCraft.protobuf.GameShot.newBuilder()
                .setBallTarget(gameShot.getBall().toString())
                .setPocketTarget(gameShot.getPocket().toString())
                .setCuePos(serializeVector2d(new Vector2d(gameShot.getCue_x(), gameShot.getCue_y())))
                .setDecision(gameShot.getDecision().toString())
                .setShotParams(serializeShotParams(gameShot.getParams()))
                .build();
    }

    private static org.CueCraft.protobuf.ShotParams serializeShotParams(ShotParams gameShot) {
        return org.CueCraft.protobuf.ShotParams.newBuilder()
                .setA(gameShot.getA())
                .setB(gameShot.getB())
                .setPhi(gameShot.getPhi())
                .setTheta(gameShot.getTheta())
                .setV(gameShot.getV())
                .build();
    }
}
