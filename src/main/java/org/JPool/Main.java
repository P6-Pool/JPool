package org.JPool;

import org.JPool.FastFiz.TableState;
import org.JPool.Grpc.Client;
import org.JPool.JGeometry.ShotTree;


public class Main
{
    public static void main( String[] args )
    {
//        Client clientP5 = new Client("localhost", 50051);
        Client clientGG = new Client("localhost", 50052);
        TableState activeTableState = TableState.randomTableState(8);
//        clientP5.showShots(ShotTree.generateShotTree(activeTableState, 3, TableState.playerPattern.SOLID), activeTableState);
        clientGG.showShots(ShotTree.generateShotTree(activeTableState, 3, TableState.playerPattern.SOLID), activeTableState);
    }
}
