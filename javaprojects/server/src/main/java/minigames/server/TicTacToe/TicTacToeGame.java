package minigames.server.tictactoe;

import minigames.server.achievements.AchievementHandler;
import io.vertx.core.json.JsonObject;

public class TicTacToeGame {

    // Constants to define the size of the board
    private final static int SIZE = 3;

    // 2D array representing the Tic Tac Toe board
    private char[][] board = new char[SIZE][SIZE];

    // Current player's turn, initialized to 'X'
    private char currentPlayer = 'X';

    // Keep track of the number of moves to determine a draw
    private int movesCount = 0;

    // Handle achievements for the game
    AchievementHandler achievementHandler;

    // Name of the game and player
    String gameName;
    String playerName;

    // Constructor to initialize the Tic Tac Toe game
    public TicTacToeGame(String gameName, String playerName) {
        this.gameName = gameName;
        this.playerName = playerName;

        // Initialize achievement handler
        this.achievementHandler = new AchievementHandler(TicTacToeServer.class);

        // Fill board with spaces to represent empty cells
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = ' ';
            }
        }
    }

    // Method to handle a player's move
    public String makeMove(int row, int col) {
        // Validate the move's coordinates
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return "Invalid move.";
        }

        // Check if the selected cell is empty
        if (board[row][col] == ' ') {
            board[row][col] = currentPlayer;  // Place the current player's symbol
            movesCount++;

            // Check if the current move resulted in a win
            if (checkWin(row, col)) {
                achievementHandler.unlockAchievement(playerName, "WINNER");
                return currentPlayer + " wins!";
            }

            // Check if the board is full (resulting in a draw)
            if (movesCount == SIZE * SIZE) {
                return "Draw!";
            }

            // Switch to the other player
            togglePlayer();
            return currentPlayer + "'s turn.";
        }
        return "Cell already taken.";
    }

    // Check if the given move leads to a win
    private boolean checkWin(int row, int col) {
        // Check if current row, column, or either diagonal has all the same symbols
        return (board[row][0] == currentPlayer && board[row][1] == currentPlayer && board[row][2] == currentPlayer) ||
               (board[0][col] == currentPlayer && board[1][col] == currentPlayer && board[2][col] == currentPlayer) ||
               (row == col && board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) ||
               (row + col == SIZE - 1 && board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer);
    }

    // Toggle the currentPlayer from 'X' to 'O' or vice versa
    private void togglePlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    // Return the current state of the board as a JsonObject
    public JsonObject boardState() {
        JsonObject state = new JsonObject();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                state.put("cell_" + i + "_" + j, board[i][j]);
            }
        }
        return state;
    }

    // Get the current player's symbol ('X' or 'O')
    public char getCurrentPlayer() {
        return currentPlayer;
    }
    
    // Check if the game is over (board is full without a winner)
    public boolean isGameOver() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
