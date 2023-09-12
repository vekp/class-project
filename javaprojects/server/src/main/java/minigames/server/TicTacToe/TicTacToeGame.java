package minigames.server.tictactoe;

import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;

import java.util.HashMap;
import java.util.Map;

public class TicTacToeGame {

    private final Map<String, Character> players = new HashMap<>();
    private final char[][] board = new char[3][3];
    private String currentPlayer;

    public TicTacToeGame() {
        // Initialize the board with empty spaces
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public RenderingPackage joinGame(String playerName) {
        if (players.size() < 2) {
            char symbol = players.isEmpty() ? 'X' : 'O';
            players.put(playerName, symbol);
            if (players.size() == 1) {
                currentPlayer = playerName;
            }
            return new RenderingPackage(gameMetadata(), null);
        } else {
            return null; // Handle error for game full.
        }
    }

    public String[] getPlayerNames() {
        return players.keySet().toArray(new String[0]);
    }

    public GameMetadata gameMetadata() {
        return new GameMetadata("TicTacToe", String.join(" vs ", getPlayerNames()), getPlayerNames(), true);
    }

    public RenderingPackage processCommand(CommandPackage cp) {
        // Assume command has the format "move:x,y" where x and y are coordinates
        if (cp.command.startsWith("move:")) {
            String[] parts = cp.command.split(":")[1].split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (isValidMove(x, y)) {
                board[x][y] = players.get(currentPlayer);
                if (checkWinCondition(x, y, players.get(currentPlayer))) {
                    return new RenderingPackage(gameMetadata(), currentPlayer + " wins!");
                }
                switchTurns();
            } else {
                return new RenderingPackage(gameMetadata(), "Invalid move");
            }
        }
        return new RenderingPackage(gameMetadata(), null);
    }

    private boolean isValidMove(int x, int y) {
        return board[x][y] == ' ';
    }

    private boolean checkWinCondition(int x, int y, char symbol) {
        // Check rows, columns, and diagonals
        return (board[x][0] == symbol && board[x][1] == symbol && board[x][2] == symbol)
            || (board[0][y] == symbol && board[1][y] == symbol && board[2][y] == symbol)
            || (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
            || (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol);
    }

    private void switchTurns() {
        for (String player : players.keySet()) {
            if (!player.equals(currentPlayer)) {
                currentPlayer = player;
                break;
            }
        }
    }
}
