package org.aouessar.chessgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChessGame extends Application {

    public static final int TILE_SIZE = 100;

    public static final int WIDTH = 8; // Chessboard width

    public static final int HEIGHT = 8; // Chessboard height

    public static TextArea messageArea;


    @Override
    public void start(Stage stage) {
        // Create the chessboard grid
        GridPane gridPane = new GridPane();


        // Create the chess board and initialize it
        Board board = new Board(TILE_SIZE, WIDTH, HEIGHT, gridPane);
        board.renderBoard();
        board.initializePieces("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", gridPane);

        addAnnotations(gridPane);

        // Create the console area
        VBox consoleBox = new VBox();
        consoleBox.setSpacing(10);

        // TextArea for displaying messages
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefHeight((HEIGHT * TILE_SIZE) - 50); // Adjust as needed
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: white;");

        // TextField for entering commands
        TextField commandInput = new TextField();
        commandInput.setPromptText("Enter command (e.g., Re5, Pb6)...");

        // Handle commands when the user presses Enter
        commandInput.setOnAction(event -> {
            String command = commandInput.getText().trim();
            if (!command.isEmpty()) {
                handleMessage(command);
                commandInput.clear();
            }
        });

        // Add TextArea and TextField to the VBox
        consoleBox.getChildren().addAll(messageArea, commandInput);

        // Layout using BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(gridPane); // Add the chessboard to the center
        root.setRight(consoleBox); // Add the console to the right

        // Display the stage
        Scene scene = new Scene(root, TILE_SIZE * WIDTH + 300, TILE_SIZE * HEIGHT + 7);
        stage.setTitle("Chess Game");
        stage.setScene(scene);
        stage.show();
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



    /**
     * Adds chessboard annotations (like "a1", "h8") to the grid.
     *
     * @param gridPane The GridPane representing the chessboard.
     */
    private void addAnnotations(GridPane gridPane) {
        // Add column labels (a-h) at the bottom
        for (int col = 1; col <= WIDTH; col++) {
            Text label = new Text(String.valueOf((char) ('a' + col - 1)));
            if ((col - 1) % 2 == 1) {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #739552; -fx-font-size: 16px;");
                label.setTranslateY(40);
                label.setTranslateX(85);
            } else {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #ebecd0; -fx-font-size: 16px;");
                label.setTranslateY(40);
                label.setTranslateX(85);
            }

            gridPane.add(label, col - 1, 7);
        }



        for(int row = 1; row <= HEIGHT; row++){
            Text label = new Text(String.valueOf(HEIGHT + 1 - row));
            if ((row - 1) % 2 == 0) {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #739552; -fx-font-size: 16px;");
                label.setTranslateY(-40);
            } else {
                label.setStyle("-fx-font-weight: bold; -fx-fill: #ebecd0; -fx-font-size: 16px;");
                label.setTranslateY(-40);
            }

            gridPane.add(label, 0, row - 1);
        }

        // Shift chessboard tiles to align with annotations
        gridPane.setHgap(1); // Optional spacing for better visibility
        gridPane.setVgap(1);
    }



    public static void main(String[] args) {
        launch();
    }
}