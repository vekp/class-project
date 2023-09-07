package minigames.server.muddle;


import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in Muddle
 */
enum MuddleAchievement {
    MUDDLER(new Achievement(
        "Muddler",
        "You opened Muddle for the first time!",
        10,
        "",
        false
        )
    ),
    SOUTH_BUTTON_PUSHER(new Achievement(
        "South button pusher",
        "You pushed the south button!",
        25,
        "",
        false
        )
    ),
    EAST_BUTTON_PUSHER(new Achievement(
        "East button pusher",
        "You pushed the East button!",
        25,
        "",
        false
        )
    ),
    SAY_THE_MAGIC_WORD(new Achievement(
        "Say the magic word",
        "You said the magic word, Abracadabra!",
        50,
        "",
        true
        )
    );

    // Field to store the achievement
    final Achievement achievement;

    /**
     * Constructor for this enum stores the achievement parameter in the achievement field
     */
    MuddleAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}

