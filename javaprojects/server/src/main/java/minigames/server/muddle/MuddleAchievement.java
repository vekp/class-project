package minigames.server.muddle;


import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in Muddle
 */
enum MuddleAchievement {
    MUDDLER (
            "You opened Muddle for the first time!",
            false
    ),
    SOUTH_BUTTON_PUSHER(
            "You pushed the south button!",
            false
    ),
    EAST_BUTTON_PUSHER(
            "You pushed the east button!",
            false
    ),
    SAY_THE_MAGIC_WORD(
            "You said the magic word, Abracadabra!",
            true
    );
    final Achievement achievement;

    MuddleAchievement(String description, boolean hidden) {
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
