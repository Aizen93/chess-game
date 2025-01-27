package org.aouessar.chessgame;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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

    private final Map<Character, Image> pieceImages = new HashMap<>(); // Map for piece images

    private Piece selectedPiece = null;

    private Rectangle highlightedTile = null;

    private boolean isWhiteTurn = true;

    private Piece whiteKing;

    private Piece blackKing;

    private int halfMoveClock = 0; // No pawn moves or captures

    private int fullMoveNumber = 1;

    private String castling = "KQkq";

    private String enPassant = "";



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
        if (isGameOver) return;

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
            }
        }
    }



    public void move(int endRow, int endCol) {
        if (selectedPiece == null) return;

        if (selectedPiece.isWhite() != isWhiteTurn) {
            ChessGame.handleMessage("It's " + (isWhiteTurn ? "White" : "Black") + "'s turn.");
            return;
        }

        if(!selectedPiece.isValidMove(selectedPiece.getRow(), selectedPiece.getCol(), endRow, endCol, board)){
            ChessGame.handleMessage("Invalid move for " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
            return;
        }

        // Check if the move prevents the king from being in check
        if (!Rules.doesMovePreventCheck(selectedPiece, endRow, endCol, HEIGHT, WIDTH, board)) {
            ChessGame.flashTile(getKingsTurn(), grid);
            ChessGame.handleMessage("Move not allowed: it leaves the king in check.");
            return;
        }


        if(board[endRow][endCol] != null) {
            halfMoveClock = 0; //we captured a piece we reset the clock
            grid.getChildren().remove(board[endRow][endCol].getIcon());
        }

        board[endRow][endCol] = selectedPiece;
        board[selectedPiece.getRow()][selectedPiece.getCol()] = null;

        grid.getChildren().remove(selectedPiece.getIcon());
        if(selectedPiece instanceof King) {
            if (Math.abs(endCol - selectedPiece.getCol()) == 2) {
                ((King)selectedPiece).performCastlingMove(endRow, endCol, board, grid);
                updateCastlingRights();
            }
            ((King) selectedPiece).setHasMoved(true);
            selectedPiece.setCol(endCol);
            selectedPiece.setRow(endRow);
            selectedPiece.addPieceToGrid(grid);

        } else {
            selectedPiece.setCol(endCol);
            selectedPiece.setRow(endRow);
            selectedPiece.addPieceToGrid(grid);
        }


        if(selectedPiece instanceof Pawn) {
            // Check for promotion
            if (((Pawn) selectedPiece).isPromotionRow()) {
                ((Pawn) selectedPiece).promotePawn(this);
            }
            halfMoveClock = 0; //pawn moves so we reset the clock
        } else {
            halfMoveClock++;
        }

        if(selectedPiece instanceof Rook) {
            updateCastlingRights();
        }

        selectedPiece = null;

        isWhiteTurn = !isWhiteTurn;

        if(isWhiteTurn) fullMoveNumber++;

        resetHighlight();
        gridToConsole();

        if(Rules.isCheckmate(isWhiteTurn, HEIGHT, WIDTH, board)){
            ChessGame.handleMessage((!isWhiteTurn ? "White" : "Black") + " Wins");
            isGameOver = true;
            ChessGame.showCheckmatePopup(this, "Game Over", "Checkmate !", currentPlayerColor() + " Wins !", Alert.AlertType.CONFIRMATION);

        } else if(Rules.isStalemate(isWhiteTurn, HEIGHT, WIDTH, board)){
            ChessGame.handleMessage("Stalemate detected, game is a Draw");
            isGameOver = true;
            ChessGame.showCheckmatePopup(this, "Game Over", "Stalemate !", "This is a DRAW !", Alert.AlertType.INFORMATION);
        }

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



    public void restartGame(String fen) {
        isWhiteTurn = true;
        isGameOver = false;
        selectedPiece = null;
        highlightedTile = null;
        this.board = new Piece[HEIGHT][WIDTH];
        ChessGame.handleMessage("-------------------------------------");
        ChessGame.handleMessage("Game RESTARTED");
        renderBoard();
        initializePieces(fen, grid);
        addAnnotations();
    }



    public void initializePieces(String fen, GridPane grid) {
        String[] parts = fen.split(" ");

        if (parts.length < 1) {
            ChessGame.handleMessage("Invalid FEN string: " + fen);
            return;
        }

        String boardPart = parts[0];
        String[] rows = boardPart.split("/");

        if (rows.length != 8) {
            ChessGame.handleMessage("FEN board description must have 8 rows: " + boardPart);
            return;
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
                    return;
                }
            }
            if (col != 8) {
                ChessGame.handleMessage("Row does not have exactly 8 columns: " + rows[row]);
                return;
            }
        }

        // Update the player's turn from the FEN string
        //System.out.println(parts[1]);
        String turnPart = parts[1];
        isWhiteTurn = "w".equals(turnPart);

    }



    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }



    private Piece fenCharToPiece(char ch, int row, int col){
        return switch (ch) {
            case 'p' -> new Pawn('p', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get('p'));
            case 'P' -> new Pawn('P', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get('P'));

            case 'r' -> new Rook('r', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get('r'));
            case 'R' -> new Rook('R', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get('R'));

            case 'n' -> new Knight('n', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get('n'));
            case 'N' -> new Knight('N', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get('N'));

            case 'b' -> new Bishop('b', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get('b'));
            case 'B' -> new Bishop('B', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get('B'));

            case 'q' -> new Queen('q', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get('q'));
            case 'Q' -> new Queen('Q', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get('Q'));

            case 'k' -> blackKing = new King('k', org.aouessar.chessgame.Color.BLACK, row, col, pieceImages.get('k'));
            case 'K' -> whiteKing = new King('K', org.aouessar.chessgame.Color.WHITE, row, col, pieceImages.get('K'));

            default -> null;
        };
    }



    /**
     * Load piece images into the pieceImages map.
     */
    private void loadPieceImages() {
        try {
            pieceImages.put('P', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_pawn.png"))));
            pieceImages.put('p', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_pawn.png"))));

            pieceImages.put('R', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_rook.png"))));
            pieceImages.put('r', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_rook.png"))));

            pieceImages.put('N', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_knight.png"))));
            pieceImages.put('n', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_knight.png"))));

            pieceImages.put('B', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_bishop.png"))));
            pieceImages.put('b', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_bishop.png"))));

            pieceImages.put('Q', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_queen.png"))));
            pieceImages.put('q', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_queen.png"))));

            pieceImages.put('K', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/white_king.png"))));
            pieceImages.put('k', new Image(Objects.requireNonNull(Board.class.getResourceAsStream("/public/black_king.png"))));
        } catch (Exception e) {
            System.err.println("Error loading piece images: " + e.getMessage());
        }
    }



    /**
     * Adds chessboard annotations (like "a1", "h8") to the grid.
     */
    public void addAnnotations() {
        // Add column labels (a-h) at the bottom
        for (int col = 1; col <= WIDTH; col++) {
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
        for(int row = 1; row <= HEIGHT; row++){
            Text label = new Text(String.valueOf(HEIGHT + 1 - row));
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


    public String saveToFEN() {
        StringBuilder fen = new StringBuilder();

        // Construct the board part
        for (int row = 0; row < 8; row++) {
            int emptyCount = 0; // Tracks consecutive empty squares
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece == null) {
                    // Count empty squares
                    emptyCount++;
                } else {
                    // Append empty squares count (if any)
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    // Append piece FEN character
                    fen.append(piece.getName());
                }
            }
            // Append remaining empty squares in the row
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            // Add '/' for all but the last row
            if (row < 7) {
                fen.append('/');
            }
        }

        // Add the additional FEN parts
        fen.append(" ");
        fen.append(isWhiteTurn ? "w" : "b"); // Whose turn it is ('w' or 'b')
        fen.append(" ");
        fen.append(castling.isEmpty() ? "-" : castling); // Castling availability
        fen.append(" ");
        fen.append(enPassant.isEmpty() ? "-" : enPassant); // En passant target
        fen.append(" ");
        fen.append(halfMoveClock); // Half-move clock
        fen.append(" ");
        fen.append(fullMoveNumber); // Full move number

        return fen.toString();
    }



    public void updateCastlingRights() {
        if (selectedPiece instanceof King) {
            if (selectedPiece.isWhite()) {
                castling = castling.replace("K", "").replace("Q", "");
            } else {
                castling = castling.replace("k", "").replace("q", "");
            }
        }

        if (selectedPiece instanceof Rook) {
            if (selectedPiece.isWhite()) {
                if (selectedPiece.getCol() == 0) {
                    castling = castling.replace("Q", "");
                } else if (selectedPiece.getCol() == 7) {
                    castling = castling.replace("K", "");
                }
            } else {
                if (selectedPiece.getCol() == 0) {
                    castling = castling.replace("q", "");
                } else if (selectedPiece.getCol() == 7) {
                    castling = castling.replace("k", "");
                }
            }
        }
    }



    private Piece getKingsTurn() {
        return isWhiteTurn ? whiteKing : blackKing;
    }



    private String currentPlayerColor() {
        return isWhiteTurn ? "Black" : "White";
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
