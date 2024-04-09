package org.CueCraft;

import org.CueCraft.FastFiz.TableState;
import org.CueCraft.Grpc.Client;
import org.CueCraft.Geometry.ShotStep;
import org.CueCraft.Geometry.ShotTree;

import java.util.ArrayList;


public class Main
{
    public static void main( String[] args )
    {
//        Client clientP5 = new Client("localhost", 50051);
        Client clientGG = new Client("localhost", 50052);
        TableState activeTableState = TableState.randomTableState(15);
        ArrayList<ShotStep> shots = ShotTree.generateShotTree(activeTableState, 5, TableState.playerPattern.SOLID);
//        clientP5.showShots(shots, activeTableState);
        clientGG.showShots(shots, activeTableState);
    }
}
