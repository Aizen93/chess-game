package org.aouessar.chessgame.piece.factory;

import javafx.scene.image.Image;
import org.aouessar.chessgame.domain.Color;
import org.aouessar.chessgame.piece.Piece;

public class Queen extends Piece {

    public Queen(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }



    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // Ensure the move is either straight (like a Rook) or diagonal (like a Bishop)
        if (dx != dy && startX != endX && startY != endY) {
            return false;
        }

        // Determine the direction of movement
        int stepX = Integer.signum(endX - startX); // -1, 0, or 1
        int stepY = Integer.signum(endY - startY); // -1, 0, or 1

        int currentX = startX + stepX;
        int currentY = startY + stepY;

        // Check for obstacles along the path
        while (currentX != endX || currentY != endY) {
            if (board[currentX][currentY] != null) {
                return false; // Path is blocked
            }
            currentX += stepX;
            currentY += stepY;
        }

        // Ensure the destination square is not occupied by a friendly piece
        return !isFriendlyPiece(endX, endY, board);
    }



    @Override
    public String getUniCode() {
        return this.getColor().equals(Color.WHITE) ?  "♕" : "♛";
    }
}
