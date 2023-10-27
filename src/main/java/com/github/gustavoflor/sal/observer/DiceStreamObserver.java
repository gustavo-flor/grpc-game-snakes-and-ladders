package com.github.gustavoflor.sal.observer;

import com.github.gustavoflor.sal.protobuf.Dice;
import com.github.gustavoflor.sal.protobuf.Player;
import com.github.gustavoflor.sal.protobuf.State;
import io.grpc.stub.StreamObserver;

import java.util.Map;

import static java.util.concurrent.ThreadLocalRandom.current;

public class DiceStreamObserver implements StreamObserver<Dice> {

    private final StreamObserver<State> stateStreamObserver;
    private final Map<Integer, Integer> board;

    private Player clientPlayer;
    private Player serverPlayer;
    private boolean gameOver;

    public DiceStreamObserver(final Player clientPlayer,
                              final Player serverPlayer,
                              final StreamObserver<State> stateStreamObserver,
                              final Map<Integer, Integer> board) {
        this.stateStreamObserver = stateStreamObserver;
        this.serverPlayer = serverPlayer;
        this.clientPlayer = clientPlayer;
        this.board = board;
    }

    @Override
    public void onNext(final Dice dice) {
        clientPlayer = getNewPlayerPosition(clientPlayer, dice.getValue());

        if (clientPlayer.getPosition() != 100) {
            serverPlayer = getNewPlayerPosition(serverPlayer, current().nextInt(1, 7));
        }

        stateStreamObserver.onNext(getState());

        if (clientPlayer.getPosition() == 100 || serverPlayer.getPosition() == 100) {
            gameOver = true;
            stateStreamObserver.onCompleted();
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        if (!gameOver) {
            gameOver = true;
            stateStreamObserver.onCompleted();
        }
        System.out.println("Stream completed!");
    }

    private Player getNewPlayerPosition(final Player player, final int diceValue) {
        final var position = getPosition(player.getPosition() + diceValue);
        if (position <= 100) {
            return player.toBuilder()
                .setPosition(position)
                .build();
        }
        return player;
    }

    private State getState() {
        return State.newBuilder()
            .addPlayers(clientPlayer)
            .addPlayers(serverPlayer)
            .build();
    }

    private int getPosition(final int position) {
        return board.getOrDefault(position, position);
    }

}
