package org.aouessar.chessgame.piece.factory;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.aouessar.chessgame.Board;
import org.aouessar.chessgame.ChessGame;
import org.aouessar.chessgame.domain.Color;
import org.aouessar.chessgame.piece.Piece;

public class Pawn extends Piece {

    public Pawn(char name, Color color, int row, int col, Image icon) {
        super(name, color, row, col, icon);
    }



    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int direction = isWhite() ? -1 : 1;

        // Standard single move
        if (endX == startX + direction && endY == startY && board[endX][endY] == null) {
            return true;
        }

        // Double move from starting position
        int startRow = isWhite() ? 6 : 1;
        if (startX == startRow && endX == startX + 2 * direction && endY == startY && board[endX][endY] == null && board[startX + direction][startY] == null) {
            return true;
        }

        // Diagonal capture
        return endX == startX + direction && Math.abs(endY - startY) == 1 && board[endX][endY] != null && !isFriendlyPiece(endX, endY, board);// Invalid move
    }



    public boolean isPromotionRow() {
        return (isWhite() && this.getRow() == 0) || (!isWhite() && this.getRow() == 7);
    }



    public void promotePawn(Board originalBoard) {
        Platform.runLater(() -> {
            // Create a new Stage for the promotion dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Pawn Promotion");

            // HBox to hold piece options
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(20);

            // List of piece types
            String[] pieceTypes = {"Queen", "Rook", "Bishop", "Night"};
            String defaultChoice = pieceTypes[0];

            for (String pieceType : pieceTypes) {
                char piecePrefix = isWhite() ? pieceType.charAt(0) : Character.toLowerCase(pieceType.charAt(0));

                // Create an ImageView for each piece
                ImageView imageView = new ImageView(originalBoard.getPieceImages().get(piecePrefix));
                imageView.setFitHeight(60);
                imageView.setFitWidth(60);

                // Create a Button for each piece option
                Button button = new Button();
                button.setGraphic(imageView);
                button.setOnAction(e -> {
                    promoteToSelectedPiece(pieceType, originalBoard);

                    // Close the dialog
                    dialogStage.close();
                });

                hbox.getChildren().add(button);
            }

            // Set up the scene and show the dialog
            Scene scene = new Scene(hbox);
            dialogStage.setScene(scene);

            // Set default on close request
            dialogStage.setOnCloseRequest(e -> promoteToSelectedPiece(defaultChoice, originalBoard));

            dialogStage.showAndWait();
        });
    }



    private void promoteToSelectedPiece(String pieceType, Board originalBoard) {
        Piece[][] board = originalBoard.getBoard();

        Piece newPiece = switch (pieceType) {
            case "Rook" -> {
                char name = isWhite() ? 'R' : 'r';
                yield new Rook(name, this.getColor(), this.getRow(), this.getCol(), originalBoard.getPieceImages().get(name));
            }
            case "Bishop" -> {
                char name = isWhite() ? 'B' : 'b';
                yield new Bishop(name, this.getColor(), this.getRow(), this.getCol(), originalBoard.getPieceImages().get(name));
            }
            case "Night" -> {
                char name = isWhite() ? 'N' : 'n';
                yield new Knight(name, this.getColor(), this.getRow(), this.getCol(), originalBoard.getPieceImages().get(name));
            }
            default -> {
                char name = isWhite() ? 'Q' : 'q';
                yield new Queen(name, this.getColor(), this.getRow(), this.getCol(), originalBoard.getPieceImages().get(name));
            }
        };

        // Replace the pawn with the new piece on the board
        board[this.getRow()][this.getCol()] = newPiece;

        // Update the GUI to reflect the new piece
        originalBoard.getUi().addPieceToGrid(newPiece);
        originalBoard.getUi().removePieceFromGrid(this);

        // Optional: Log the promotion
        ChessGame.handleMessage(this.getColor() + " Pawn promoted to " + newPiece.getClass().getSimpleName());
    }


    @Override
    public String getUniCode() {
        return this.getColor().equals(Color.WHITE) ?  "♙" : "♟";
    }

}
