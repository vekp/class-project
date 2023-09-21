package minigames.server.memory;

import minigames.achievements.Achievement;

/**
 * An enum containing all Achievements to be passed to the AchievementHandler for tracking
 */

enum MemoryAchievement{
    TEST_THAT_MEMORY (
            "Test that memory achievement works!",
            false
    ),
    CARD_FLIPPER (
            "Flip your first card!",
            false
    ),
    DOUBLE_FLIPPER (
            "Already flipped you Mongoose.",
            false
    ),

    SPEED_RUNNER (
            "Earned by matching all of the cards in under a certain amount of time.",
            false
    ),
    MEMORY_MASTER (
            "Earned by matching all of the cards in a single game.",
            false
    ),
    CARD_MATCHER (
            "Earned by making your first match. Well done.",
            false
    );

    final Achievement achievement;


    MemoryAchievement(String description, boolean hidden) {
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
