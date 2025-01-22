package org.aouessar.chessgame;

import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.factory.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Board {

    private final int TILE_SIZE;

    private final int WIDTH; // Chessboard width

    private final int HEIGHT; // Chessboard height

    private Piece[][] board; // Board array

    private final Map<String, Image> pieceImages = new HashMap<>(); // Map for piece images


    public Board(int tileSize, int width, int height) {
        this.TILE_SIZE = tileSize;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.board = new Piece[HEIGHT][WIDTH];
        loadPieceImages();
    }


    public void renderBoard(GridPane grid) {
        grid.getChildren().clear(); // Clear previous board before rendering

        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
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
        Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
        if ((row + col) % 2 == 0) {
            rect.setFill(Color.valueOf("#ebecd0")); // Light tiles
        } else {
            rect.setFill(Color.valueOf("#739552")); // Dark tiles
        }

        return rect;
    }



    public void initializePieces(String fen) {
        String[] parts = fen.split(" ");

        if (parts.length < 1) {
            System.out.println("Invalid FEN string: " + fen);
        }

        String boardPart = parts[0];
        String[] rows = boardPart.split("/");

        if (rows.length != 8) {
            System.out.println("FEN board description must have 8 rows: " + boardPart);
        }

        for (int row = 0; row < WIDTH; row++) {
            int col = 0;
            for (char ch : rows[row].toCharArray()) {
                if (Character.isDigit(ch)) {
                    // Empty squares, e.g., '3' means 3 empty squares
                    int emptySquares = Character.getNumericValue(ch);
                    col += emptySquares;
                } else if ("prnbqkPRNBQK".indexOf(ch) != -1) {
                    // Piece characters
                    board[row][col] = fenCharToPiece(ch, row, col);
                    col++;
                } else {
                    System.out.println("Invalid FEN character: " + ch);
                }
            }
            if (col != 8) {
                System.out.println("Row does not have exactly 8 columns: " + rows[row]);
            }
        }

    }


    private Piece fenCharToPiece(char ch, int row, int col){
        return switch (ch) {
            case 'p' -> new Pawn(org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("p"));
            case 'r' -> new Rook(org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("r"));
            case 'n' -> new Knight(org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("n"));
            case 'b' -> new Bishop(org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("b"));
            case 'q' -> new Queen(org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("q"));
            case 'k' -> new King(org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("k"));
            case 'P' -> new Pawn(org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("P"));
            case 'R' -> new Rook(org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("R"));
            case 'N' -> new Knight(org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("N"));
            case 'B' -> new Bishop(org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("B"));
            case 'Q' -> new Queen(org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("Q"));
            case 'K' -> new King(org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("K"));
            default -> null;
        };
    }



    /**
     * Load piece images into the pieceImages map.
     */
    private void loadPieceImages() {
        try {
            pieceImages.put("P", new Image(getClass().getResourceAsStream("white_pawn.png")));
            pieceImages.put("p", new Image(getClass().getResourceAsStream("black_pawn.png")));
            pieceImages.put("R", new Image(getClass().getResourceAsStream("white_rook.png")));
            pieceImages.put("r", new Image(getClass().getResourceAsStream("black_rook.png")));
            pieceImages.put("N", new Image(getClass().getResourceAsStream("white_knight.png")));
            pieceImages.put("n", new Image(getClass().getResourceAsStream("black_knight.png")));
            pieceImages.put("B", new Image(getClass().getResourceAsStream("white_bishop.png")));
            pieceImages.put("b", new Image(getClass().getResourceAsStream("black_bishop.png")));
            pieceImages.put("Q", new Image(getClass().getResourceAsStream("white_queen.png")));
            pieceImages.put("q", new Image(getClass().getResourceAsStream("black_queen.png")));
            pieceImages.put("K", new Image(getClass().getResourceAsStream("white_king.png")));
            pieceImages.put("k", new Image(getClass().getResourceAsStream("black_king.png")));
        } catch (Exception e) {
            System.err.println("Error loading piece images: " + e.getMessage());
        }
    }
}
