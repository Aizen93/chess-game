package org.aouessar.chessgame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.aouessar.chessgame.factory.Piece;

public class ChessGame extends Application {

    public static final int TILE_SIZE = 100;

    public static final int WIDTH = 8; // Chessboard width

    public static final int HEIGHT = 8; // Chessboard height

    public static TextArea messageArea;

    private static boolean isAnimating = false;


    @Override
    public void start(Stage stage) {
        // Create the chessboard grid
        GridPane gridPane = new GridPane();

        // Create the chess board and initialize it
        Board board = new Board(TILE_SIZE, WIDTH, HEIGHT, gridPane);
        board.renderBoard();
        board.initializePieces("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", gridPane);
        board.addAnnotations();

        // Create the console area
        VBox consoleBox = new VBox();
        consoleBox.setSpacing(10);

        // TextArea for displaying messages
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefHeight((HEIGHT * TILE_SIZE) - 50); // Adjust as needed
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: white;");
        messageArea.setFocusTraversable(false);

        TextField commandInput = getTextField(board);

        // Add TextArea and TextField to the VBox
        consoleBox.getChildren().addAll(messageArea, commandInput);

        // Layout using BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(gridPane); // Add the chessboard to the center
        root.setRight(consoleBox); // Add the console to the right

        // Display the stage
        Scene scene = new Scene(root, TILE_SIZE * WIDTH + 485, TILE_SIZE * HEIGHT + 7);
        stage.setTitle("Chess Game");
        stage.setScene(scene);
        stage.show();
    }



    private static TextField getTextField(Board board) {
        TextField commandInput = new TextField();
        commandInput.setPromptText("Enter command (e.g., g1 h3 moves rook g1 to h3)...");
        commandInput.setFocusTraversable(false);

        CommandLine commandLine = new CommandLine();

        // Handle commands when the user presses Enter
        commandInput.setOnAction(event -> {
            String command = commandInput.getText().trim();
            if (!command.isEmpty()) {
                commandLine.execute(command, board);
                commandInput.clear();
            }
        });
        return commandInput;
    }




    /**
     * Handle console commands.
     *
     * @param command The user-entered command.
     */
    public static void handleMessage(String command) {
        if(command.length() > 10) {
             messageArea.appendText("$System> " + command + "\n");
        }
        else messageArea.appendText("$Command> " + command + "\n");
    }



    public static Rectangle findRectangleInGrid(Piece king, GridPane grid) {
        int targetRow = king.getRow();
        int targetCol = king.getCol();

        for (Node node : grid.getChildren()) {
            // Directly fetch and compare row and column indices
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);

            // Default row/col to 0 if null (nodes default to (0,0) if not explicitly set)
            row = (row == null) ? 0 : row;
            col = (col == null) ? 0 : col;

            // Match the target indices and ensure the node is a Rectangle
            if (row == targetRow && col == targetCol && node instanceof Rectangle) {
                return (Rectangle) node; // Return immediately upon finding the target Rectangle
            }
        }
        return null; // Return null if no match is found
    }



    public static void flashTile(Piece king, GridPane grid) {
        if (isAnimating) {
            return;
        }

        isAnimating = true;

        Rectangle rectangle = findRectangleInGrid(king, grid);

        // Store the original color
        assert rectangle != null;
        Color originalColor = (Color) rectangle.getFill();

        Rectangle tempRectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        tempRectangle.setFill(originalColor);
        tempRectangle.setOpacity(0.5);
        grid.add(tempRectangle, king.getCol(), king.getRow());

        Timeline timeline = new Timeline();

        for (int i = 0; i < 6; i++) {
            // Alternate between red and original color
            boolean isRed = (i % 2 == 0);
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * 200), // 200ms intervals
                    e -> tempRectangle.setFill(isRed ? Color.RED : originalColor)
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        // When the animation finishes, re-enable the rectangle
        timeline.setOnFinished(e -> {
            grid.getChildren().remove(tempRectangle);
            isAnimating = false;
        });

        // Play the timeline
        timeline.play();
    }



    public static void showCheckmatePopup(Board board, String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        // Create a "Restart Game" button
        ButtonType restartButton = new ButtonType("Restart Game");
        alert.getButtonTypes().setAll(restartButton, ButtonType.CLOSE);

        alert.showAndWait().ifPresent(response -> {
            if (response == restartButton) {
                board.restartGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            }
        });
    }



    public static void main(String[] args) {
        launch();
    }
}