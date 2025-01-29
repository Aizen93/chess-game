package org.aouessar.chessgame;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.ai.Stockfish;
import org.aouessar.chessgame.piece.factory.King;
import org.aouessar.chessgame.piece.factory.Pawn;
import org.aouessar.chessgame.piece.Piece;
import org.aouessar.chessgame.piece.factory.Rook;
import org.aouessar.chessgame.ui.GameUI;
import org.aouessar.chessgame.utils.FENParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class Board {

    private GridPane grid;

    private Piece[][] board;

    private Piece whiteKing;

    private Piece blackKing;

    private Piece selectedPiece;

    private final GameState gameState;

    private final FENParser parser;

    private final GameUI ui;

    private final Map<Character, Image> pieceImages;

    private final Stockfish stockfish;

    private int skillLevel;

    private int computationDepth;



    public Board(int tileSize, int width, int height, GridPane gridPane) {
        this.selectedPiece = null;
        this.grid = gridPane;

        this.pieceImages = new HashMap<>();
        loadPieceImages();

        this.ui = new GameUI(gridPane, this);

        this.gameState = new GameState(tileSize, width, height);
        this.parser = new FENParser();

        this.ui.renderBoard();
        this.board = this.parser.initializePieces("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", gameState, this);
        this.ui.addAnnotations();

        skillLevel = 12;
        computationDepth = 15;

        this.stockfish = new Stockfish("C:\\Users\\u200159\\Desktop\\Workspace\\personnal\\chess-game\\ChessGame\\src\\main\\resources\\public\\stockfish\\stockfish-windows-x86-64-avx2.exe");
        makeAIMove();
    }



    public void handleTileClick(int row, int col, Rectangle rect) {
        //TODO relocate this method to GameUI
        if (gameState.isGameOver()){
            return;
        }

        if (selectedPiece == null && board[row][col] != null) {
            if (board[row][col].isWhite() != gameState.isWhiteTurn()) {
                ChessGame.handleMessage("It's " + (gameState.isWhiteTurn() ? "White" : "Black") + "'s turn.");
                return;
            }

            selectedPiece = board[row][col];
            this.ui.highlightTile(rect);
            ChessGame.handleMessage("Piece selected at (" + row + ", " + col + "): " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
        }
        else if (selectedPiece != null) {
            if (board[row][col] != null && board[row][col].isFriendlyPiece(selectedPiece.getRow(), selectedPiece.getCol(), board)) {
                selectedPiece = board[row][col];
                this.ui.highlightTile(rect);
                ChessGame.handleMessage("Piece selection changed at (" + row + ", " + col + "): " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
            }
            else {
                //humain joue blanc
                boolean didMove = move(row, col);
                if (didMove && !gameState.isGameOver()) {
                    makeAIMove(); // Call Stockfish to make a move
                }
            }
        }
    }



    public boolean move(int endRow, int endCol) {
        if (selectedPiece == null) return false;

        if (selectedPiece.isWhite() != gameState.isWhiteTurn()) {
            ChessGame.handleMessage("It's " + (gameState.isWhiteTurn() ? "White" : "Black") + "'s turn.");
            return false;
        }

        if(!selectedPiece.isValidMove(selectedPiece.getRow(), selectedPiece.getCol(), endRow, endCol, board)){
            ChessGame.handleMessage("Invalid move for " + (selectedPiece.getColor().name() + " " + selectedPiece.getClass().getSimpleName()));
            return false;
        }

        // Check if the move prevents the king from being in check
        if (!Rules.doesMovePreventCheck(selectedPiece, endRow, endCol, gameState.getHEIGHT(), gameState.getWIDTH(), board)) {
            ChessGame.flashTile(getKingsTurn(), grid);
            ChessGame.handleMessage("Move not allowed: it leaves the king in check.");
            return false;
        }


        if(board[endRow][endCol] != null) {
            parser.setHalfMoveClock(0); //we captured a piece we reset the clock
            this.ui.removePieceFromGrid(board[endRow][endCol]);
        }

        board[endRow][endCol] = selectedPiece;
        board[selectedPiece.getRow()][selectedPiece.getCol()] = null;

        this.ui.removePieceFromGrid(selectedPiece);

        if(selectedPiece instanceof King) {
            if (Math.abs(endCol - selectedPiece.getCol()) == 2) {
                ((King)selectedPiece).performCastlingMove(endRow, endCol, this);
                parser.updateCastlingRights(selectedPiece);
            }
            selectedPiece.setHasMoved(true);

        }

        selectedPiece.setCol(endCol);
        selectedPiece.setRow(endRow);
        this.ui.addPieceToGrid(selectedPiece);


        if(selectedPiece instanceof Pawn) {
            // Check for promotion
            if (((Pawn) selectedPiece).isPromotionRow()) {
                ((Pawn) selectedPiece).promotePawn(this);
            }
            parser.setHalfMoveClock(0); //pawn moves so we reset the clock
        } else {
            parser.incrementHalfMoveClock();
        }

        if(selectedPiece instanceof Rook) {
            selectedPiece.setHasMoved(true);
            parser.updateCastlingRights(selectedPiece);
        }

        selectedPiece = null;

        gameState.switchTurn();

        if(gameState.isWhiteTurn()) parser.incrementFullMoveNumber();

        this.ui.resetHighlight();
        gridToConsole();

        if(Rules.isCheckmate(gameState.isWhiteTurn(), gameState.getHEIGHT(), gameState.getWIDTH(), board)){
            ChessGame.handleMessage((!gameState.isWhiteTurn() ? "White" : "Black") + " Wins");
            gameState.setGameOver(true);
            ChessGame.showCheckmatePopup(this, "Game Over", "Checkmate !", currentPlayerColor() + " Wins !", Alert.AlertType.CONFIRMATION);

        } else if(Rules.isStalemate(gameState.isWhiteTurn(), gameState.getHEIGHT(), gameState.getWIDTH(), board)){
            ChessGame.handleMessage("Stalemate detected, game is a Draw");
            gameState.setGameOver(true);
            ChessGame.showCheckmatePopup(this, "Game Over", "Stalemate !", "This is a DRAW !", Alert.AlertType.INFORMATION);
        }

        return true;
    }



    public void restartGame(String fen) {
        gameState.setWhiteTurn(true);
        gameState.setGameOver(false);
        selectedPiece = null;
        this.ui.setHighlightedTile(null);
        parser.resetFen();
        gridToConsole();
        this.ui.renderBoard();
        board = parser.initializePieces(fen, gameState, this);
        this.ui.addAnnotations();
        makeAIMove();
        ChessGame.handleMessage("-------------- Game RESTARTED --------------");
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



    private Piece getKingsTurn() {
        return gameState.isWhiteTurn() ? whiteKing : blackKing;
    }



    private String currentPlayerColor() {
        return gameState.isWhiteTurn() ? "Black" : "White";
    }



    public Piece setWhiteKing(Piece whiteKing) {
        this.whiteKing = whiteKing;
        return whiteKing;
    }



    public Piece setBlackKing(Piece blackKing) {
        this.blackKing = blackKing;
        return blackKing;
    }



    private void gridToConsole() {
        System.out.println("_______________________________");
        for (int row = 0; row < gameState.getHEIGHT(); row++) {
            System.out.print("| ");
            for (int col = 0; col < gameState.getWIDTH(); col++) {
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


    public boolean makeAIMove() {
        if (stockfish != null) {
            // Convert the current board state to FEN
            String fen = parser.saveToFENString(gameState.isWhiteTurn(), board);

            // Get the best move from Stockfish
            String bestMove = stockfish.getBestMove(fen, computationDepth, skillLevel);

            if (bestMove != null) {
                // Parse the move and update the board
                int startRow = 8 - Character.getNumericValue(bestMove.charAt(1));
                int startCol = bestMove.charAt(0) - 'a';
                int endRow = 8 - Character.getNumericValue(bestMove.charAt(3));
                int endCol = bestMove.charAt(2) - 'a';

                selectedPiece = board[startRow][startCol];
                return move(endRow, endCol);
            }
        }
        return false;
    }
}
