package org.aouessar.chessgame.factory;

import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.aouessar.chessgame.ChessGame;
import org.aouessar.chessgame.Color;
import org.aouessar.chessgame.Rules;

@Getter
@Setter
public class King extends Piece {

    private boolean hasMoved;



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
            return canCastle(startX, startY, endX, endY, board);
        }

        return false;
    }



    private boolean canCastle(int startX, int startY, int endX, int endY, Piece[][] board) {
        // Kingside castling
        if (endY > startY) {
            // Check squares between king and rook
            for (int col = startY + 1; col < endY; col++) {
                if (board[startX][col] != null) return false;
            }

            // Check if the rook has moved
            Piece rook = board[startX][7];
            if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) return false;

            // Check if the king is in check or would move through check
            for (int col = startY; col <= endY; col++) {
                if (Rules.isInCheck(isWhite(), board.length, board[0].length, board)) {
                    return false;
                }
            }

            return true;
        } else {
            // Queenside castling
            // Check squares between king and rook
            for (int col = startY - 1; col > endY; col--) {
                if (board[startX][col] != null) return false;
            }

            // Check if the rook has moved
            Piece rook = board[startX][0];
            if (!(rook instanceof Rook) || ((Rook) rook).hasMoved()) return false;

            // Check if the king is in check or would move through check
            for (int col = startY; col >= endY; col--) {
                if (Rules.isInCheck(isWhite(), board.length, board[0].length, board)) {
                    return false;
                }
            }

            return true;
        }
    }



    public void performCastlingMove(int targetRow, int targetCol, Piece[][] board, GridPane grid) {
        // Kingside castling
        if (targetCol == 6) {
            // Move the king
            board[targetRow][this.getCol()] = null;
            this.setCol(6);
            board[targetRow][this.getCol()] = this;

            // Move the rook
            Piece rook = board[targetRow][7];
            board[targetRow][7] = null;
            rook.setCol(5);
            board[targetRow][5] = rook;

            grid.getChildren().remove(rook.getIcon());
            rook.addPieceToGrid(grid);
            ChessGame.handleMessage(this.getColor() + " King castling performed King's side");

        } else if (targetCol == 2) {
            // Queenside castling
            // Move the king
            board[targetRow][this.getCol()] = null;
            this.setCol(2);
            board[targetRow][this.getCol()] = this;

            // Move the rook
            Piece rook = board[targetRow][0];
            board[targetRow][0] = null;
            rook.setCol(3);
            board[targetRow][3] = rook;

            grid.getChildren().remove(rook.getIcon());
            rook.addPieceToGrid(grid);
            ChessGame.handleMessage(this.getColor() + " King castling performed Queen's side");
        }
    }



    public boolean move(int targetRow, int targetCol, Piece[][] board, GridPane grid) {
        if (isValidMove(this.getRow(), this.getCol(), targetRow, targetCol, board)) {
            if (Math.abs(targetCol - this.getCol()) == 2) {
                performCastlingMove(targetRow, targetCol, board, grid);
            } else {
                // Normal move
                board[this.getRow()][this.getCol()] = null;
                this.setRow(targetRow);
                this.setCol(targetCol);
                board[this.getRow()][this.getCol()] = this;
            }
            hasMoved = true;
            return true;
        }
        return false;
    }



    public boolean hasMoved() {
        return hasMoved;
    }

}
