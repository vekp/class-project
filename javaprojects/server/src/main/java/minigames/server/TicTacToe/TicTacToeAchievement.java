package minigames.server.tictactoe;

import java.util.Arrays;

/**
 * Class representing achievements for the Tic Tac Toe game.
 */
public class TicTacToeAchievement {

    // Constants for the achievements
    private static final String QUICK_WIN = "Quick Win! (Winning in 5 moves)";
    private static final String COMPLETE_LINE = "Line Dominator (Completed a full row, column or diagonal)";
    private static final String DRAW_GAME = "Peace Keeper (Drew the game)";

    private char[][] board;
    
    public TicTacToeAchievement(char[][] board) {
        this.board = board;
    }

    /**
     * Checks and returns the achieved accomplishments.
     * 
     * @return String describing the achieved accomplishment, or null if none.
     */
    public String checkAchievement() {
        if (checkQuickWin()) {
            return QUICK_WIN;
        } else if (checkCompleteLine()) {
            return COMPLETE_LINE;
        } else if (checkDrawGame()) {
            return DRAW_GAME;
        }
        return null;
    }

    /**
     * Checks if the game was won in 5 moves.
     * 
     * @return true if achieved, false otherwise.
     */
    private boolean checkQuickWin() {
        int filledCells = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != '\0') {  // '\0' is default value for char.
                    filledCells++;
                }
            }
        }
        return filledCells == 5 && (checkRows() || checkColumns() || checkDiagonals());
    }

    /**
     * Checks if any row, column, or diagonal is completely filled by the same player.
     * 
     * @return true if achieved, false otherwise.
     */
    private boolean checkCompleteLine() {
        return checkRows() || checkColumns() || checkDiagonals();
    }

    /**
     * Checks if the game board is completely filled without any player winning.
     * 
     * @return true if achieved, false otherwise.
     */
    private boolean checkDrawGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    return false;
                }
            }
        }
        return !checkRows() && !checkColumns() && !checkDiagonals();
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '\0' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns() {
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != '\0' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        return (board[0][0] != '\0' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) ||
               (board[0][2] != '\0' && board[0][2] == board[1][1] && board[1][1] == board[2][0]);
    }
}
