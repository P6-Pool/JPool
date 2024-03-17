package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.protobuf.JPoolAPIGrpc;


public class Main
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );


        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        JPoolAPIGrpc.JPoolAPIBlockingStub stub = JPoolAPIGrpc.newBlockingStub(channel);



//        stub.showShots()

        channel.shutdown();

    }
}
