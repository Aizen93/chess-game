package org.aouessar.chessgame.ui;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.Board;
import org.aouessar.chessgame.ChessGame;
import org.aouessar.chessgame.piece.Piece;

@Getter
@Setter
public class GameUI {

    private final GridPane grid;

    private final Board board;

    private Rectangle highlightedTile;



    public GameUI(GridPane grid, Board board) {
        this.grid = grid;
        this.board = board;
        this.highlightedTile = null;
    }



    public void renderBoard() {
        grid.getChildren().clear(); // Clear previous board before rendering

        for (int row = 0; row < board.getGameState().getHEIGHT(); row++) {
            for (int col = 0; col < board.getGameState().getWIDTH(); col++) {
                Rectangle tile = createTile(row, col);
                grid.add(tile, col, row);
            }
        }
    }



    /**
     * Create a single tile for the chessboard.
     */
    private Rectangle createTile(int row, int col) {
        // Create the background rectangle
        Rectangle rect = new Rectangle(board.getGameState().getTILE_SIZE(), board.getGameState().getTILE_SIZE());
        if ((row + col) % 2 == 0) {
            rect.setFill(Color.valueOf("#ebecd0")); // Light tiles
        } else {
            rect.setFill(Color.valueOf("#739552")); // Dark tiles
        }

        rect.setOnMouseClicked(e -> board.handleTileClick(row, col, rect));

        return rect;
    }



    public void addPieceToGrid(Piece piece) {
        piece.getIcon().setFitWidth(ChessGame.TILE_SIZE * 0.98);
        piece.getIcon().setFitHeight(ChessGame.TILE_SIZE * 0.98);
        piece.getIcon().setMouseTransparent(true);
        grid.add(piece.getIcon(), piece.getCol(), piece.getRow());
    }



    public void removePieceFromGrid(Piece piece) {
        grid.getChildren().remove(piece.getIcon());
    }



    public void highlightTile(Rectangle rect) {
        resetHighlight();

        rect.setFill(Color.ORANGE);
        highlightedTile = rect;
    }



    public void resetHighlight() {
        if (highlightedTile != null) {
            int previousRow = GridPane.getRowIndex(highlightedTile);
            int previousCol = GridPane.getColumnIndex(highlightedTile);

            if ((previousRow + previousCol) % 2 == 0) {
                highlightedTile.setFill(Color.valueOf("#ebecd0"));
            } else {
                highlightedTile.setFill(Color.valueOf("#739552"));
            }

            highlightedTile = null;
        }
    }



    /**
     * Adds chessboard annotations (like "a1", "h8") to the grid.
     */
    public void addAnnotations() {
        // Add column labels (a-h) at the bottom
        for (int col = 1; col <= board.getGameState().getWIDTH(); col++) {
            Text label = new Text(String.valueOf((char) ('a' + col - 1)));
            label.setTranslateY(40);
            label.setTranslateX(85);

            if ((col - 1) % 2 == 1) {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #739552; -fx-font-size: 16px;");
            } else {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #ebecd0; -fx-font-size: 16px;");
            }

            grid.add(label, col - 1, 7);
        }

        // Add row labels (1-8) at the left
        for(int row = 1; row <= board.getGameState().getHEIGHT(); row++){
            Text label = new Text(String.valueOf(board.getGameState().getHEIGHT() + 1 - row));
            if ((row - 1) % 2 == 0) {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #739552; -fx-font-size: 16px;");
                label.setTranslateY(-40);
            } else {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #ebecd0; -fx-font-size: 16px;");
                label.setTranslateY(-40);
            }

            grid.add(label, 0, row - 1);
        }

        // Shift chessboard tiles to align with annotations
        grid.setHgap(1);
        grid.setVgap(1);
    }



    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

}
