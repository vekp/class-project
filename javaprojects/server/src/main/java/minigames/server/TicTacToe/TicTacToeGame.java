package minigames.server.tictactoe;

import minigames.commands.CommandPackage;
import minigames.rendering.RenderingPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToeGame {
    String name;
    char[] board = new char[9];  // 3x3 board, can be 'X', 'O', or '\0' (empty)
    List<String> players = new ArrayList<>();

    public TicTacToeGame(String name, String firstPlayer) {
        this.name = name;
        Arrays.fill(board, '\0');  // Initialize the board to be empty
        players.add(firstPlayer);
    }

    public List<String> getPlayerNames() {
        return players;
    }

    public RenderingPackage joinGame(String playerName) {
        players.add(playerName);
        return new RenderingPackage(name, playerName, boardState());
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        int move = cp.commands().get(0).getInteger("move");
        char currentPlayerMark = players.indexOf(cp.playerName()) == 0 ? 'X' : 'O';

        if (board[move] == '\0') {
            board[move] = currentPlayerMark;
            if (checkVictory(currentPlayerMark)) {
                return new RenderingPackage(name, cp.playerName(), "gameWon", currentPlayerMark);
            } else if (isBoardFull()) {
                return new RenderingPackage(name, cp.playerName(), "gameDraw");
            }
        }
        return new RenderingPackage(name, cp.playerName(), boardState());
    }

    private boolean checkVictory(char mark) {
        // Implement the logic to check if 'mark' has won the game
        // Check rows, columns, and diagonals
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == '\0') return false;
        }
        return true;
    }

    private String boardState() {
        return new String(board);
    }
}
