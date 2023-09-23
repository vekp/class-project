package minigames.server.tictactoe;

import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in TicTacToe
 */
public enum TicTacToeAchievement {
    FIRST_MOVE(new Achievement(
            "First Mover",
            "You made your first move in TicTacToe!",
            10,
            "",
            false)),
    FIRST_WIN(new Achievement(
            "First Win",
            "You won a game of TicTacToe for the first time!",
            50,
            "",
            false)),
    UNBEATABLE(new Achievement(
            "Unbeatable",
            "You won 10 games in a row in TicTacToe!",
            100,
            "",
            true)),
    GOOD_SPORTSMANSHIP(new Achievement(
            "Good Sportsmanship",
            "You played 50 games of TicTacToe!",
            25,
            "",
            false));

    // Field to store the achievement
    final Achievement achievement;

    /**
     * Constructor for this enum stores the achievement parameter in the achievement
     * field
     */
    TicTacToeAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}
