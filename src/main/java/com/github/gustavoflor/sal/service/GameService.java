package com.github.gustavoflor.sal.service;

import com.github.gustavoflor.sal.observer.DiceStreamObserver;
import com.github.gustavoflor.sal.protobuf.Dice;
import com.github.gustavoflor.sal.protobuf.GameServiceGrpc;
import com.github.gustavoflor.sal.protobuf.Player;
import com.github.gustavoflor.sal.protobuf.State;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class GameService extends GameServiceGrpc.GameServiceImplBase {

    private static final Map<Integer, Integer> BOARD = new HashMap<>();

    static {
        // Snakes
        BOARD.put(32, 10);
        BOARD.put(36, 6);
        BOARD.put(48, 26);
        BOARD.put(62, 18);
        BOARD.put(88, 24);
        BOARD.put(95, 56);
        BOARD.put(97, 78);

        // Ladders
        BOARD.put(1, 38);
        BOARD.put(4, 14);
        BOARD.put(8, 30);
        BOARD.put(21, 42);
        BOARD.put(28, 76);
        BOARD.put(50, 67);
        BOARD.put(71, 92);
        BOARD.put(80, 99);
    }

    @Override
    public StreamObserver<Dice> play(final StreamObserver<State> responseObserver) {
        final var clientPlayer = Player.newBuilder()
            .setName("Client")
            .setPosition(0)
            .build();
        final var serverPlayer = Player.newBuilder()
            .setName("Server")
            .setPosition(0)
            .build();
        return new DiceStreamObserver(clientPlayer, serverPlayer, responseObserver, BOARD);
    }
}
