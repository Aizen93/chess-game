package org.aouessar.chessgame;

public class CommandLine {

    public void execute(String command, Board board) {
        if (command == null || command.trim().isEmpty()) {
            ChessGame.handleMessage("Command invalid");
            return ;
        }

        String[] parts = command.trim().split("\\s+");
        String mainCommand = parts[0];

        switch(mainCommand.toLowerCase()) {
            case "reset" -> board.restartGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

            case "save" -> {
                String fen = board.getParser().saveToFENString(
                    board.getGameState().isWhiteTurn(),
                    board.getBoard()
                );
                ChessGame.handleMessage(fen);
            }

            case "fen" -> {
                if(parts.length == 7 && !parts[1].isEmpty()) {
                    board.restartGame(String.join(" ", parts[1], parts[2], parts[3], parts[4], parts[5]));
                } else {
                    ChessGame.handleMessage("Command invalid : ex -> fen 8/8/8/8/8/8/2K5/4Rk2 w - - 0 1");
                }
            }

            default -> {
                if(isValidMoveFormat(command)){
                    int[] coordinates = parseMoveCommand(command);
                    board.setSelectedPiece(board.getBoard()[coordinates[0]][coordinates[1]]);
                    board.move(coordinates[2],coordinates[3]);
                } else {
                    ChessGame.handleMessage("Command invalid");
                }
            }
        }
    }



    private boolean isValidMoveFormat(String command) {
        return command != null && command.matches("^[a-h][1-8] [a-h][1-8]$");
    }



    public int[] parseMoveCommand(String command) {
        // Split the command into start and end positions
        String[] positions = command.split(" ");
        String start = positions[0];
        String end = positions[1];

        // Convert chess notation to row and column indices
        int startCol = start.charAt(0) - 'a'; // 'a' -> 0, 'b' -> 1, ..., 'h' -> 7
        int startRow = 8 - Character.getNumericValue(start.charAt(1)); // '1' -> 7, ..., '8' -> 0
        int endCol = end.charAt(0) - 'a';
        int endRow = 8 - Character.getNumericValue(end.charAt(1));

        return new int[]{startRow, startCol, endRow, endCol};
    }

}
