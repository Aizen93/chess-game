package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.Board;
import org.aouessar.chessgame.Color;

@Getter
@Setter
public abstract class Piece {

    private final Color color; // Indicates whether the piece is white or black

    private final int row;

    private final int col;

    private final Image icon;


    public Piece(Color color, int row, int col, Image icon) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.icon = icon;
    }


    // Abstract method to check if a move is valid
    abstract boolean isValidMove(int startX, int startY, int endX, int endY, Board board);


    // Check if the destination is occupied by a friendly piece
    protected boolean isFriendlyPiece(int x, int y, Board board) {
        return board.getBoard()[x][y] != null && color.equals(board.getBoard()[x][y].color);
    }
}