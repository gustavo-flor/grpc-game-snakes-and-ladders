package com.github.gustavoflor.sal;

import com.github.gustavoflor.sal.service.GameService;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        final var port = 9090;
        final var server = ServerBuilder.forPort(port)
            .addService(new GameService())
            .build();

        server.start();
        System.out.printf("gRPC server is running at port %s.%n", port);

        server.awaitTermination();
    }


}
