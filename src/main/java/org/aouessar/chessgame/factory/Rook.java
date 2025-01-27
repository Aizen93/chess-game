package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.Color;

@Setter
@Getter
public class Rook extends Piece {

    private boolean hasMoved;



    public Rook(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }



    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        if (startX != endX && startY != endY) {
            return false; // Must move in a straight line
        }

        // Determine the direction of movement and check for obstacles
        int stepX = Integer.signum(endX - startX); // -1, 0, or 1
        int stepY = Integer.signum(endY - startY); // -1, 0, or 1

        int currentX = startX + stepX;
        int currentY = startY + stepY;

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



    public boolean hasMoved() {
        return hasMoved;
    }

}
