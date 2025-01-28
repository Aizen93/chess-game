package org.aouessar.chessgame.piece.factory;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.Board;
import org.aouessar.chessgame.ChessGame;
import org.aouessar.chessgame.domain.Color;
import org.aouessar.chessgame.Rules;
import org.aouessar.chessgame.piece.Piece;

@Getter
@Setter
public class King extends Piece {


    public King(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }



    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // The King can move one square in any direction
        if (dx <= 1 && dy <= 1) {
            // Ensure the destination square is not occupied by a friendly piece
            return !isFriendlyPiece(endX, endY, board);
        }

        // Castling move validation
        if (!hasMoved && startX == endX && Math.abs(startY - endY) == 2) {
            return canCastle(startX, startY, endY, board);
        }

        return false;
    }



    private boolean canCastle(int startX, int startY, int endY, Piece[][] board) {
        // King side castling
        if (endY > startY) {
            // Check squares between king and rook
            for (int col = startY + 1; col < endY; col++) {
                if (board[startX][col] != null) return false;
            }

            // Check if the rook has moved
            Piece rook = board[startX][7];
            if (!(rook instanceof Rook) || rook.hasMoved()) return false;

            // Check if the king is in check or would move through check
            for (int col = startY; col <= endY; col++) {
                if (Rules.isInCheck(isWhite(), board.length, board[0].length, board)) {
                    return false;
                }
            }

        } else {
            // Queen side castling
            // Check squares between king and rook
            for (int col = startY - 1; col > endY; col--) {
                if (board[startX][col] != null) return false;
            }

            // Check if the rook has moved
            Piece rook = board[startX][0];
            if (!(rook instanceof Rook) || rook.hasMoved()) return false;

            // Check if the king is in check or would move through check
            for (int col = startY; col >= endY; col--) {
                if (Rules.isInCheck(isWhite(), board.length, board[0].length, board)) {
                    return false;
                }
            }

        }

        return true;
    }



    public void performCastlingMove(int targetRow, int targetCol, Board board) {
        // Kingside castling
        if (targetCol == 6) {
            // Move the king
            board.getBoard()[targetRow][this.getCol()] = null;
            this.setCol(6);
            board.getBoard()[targetRow][this.getCol()] = this;

            // Move the rook
            Piece rook = board.getBoard()[targetRow][7];
            board.getBoard()[targetRow][7] = null;
            rook.setCol(5);
            board.getBoard()[targetRow][5] = rook;

            board.getUi().removePieceFromGrid(rook);
            board.getUi().addPieceToGrid(rook);
            ChessGame.handleMessage(this.getColor() + " King castling performed King's side");

        } else if (targetCol == 2) {
            // Queenside castling
            // Move the king
            board.getBoard()[targetRow][this.getCol()] = null;
            this.setCol(2);
            board.getBoard()[targetRow][this.getCol()] = this;

            // Move the rook
            Piece rook = board.getBoard()[targetRow][0];
            board.getBoard()[targetRow][0] = null;
            rook.setCol(3);
            board.getBoard()[targetRow][3] = rook;

            board.getUi().removePieceFromGrid(rook);
            board.getUi().addPieceToGrid(rook);
            ChessGame.handleMessage(this.getColor() + " King castling performed Queen's side");
        }
    }



    public boolean move(int targetRow, int targetCol, Board board) {
        if (isValidMove(this.getRow(), this.getCol(), targetRow, targetCol, board.getBoard())) {
            if (Math.abs(targetCol - this.getCol()) == 2) {
                performCastlingMove(targetRow, targetCol, board);
            } else {
                // Normal move
                board.getBoard()[this.getRow()][this.getCol()] = null;
                this.setRow(targetRow);
                this.setCol(targetCol);
                board.getBoard()[this.getRow()][this.getCol()] = this;
            }
            hasMoved = true;
            return true;
        }
        return false;
    }



    @Override
    public String getUniCode() {
        return this.getColor().equals(Color.WHITE) ?  "♔" : "♚";
    }

}
