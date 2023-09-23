package minigames.server.hangman;

import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in Muddle
 */
enum HangmanGameAchievement {
    MUDDLER (
            "You opened Hangman Game for the first time!",
            false
    );
    final Achievement achievement;

    HangmanGameAchievement(String description, boolean hidden) {
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


