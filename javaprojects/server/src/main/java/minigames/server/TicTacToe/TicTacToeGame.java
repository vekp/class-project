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
    int currentPlayerIndex = 0; // index to decide the current player's turn

    public TicTacToeGame(String name, String firstPlayer) {
        this.name = name;
        Arrays.fill(board, '\0');  // Initialize the board to be empty
        players.add(firstPlayer);
    }

    public List<String> getPlayerNames() {
        return players;
    }

    public RenderingPackage joinGame(String playerName) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }
        return new RenderingPackage(name, playerName, boardState());
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        // Only the current player can make a move
        if (!cp.playerName().equals(players.get(currentPlayerIndex))) {
            return new RenderingPackage(name, cp.playerName(), "notYourTurn");
        }

        int move = cp.commands().get(0).getInteger("move");
        char currentPlayerMark = currentPlayerIndex == 0 ? 'X' : 'O';

        if (board[move] == '\0') {
            board[move] = currentPlayerMark;
            if (checkVictory(currentPlayerMark)) {
                return new RenderingPackage(name, cp.playerName(), "gameWon", currentPlayerMark);
            } else if (isBoardFull()) {
                return new RenderingPackage(name, cp.playerName(), "gameDraw");
            } else {
                // Switch to the other player
                currentPlayerIndex = 1 - currentPlayerIndex;
            }
        } else {
            return new RenderingPackage(name, cp.playerName(), "cellTaken");
        }
        return new RenderingPackage(name, cp.playerName(), boardState());
    }

    private boolean checkVictory(char mark) {
        // Implement the logic to check if 'mark' has won the game
        // Check rows, columns, and diagonals

        // Rows
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == mark && board[i+1] == mark && board[i+2] == mark) return true;
        }
        
        // Columns
        for (int i = 0; i < 3; i++) {
            if (board[i] == mark && board[i+3] == mark && board[i+6] == mark) return true;
        }
        
        // Diagonals
        if (board[0] == mark && board[4] == mark && board[8] == mark) return true;
        if (board[2] == mark && board[4] == mark && board[6] == mark) return true;

        return false;
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
