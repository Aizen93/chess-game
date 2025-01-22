package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import org.aouessar.chessgame.Board;
import org.aouessar.chessgame.Color;

public class Knight extends Piece {

    public Knight(Color color, int row, int col, Image icon) {
        super(color, row, col, icon);
    }

    @Override
    boolean isValidMove(int startX, int startY, int endX, int endY, Board board) {
        return false;
    }
}