package org.CueCraft;

import JFastfiz.ShotParams;
import org.CueCraft.Pool.Table;
import org.CueCraft.Grpc.Client;
import org.CueCraft.ShotEvaluator.ShotEvaluator;
import org.CueCraft.ShotGenerator.ShotStep;
import org.CueCraft.ShotGenerator.ShotGenerator;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    static {
        String workingDir = System.getProperty("user.dir");
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    public static void main( String[] args )
    {
        Client client = new Client("localhost", 50051);

        Table activeTable = Table.randomSequentialTableState(15);

//        ArrayList<ShotStep> shots = ShotGenerator.generateShots(activeTable, 3, Table.PlayerPattern.SOLID);

        ShotEvaluator.ShotDecider shotDecider = (generator, table, playerPattern) -> ShotEvaluator.monteCarloTreeSearch(table, 1, 400, generator, playerPattern);
        Triplet<Double, ShotStep, ShotParams> shotParams = ShotEvaluator.getBestShot(activeTable, 2, Table.PlayerPattern.SOLID, shotDecider, ShotEvaluator::rewardShotSimple);

        client.showShots(new ArrayList<>(List.of(shotParams.getValue1())), activeTable);


    }
}
