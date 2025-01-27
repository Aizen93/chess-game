package org.aouessar.chessgame;

import org.aouessar.chessgame.factory.King;
import org.aouessar.chessgame.factory.Piece;

public class Rules {

    public static boolean isInCheck(boolean isWhite, int HEIGHT, int WIDTH, Piece[][] board) {
        int[] kingsPos = findKingsPosition(isWhite, HEIGHT, WIDTH, board);

        // Check if any opponent piece can attack the king
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != null && board[row][col].isWhite() != isWhite) {
                    if (board[row][col].isValidMove(row, col, kingsPos[0], kingsPos[1], board)) {
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


    public static boolean doesMovePreventCheck(Piece piece, int targetRow, int targetCol, int HEIGHT, int WIDTH, Piece[][] board) {
        // Store the original state
        int originalRow = piece.getRow();
        int originalCol = piece.getCol();
        Piece capturedPiece = board[targetRow][targetCol];

        // Simulate the move
        board[targetRow][targetCol] = piece;
        board[originalRow][originalCol] = null;
        piece.setRow(targetRow);
        piece.setCol(targetCol);

        // Check if the king is still in check
        boolean kingStillInCheck = isInCheck(piece.isWhite(), HEIGHT, WIDTH, board);

        // Undo the move
        piece.setRow(originalRow);
        piece.setCol(originalCol);
        board[originalRow][originalCol] = piece;
        board[targetRow][targetCol] = capturedPiece;

        return !kingStillInCheck;
    }



    public static boolean isStalemate(boolean isWhite, int HEIGHT, int WIDTH, Piece[][] board) {
        // Step 1: Check if the king is not in check
        if (isInCheck(isWhite, HEIGHT, WIDTH, board)) {
            return false; // Not stalemate if the king is in check
        }

        // Step 2: Look for any legal moves
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                Piece piece = board[row][col];

                // Continue only if it's the current player's piece
                if (piece != null && piece.isWhite() == isWhite) {
                    // Check all possible target positions
                    for (int targetRow = 0; targetRow < HEIGHT; targetRow++) {
                        for (int targetCol = 0; targetCol < WIDTH; targetCol++) {
                            // Validate the move
                            if (piece.isValidMove(row, col, targetRow, targetCol, board)) {
                                // Simulate the move to see if it keeps the king safe
                                if (doesMovePreventCheck(piece, targetRow, targetCol, HEIGHT, WIDTH, board)) {
                                    return false; // Found a legal move, so not stalemate
                                }
                            }
                        }
                    }
                }
            }
        }

        // No legal moves found, it's stalemate
        return true;
    }



    /**
     * In case we need to find the current turn's king dynamically
     * @param isWhite turn
     * @param HEIGHT board bounds
     * @param WIDTH board bounds
     * @param board the board object
     * @return a pair of (row, col) as array
     */
    public static int[] findKingsPosition(boolean isWhite, int HEIGHT, int WIDTH, Piece[][] board) {
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
        return new int[]{kingRow, kingCol};
    }

}
