package org.aouessar.chessgame;

import org.aouessar.chessgame.factory.King;
import org.aouessar.chessgame.factory.Piece;

public class Rules {

    public static boolean isInCheck(boolean isWhite, int HEIGHT, int WIDTH, Piece[][] board) {
        int kingRow = -1;
        int kingCol = -1;

        // Find the king's position
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != null && board[row][col] instanceof King && board[row][col].isWhite() == isWhite) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        // Check if any opponent piece can attack the king
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != null && board[row][col].isWhite() != isWhite) {
                    if (board[row][col].isValidMove(row, col, kingRow, kingCol, board)) {
                        return true; // King is in check
                    }
                }
            }
        }

        return false; // King is not in check
    }


    public static boolean isCheckmate(boolean isWhite, int HEIGHT, int WIDTH, Piece[][] board) {
        if (!isInCheck(isWhite, HEIGHT, WIDTH, board)) {
            return false; // If the king isn't in check, it's not checkmate
        }

        ChessGame.handleMessage((isWhite ? "White" : "Black") + " King's in check");

        // Check if the player has any valid moves that can get them out of check
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != null && board[row][col].isWhite() == isWhite) {
                    // Try all possible moves for the piece
                    for (int targetRow = 0; targetRow < HEIGHT; targetRow++) {
                        for (int targetCol = 0; targetCol < WIDTH; targetCol++) {
                            if (board[row][col].isValidMove(row, col, targetRow, targetCol, board)) {
                                // Simulate the move and check if it results in the king being in check
                                Piece capturedPiece = board[targetRow][targetCol];
                                board[targetRow][targetCol] = board[row][col];
                                board[row][col] = null;

                                // Check if the king is still in check
                                if (!isInCheck(isWhite, HEIGHT, WIDTH, board)) {
                                    // Undo the move
                                    board[row][col] = board[targetRow][targetCol];
                                    board[targetRow][targetCol] = capturedPiece;
                                    return false; // The player has a valid move, so it's not checkmate
                                }

                                // Undo the move
                                board[row][col] = board[targetRow][targetCol];
                                board[targetRow][targetCol] = capturedPiece;
                            }
                        }
                    }
                }
            }
        }

        return true; // No valid moves to escape check, it's checkmate
    }

}
