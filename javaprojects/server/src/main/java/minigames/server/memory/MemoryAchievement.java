package minigames.server.memory;

import minigames.achievements.Achievement;

// Achievements for the Memory game.
enum MemoryAchievement{
    TEST_THAT_MEMORY (
            "Test that memory achievement works!",
            false
    ),
    CARD_FLIPPER (
            "Flip your first card!",
            false
    ),
    PERFECT_MATCH (
            "Earned by matching all of the cards in one turn.",
            false
    ),

    SPEED_RUNNER (
            "Earned by matching all of the cards in less than a second.",
            false
    ),
    MEMORY_MASTER (
            "Earned by matching all of the cards in less than a second.",
            false
    ),
    ACE_MATCHER (
            "Earned by matching all of the cards in less than a second.",
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
