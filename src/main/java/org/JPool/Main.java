package org.JPool;

import org.JPool.FastFiz.TableState;
import org.JPool.Grpc.Client;
import org.JPool.JGeometry.JShotStep;
import org.JPool.JGeometry.ShotTree;

import java.util.ArrayList;


public class Main
{
    public static void main( String[] args )
    {
//        Client clientP5 = new Client("localhost", 50051);
        Client clientGG = new Client("localhost", 50052);
        TableState activeTableState = TableState.randomTableState(8);
        ArrayList<JShotStep> shots = ShotTree.generateShotTree(activeTableState, 5, TableState.playerPattern.SOLID);
//        clientP5.showShots(shots, activeTableState);
        clientGG.showShots(shots, activeTableState);
    }
}
