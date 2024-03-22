package org.JPool.Grpc;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.JPool.FastFiz.Ball;
import org.JPool.FastFiz.TableState;
import org.JPool.JGeometry.JShotStep;
import org.JPool.JGeometry.Vector2d;
import org.JPool.protobuf.JPoolAPIGrpc;
import org.JPool.protobuf.ShowShotsRequest;
import org.JPool.protobuf.Shot;
import org.JPool.protobuf.ShotType;
import org.JPool.protobuf.Point;

import java.util.ArrayList;

public class Client {
    ManagedChannel channel;
    JPoolAPIGrpc.JPoolAPIBlockingStub stub;

    public Client(String address, int port) {
        channel = ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
        stub = JPoolAPIGrpc.newBlockingStub(channel);
    }

    public Empty showShots(ArrayList<JShotStep> shots, TableState tableState) {

        ArrayList<Shot> serShots = new ArrayList<>();

        for (JShotStep shot : shots) {
            serShots.add(convertShotToSerShot(shot));
        }

        ShowShotsRequest req = ShowShotsRequest.newBuilder()
                .addAllShots(serShots)
                .setTableState(ConvertTableStateToSerTableState(tableState))
                .build();

        return stub.showShots(req);
    }

    public void shutdown() {
        channel.shutdown();
    }

    private Shot convertShotToSerShot(JShotStep shot) {
        Shot.Builder builder = Shot.newBuilder()
                .setType(convertJShotStepTypeToSerShotType(shot.type))
                .setB1(shot.b1)
                .setB2(shot.b2)
                .setId(shot.id);

        if (shot.next != null) builder.setNext(convertShotToSerShot(shot.next));
        if (shot.branch != null) builder.setBranch(convertShotToSerShot(shot.branch));
        if (shot.posB1 != null) builder.setPosB1(convertVector2DToSerPoint(shot.posB1));
        if (shot.ghostBallPos != null) builder.setGhostBall(convertVector2DToSerPoint(shot.ghostBallPos));
        if (shot.leftMost != null) builder.setLeftMost(convertVector2DToSerPoint(shot.leftMost));
        if (shot.rightMost != null) builder.setRightMost(convertVector2DToSerPoint(shot.rightMost));

        return builder.build();
    }

    private ShotType convertJShotStepTypeToSerShotType(JShotStep.JShotStepType type) {
        return ShotType.forNumber(type.ordinal());
    }

    private Point convertVector2DToSerPoint(Vector2d vec) {
        return Point.newBuilder().setX(vec.x).setY(vec.y).build();
    }

    private org.JPool.protobuf.TableState ConvertTableStateToSerTableState(TableState tableState) {
        ArrayList<org.JPool.protobuf.Ball> serBalls = new ArrayList<>();

        for (Ball ball : tableState.balls) {
            serBalls.add(ConvertBallToSerBall(ball));
        }

        return org.JPool.protobuf.TableState.newBuilder()
                .addAllBalls(serBalls)
                .build();
    }

    private org.JPool.protobuf.Ball ConvertBallToSerBall(Ball ball) {
        return org.JPool.protobuf.Ball.newBuilder()
                .setPos(convertVector2DToSerPoint(ball.pos))
                .setNumber(ball.number)
                .setState(ball.state)
                .build();
    }
}
