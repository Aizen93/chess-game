package org.aouessar.chessgame.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Stockfish {
    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    public Stockfish(String pathToEngine) {
        try {
            // Start the Stockfish process
            engineProcess = new ProcessBuilder(pathToEngine).start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());
            initializeEngine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeEngine() throws IOException {
        // Send the UCI initialization commands
        sendCommand("uci");
        waitForResponse("uciok");
    }

    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        try {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = processReader.readLine()) != null) {
                response.append(line).append("\n");
                if (line.startsWith("bestmove")) {
                    break;
                }
            }
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void waitForResponse(String expectedResponse) throws IOException {
        String line;
        while ((line = processReader.readLine()) != null) {
            if (line.startsWith(expectedResponse)) {
                break;
            }
        }
    }

    public String getBestMove(String fen, int depth, int skillLevel) {
        // Set the position and calculate the best move
        sendCommand("position fen " + fen);
        sendCommand("go depth " + depth);
        sendCommand("setoption name Skill Level value " + skillLevel);
        String response = getResponse();

        // Extract the best move from the response
        if (response != null) {
            for (String line : response.split("\n")) {
                if (line.startsWith("bestmove")) {
                    return line.split(" ")[1]; // The best move is the second word
                }
            }
        }
        return null;
    }

    public void close() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
            engineProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
