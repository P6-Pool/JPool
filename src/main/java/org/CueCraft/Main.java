package org.CueCraft;

import org.CueCraft.Pool.Table;
import org.CueCraft.Grpc.Client;
import org.CueCraft.ShotGenerator.ShotStep;
import org.CueCraft.ShotGenerator.ShotGenerator;

import java.util.ArrayList;

public class Main
{
    static {
        String workingDir = System.getProperty("user.dir");
        String libraryPath = workingDir + "/src/main/resources/lib/libfastfiz.so";
        System.load(libraryPath);
    }

    public static void main( String[] args )
    {
        Client client = new Client("localhost", 50052);

        Table activeTable = Table.randomTableState(15);

        ArrayList<ShotStep> shots = ShotGenerator.generateShots(activeTable, 5, Table.playerPattern.SOLID);

        client.showShots(shots, activeTable);
    }
}
