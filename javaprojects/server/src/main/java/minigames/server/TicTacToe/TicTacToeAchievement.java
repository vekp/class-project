package minigames.server.tictactoe;

import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in TicTacToe
 */
enum TicTacToeAchievement {
    TICTACTOE_BEGINNER(new Achievement(
        "TicTacToe Beginner",
        "You started a TicTacToe game for the first time!",
        10,
        "",
        false
        )
    ),
    FIRST_MOVE(new Achievement(
        "First Move Maker",
        "You made your first move in TicTacToe!",
        20,
        "",
        false
        )
    ),
    WINNER(new Achievement(
        "Winner Winner",
        "You won a game of TicTacToe!",
        50,
        "",
        true
        )
    ),
    UNDEFEATED(new Achievement(
        "Undefeated",
        "You won 10 games in a row!",
        100,
        "",
        true
        )
    );

    // Field to store the achievement
    final Achievement achievement;

    /**
     * Constructor for this enum stores the achievement parameter in the achievement field
     */
    TicTacToeAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}
