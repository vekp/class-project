package minigames.server.snake;


import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in Muddle
 */
enum SnakeAchievement {
    SNAKE_WARRIOR (
            "You opened Snake for the first time!",
            false
    ),
    QUICK_STARTER(
            "Reached a length of 10 within 30 seconds.",
            false
    ),
    SPEED_DEMON(
            "Completed a level without pausing.",
            false
    ),
    PINPOINT_ACCURACY(
            "Collected 10 fruits in a row without touching the walls or yourself.",
            false
    ),
    UNTOUCHABLE(
            "Reach a length of 50 without losing a life.",
            false
    ),
    GROWING_UP(
            "Reached a snake length of 20 for the first time.",
            false
    ),
    MARATHONER(
            "Reached a snake length of 100.",
            false
    ),
    CENTURION(
            "Scored 100 points in a single game.",
            false
    ),
    MILLIONAIRE(
            "Accumulated 1,000 points across all games.",
            false
    ),
    FRUIT_SALAD(
            "Ate each type of fruit at least once in a single game.",
            false
    ),
    TIME_BENDER(
            "Played for a total of 1 hour.",
            false
    ),
    EGG_HUNTER(
            "Found and ate the hidden 'Golden Egg' fruit.",
            false
    ),
    IMMORTAL(
            "Completed a game without losing any lives.",
            false
    ),
    PERFECTIONIST(
            "Ate every single fruit that appeared.",
            false
    ),
    LEGENDARY_SNAKE(
            "Reached a length of 500.",
            false
    ),
    MASTER_OF_SNAKE(
            "Completed all levels.",
            false
    ),
    SOCIAL_SNAKE(
            "Shared your score on social media.",
            false
    ),
    FRIEND_OR_FOE(
            "Beat a friend's high score.",
            false
    );
    final Achievement achievement;

    SnakeAchievement(String description, boolean hidden) {
        this.achievement = new Achievement(
                this.toString(),
                description, 25,
                "",
                hidden
        );
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase().replace("_", " ");
    }
}