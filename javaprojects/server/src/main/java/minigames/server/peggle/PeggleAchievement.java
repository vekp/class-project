package minigames.server.peggle;


import minigames.achievements.Achievement;

/**
 * An enum to store details of each achievement in Muddle
 */
enum PeggleAchievement {

    MUDDLER(new Achievement(
            "Muddler",
            "You opened Muddle for the first time!",
            10,
            "",
            false
    )
    );

    // Field to store the achievement
    final Achievement achievement;
    /**
     * Constructor for this enum stores the achievement parameter in the achievement field
     */
    PeggleAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}

