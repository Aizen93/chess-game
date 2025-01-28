package org.aouessar.chessgame.utils;

import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.Board;
import org.aouessar.chessgame.ChessGame;
import org.aouessar.chessgame.GameState;
import org.aouessar.chessgame.domain.Color;
import org.aouessar.chessgame.piece.Piece;
import org.aouessar.chessgame.piece.factory.*;

@Getter
@Setter
public class FENParser {

    private int halfMoveClock; // No captures or pawn moves

    private int fullMoveNumber;

    private String castling;

    private String enPassant;



    public FENParser() {
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
        this.castling = "KQkq";
        this.enPassant = "";
    }



    public Piece[][] initializePieces(String fen, GameState gameState, Board board) {
        Piece[][] boardArray = new Piece[board.getGameState().getHEIGHT()][board.getGameState().getWIDTH()];

        String[] parts = fen.split(" ");

        if (parts.length < 1) {
            ChessGame.handleMessage("Invalid FEN string: " + fen);
            return null;
        }

        String boardPart = parts[0];
        String[] rows = boardPart.split("/");

        if (rows.length != 8) {
            ChessGame.handleMessage("FEN board description must have 8 rows: " + boardPart);
            return null;
        }

        for (int row = 0; row < gameState.getWIDTH(); row++) {
            int col = 0;
            for (char ch : rows[row].toCharArray()) {
                if (Character.isDigit(ch)) {
                    // Empty squares, e.g., '3' means 3 empty squares
                    int emptySquares = Character.getNumericValue(ch);
                    col += emptySquares;
                } else if ("prnbqkPRNBQK".indexOf(ch) != -1) {
                    // Piece characters
                    Piece piece = fenCharToPiece(ch, row, col, board);
                    board.getUi().addPieceToGrid(piece);
                    boardArray[row][col] = piece;
                    col++;
                } else {
                    ChessGame.handleMessage("Invalid FEN character: " + ch);
                    return null;
                }
            }
            if (col != 8) {
                ChessGame.handleMessage("Row does not have exactly 8 columns: " + rows[row]);
                return null;
            }
        }

        // Update the player's turn from the FEN string
        String turnPart = parts[1];
        gameState.setWhiteTurn("w".equals(turnPart));

        return boardArray;
    }



    private Piece fenCharToPiece(char ch, int row, int col, Board board){
        return switch (ch) {
            case 'p' -> new Pawn('p', Color.BLACK, row, col, board.getPieceImages().get('p'));
            case 'P' -> new Pawn('P', Color.WHITE, row, col, board.getPieceImages().get('P'));

            case 'r' -> new Rook('r', Color.BLACK, row, col, board.getPieceImages().get('r'));
            case 'R' -> new Rook('R', Color.WHITE, row, col, board.getPieceImages().get('R'));

            case 'n' -> new Knight('n', Color.BLACK, row, col, board.getPieceImages().get('n'));
            case 'N' -> new Knight('N', Color.WHITE, row, col, board.getPieceImages().get('N'));

            case 'b' -> new Bishop('b', Color.BLACK, row, col, board.getPieceImages().get('b'));
            case 'B' -> new Bishop('B', Color.WHITE, row, col, board.getPieceImages().get('B'));

            case 'q' -> new Queen('q', Color.BLACK, row, col, board.getPieceImages().get('q'));
            case 'Q' -> new Queen('Q', Color.WHITE, row, col, board.getPieceImages().get('Q'));

            case 'k' -> board.setBlackKing(new King('k', Color.BLACK, row, col, board.getPieceImages().get('k')));
            case 'K' -> board.setWhiteKing(new King('K', Color.WHITE, row, col, board.getPieceImages().get('K')));

            default -> null;
        };
    }



    public String saveToFENString(boolean isWhiteTurn, Piece[][] board) {
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



    public void updateCastlingRights(Piece selectedPiece) {
        //TODO not complete yet, some cases still not handled, ex : if rook moves
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


    public void incrementHalfMoveClock() {
        this.halfMoveClock++;
    }



    public void incrementFullMoveNumber() {
        this.fullMoveNumber++;
    }


    public void resetFen(){
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
        this.castling = "KQkq";
        this.enPassant = "";
    }
}
