package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.Color;

@Getter
@Setter
public class King extends Piece {

    private boolean hasMoved;

    public King(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // The King can move one square in any direction
        if (dx <= 1 && dy <= 1) {
            // Ensure the destination square is not occupied by a friendly piece
            return !isFriendlyPiece(endX, endY, board);
        }

        /*
        if (isCastlingMove(startX, startY, endX, endY, board)) {
            return true;
        }*/

        return false;
    }


    /*private boolean isCastlingMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        // Castling only occurs along the same row
        if (startX != endX || (Math.abs(endY - startY) != 2)) return false;

        // Ensure King and Rook haven't moved
        if (hasMoved() || hasRookMoved(startX, endY > startY ? 7 : 0)) return false;

        // Check that the path is clear
        int stepY = (endY > startY) ? 1 : -1;
        for (int y = startY + stepY; y != endY; y += stepY) {
            if (board[startX][y] != null) return false;
        }

        // Ensure the King isn't in check or passing through attacked squares
        return !isInCheck(startX, startY) &&
                !isInCheck(startX, startY + stepY) &&
                !isInCheck(startX, endY);
    }*/
}
