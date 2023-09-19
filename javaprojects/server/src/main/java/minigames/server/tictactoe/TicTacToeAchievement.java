package minigames.server.tictactoe;

public enum TicTacToeAchievement {

    FIRST_WIN("First Win", "Win your first game of Tic Tac Toe"),
    FIVE_WINS("Five Wins", "Win five games of Tic Tac Toe"),
    TEN_WINS("Ten Wins", "Win ten games of Tic Tac Toe");

    public final String title;
    public final String description;

    TicTacToeAchievement(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
