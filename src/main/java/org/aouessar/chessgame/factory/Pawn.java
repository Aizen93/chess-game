package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import org.aouessar.chessgame.Color;

public class Pawn extends Piece{

    public Pawn(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int direction = isWhite() ? -1 : 1;

        // Standard single move
        if (endX == startX + direction && endY == startY && board[endX][endY] == null) {
            return true;
        }

        // Double move from starting position
        int startRow = isWhite() ? 6 : 1;
        if (startX == startRow && endX == startX + 2 * direction && endY == startY && board[endX][endY] == null && board[startX + direction][startY] == null) {
            return true;
        }

        // Diagonal capture
        return endX == startX + direction && Math.abs(endY - startY) == 1 && board[endX][endY] != null && !isFriendlyPiece(endX, endY, board);// Invalid move
    }
}
