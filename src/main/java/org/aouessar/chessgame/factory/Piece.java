package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.ChessGame;
import org.aouessar.chessgame.Color;

@Getter
@Setter
public abstract class Piece {

    private final char name;

    private final Color color; // Indicates whether the piece is white or black

    private int row;

    private int col;

    private final ImageView icon;


    public Piece(char name, Color color, int row, int col, Image icon) {
        this.name = name;
        this.color = color;
        this.row = row;
        this.col = col;
        this.icon = new ImageView(icon);
    }

    public void addPieceToGrid(GridPane grid) {
        icon.setFitWidth(ChessGame.TILE_SIZE * 0.98);
        icon.setFitHeight(ChessGame.TILE_SIZE * 0.98);
        icon.setMouseTransparent(true);
        grid.add(icon, col, row);
    }


    // Abstract method to check if a move is valid
    public abstract boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board);


    // Check if the destination is occupied by a friendly piece
    public boolean isFriendlyPiece(int row, int col, Piece[][] board) {
        return board[row][col] != null && color.name().equals(board[row][col].getColor().name());
    }


    public boolean isWhite() {
        return getColor().equals(Color.WHITE);
    }
}