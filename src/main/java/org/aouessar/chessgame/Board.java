package org.aouessar.chessgame;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.factory.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class Board {

    private final int TILE_SIZE;

    private final int WIDTH; // Chessboard width

    private final int HEIGHT; // Chessboard height

    private Piece[][] board; // Board array

    private GridPane grid;

    private boolean isGameOver = false;

    private final Map<String, Image> pieceImages = new HashMap<>(); // Map for piece images

    private Piece selectedPiece = null;

    private Rectangle highlightedTile = null;

    private boolean isWhiteTurn = true;

    private Piece whiteKing;

    private Piece blackKing;



    public Board(int tileSize, int width, int height, GridPane gridPane) {
        this.TILE_SIZE = tileSize;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.board = new Piece[HEIGHT][WIDTH];
        this.grid = gridPane;
        loadPieceImages();
    }



    public void renderBoard() {
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

        rect.setOnMouseClicked(e -> handleTileClick(row, col, rect));

        return rect;
    }



    private void handleTileClick(int row, int col, Rectangle rect) {
        if (isGameOver) {
            return;
        }

        if (selectedPiece == null && board[row][col] != null) {
            if (board[row][col].isWhite() != isWhiteTurn) {
                ChessGame.handleMessage("It's " + (isWhiteTurn ? "White" : "Black") + "'s turn.");
                return;
            }

            selectedPiece = board[row][col];
            highlightTile(rect);
            ChessGame.handleMessage("Piece selected at (" + row + ", " + col + "): " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
        }
        else if (selectedPiece != null) {
            if (board[row][col] != null && board[row][col].isFriendlyPiece(selectedPiece.getRow(), selectedPiece.getCol(), board)) {
                selectedPiece = board[row][col];
                highlightTile(rect);
                ChessGame.handleMessage("Piece selection changed at (" + row + ", " + col + "): " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
            }
            else {
                move(row, col);
                org.aouessar.chessgame.Color player = !isWhiteTurn ? org.aouessar.chessgame.Color.WHITE : org.aouessar.chessgame.Color.BLACK;
                if(Rules.isCheckmate(isWhiteTurn, HEIGHT, WIDTH, board)){
                    ChessGame.handleMessage(player + " Wins");
                    isGameOver = true;
                    showCheckmatePopup();
                }
            }
        }
    }



    public void initializePieces(String fen, GridPane grid) {
        String[] parts = fen.split(" ");

        if (parts.length < 1) {
            ChessGame.handleMessage("Invalid FEN string: " + fen);
        }

        String boardPart = parts[0];
        String[] rows = boardPart.split("/");

        if (rows.length != 8) {
            ChessGame.handleMessage("FEN board description must have 8 rows: " + boardPart);
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
                    Piece piece = fenCharToPiece(ch, row, col);
                    piece.addPieceToGrid(grid);
                    board[row][col] = piece;
                    col++;
                } else {
                    ChessGame.handleMessage("Invalid FEN character: " + ch);
                }
            }
            if (col != 8) {
                ChessGame.handleMessage("Row does not have exactly 8 columns: " + rows[row]);
            }
        }

    }



    private void move(int endRow, int endCol) {
        if(!selectedPiece.isValidMove(selectedPiece.getRow(), selectedPiece.getCol(), endRow, endCol, board)){
            ChessGame.handleMessage("Invalid move for " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
            return;
        }
        if(board[endRow][endCol] != null) {
            grid.getChildren().remove(board[endRow][endCol].getIcon());
        }

        board[endRow][endCol] = selectedPiece;
        board[selectedPiece.getRow()][selectedPiece.getCol()] = null;

        grid.getChildren().remove(selectedPiece.getIcon());
        selectedPiece.setCol(endCol);
        selectedPiece.setRow(endRow);
        selectedPiece.addPieceToGrid(grid);

        selectedPiece = null;

        isWhiteTurn = !isWhiteTurn;

        resetHighlight();
        //gridToConsole();
    }






    private void highlightTile(Rectangle rect) {
        resetHighlight();

        rect.setFill(Color.ORANGE);
        highlightedTile = rect;
    }



    private void resetHighlight() {
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



    public void showCheckmatePopup() {
        String winner = isWhiteTurn ? "Black" : "White";  // Winner is the opposite of the current turn

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Checkmate!");
        alert.setContentText(winner + " wins!");

        // Create a "Restart Game" button
        ButtonType restartButton = new ButtonType("Restart Game");
        alert.getButtonTypes().setAll(restartButton, ButtonType.CLOSE);

        alert.showAndWait().ifPresent(response -> {
            if (response == restartButton) {
                restartGame();
            }
        });
    }



    public void restartGame() {
        isWhiteTurn = true;
        isGameOver = false;
        this.board = new Piece[HEIGHT][WIDTH];
        renderBoard();
        initializePieces("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", grid);
        ChessGame.handleMessage("-------------------------------------");
        ChessGame.handleMessage("Game RESTARTED");
    }



    private Piece fenCharToPiece(char ch, int row, int col){
        return switch (ch) {
            case 'p' -> new Pawn('p', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("p"));
            case 'P' -> new Pawn('P', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("P"));

            case 'r' -> new Rook('r', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("r"));
            case 'R' -> new Rook('R', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("R"));

            case 'n' -> new Knight('n', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("n"));
            case 'N' -> new Knight('N', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("N"));

            case 'b' -> new Bishop('b', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("b"));
            case 'B' -> new Bishop('B', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("B"));

            case 'q' -> new Queen('q', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("q"));
            case 'Q' -> new Queen('Q', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("Q"));

            case 'k' -> blackKing = new King('k', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get("k"));
            case 'K' -> whiteKing = new King('K', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get("K"));

            default -> null;
        };
    }



    /**
     * Load piece images into the pieceImages map.
     */
    private void loadPieceImages() {
        try {
            pieceImages.put("P", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_pawn.png"))));
            pieceImages.put("p", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_pawn.png"))));
            pieceImages.put("R", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_rook.png"))));
            pieceImages.put("r", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_rook.png"))));
            pieceImages.put("N", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_knight.png"))));
            pieceImages.put("n", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_knight.png"))));
            pieceImages.put("B", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_bishop.png"))));
            pieceImages.put("b", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_bishop.png"))));
            pieceImages.put("Q", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_queen.png"))));
            pieceImages.put("q", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_queen.png"))));
            pieceImages.put("K", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_king.png"))));
            pieceImages.put("k", new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_king.png"))));
        } catch (Exception e) {
            System.err.println("Error loading piece images: " + e.getMessage());
        }
    }



    private void gridToConsole() {
        System.out.println("_______________________________");
        for (int row = 0; row < HEIGHT; row++) {
            System.out.print("| ");
            for (int col = 0; col < WIDTH; col++) {
                if(board[row][col] != null) System.out.print(board[row][col].getName() + " | ");
                else System.out.print("  | ");
            }
            System.out.println();
            System.out.println("_________________________________");
        }
        System.out.println();
        System.out.println("****************************************");
        System.out.println();
    }
}
