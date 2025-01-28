package org.aouessar.chessgame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState {

    private final int TILE_SIZE;

    private final int WIDTH; // Chessboard width

    private final int HEIGHT; // Chessboard height

    private boolean isGameOver = false;

    private boolean isWhiteTurn = true;


    public GameState(int tileSize, int width, int height) {
        TILE_SIZE = tileSize;
        WIDTH = width;
        HEIGHT = height;
    }


    public void switchTurn() {
        this.isWhiteTurn = !isWhiteTurn;
    }

}
