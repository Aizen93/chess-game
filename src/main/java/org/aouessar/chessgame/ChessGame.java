package org.aouessar.chessgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChessGame extends Application {

    private static final int TILE_SIZE = 80;

    private static final int WIDTH = 8; // Chessboard width

    private static final int HEIGHT = 8; // Chessboard height


    @Override
    public void start(Stage stage) {
        GridPane gridPane = new GridPane();

        Board board = new Board(TILE_SIZE, WIDTH, HEIGHT);
        board.renderBoard(gridPane);

        // Display the stage
        Scene scene = new Scene(gridPane, TILE_SIZE * WIDTH, TILE_SIZE * HEIGHT);
        stage.setTitle("Chess Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}