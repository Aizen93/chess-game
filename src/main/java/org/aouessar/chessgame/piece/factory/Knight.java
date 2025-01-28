package org.aouessar.chessgame.piece.factory;

import javafx.scene.image.Image;
import org.aouessar.chessgame.domain.Color;
import org.aouessar.chessgame.piece.Piece;

public class Knight extends Piece {

    public Knight(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // Check for L-shape movement
        if (dx * dy == 2) {
            return !isFriendlyPiece(endX, endY, board);
        }

        return false;
    }



    @Override
    public String getUniCode() {
        return this.getColor().equals(Color.WHITE) ?  "♘" : "♞";
    }

}
