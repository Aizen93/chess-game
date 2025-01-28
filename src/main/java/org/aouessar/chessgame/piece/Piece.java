package org.aouessar.chessgame.piece;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.domain.Color;

@Getter
@Setter
public abstract class Piece {

    private final char name;

    private final Color color;

    private final ImageView icon;

    private int row;

    private int col;

    protected boolean hasMoved;




    public Piece(char name, Color color, int row, int col, Image icon) {
        this.name = name;
        this.color = color;
        this.row = row;
        this.col = col;
        this.icon = new ImageView(icon);
    }


    // Abstract method to check if a move is valid
    public abstract boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board);


    // Check if the destination is occupied by a friendly piece
    public boolean isFriendlyPiece(int row, int col, Piece[][] board) {
        return board[row][col] != null && color.name().equals(board[row][col].getColor().name());
    }


    public abstract String getUniCode();


    public boolean isWhite() {
        return getColor().equals(Color.WHITE);
    }


    public boolean hasMoved() {
        return hasMoved;
    }
}