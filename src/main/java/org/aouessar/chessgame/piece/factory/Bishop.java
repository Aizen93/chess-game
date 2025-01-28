package org.aouessar.chessgame.piece.factory;

import javafx.scene.image.Image;
import org.aouessar.chessgame.domain.Color;
import org.aouessar.chessgame.piece.Piece;

public class Bishop extends Piece {


    public Bishop(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        if (dx != dy) {
            return false; // Must move diagonally
        }

        // Check for obstacles along the path
        int stepX = Integer.signum(endX - startX);
        int stepY = Integer.signum(endY - startY);

        for (int x = startX + stepX, y = startY + stepY; x != endX; x += stepX, y += stepY) {
            if (board[x][y] != null) {
                return false;
            }
        }

        return !isFriendlyPiece(endX, endY, board);
    }

    @Override
    public String getUniCode() {
        return this.getColor().equals(Color.WHITE) ?  "♗" : "♝";
    }
}