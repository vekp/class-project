package minigames.server.achievements;

import java.util.*;

/**
 * A profile data class that maps a player's ID with a list of achievements that they have earned, separated by game.
 * This profile object could eventually be held in a player profile object (e.g. for user account data).
 */
public class PlayerAchievementProfile {

    private final String name;
    //a set of all achievements unlocked for this player (in their entire lifetime) - split by game
    private final Map<String, HashSet<String>> achievements = new HashMap<>();

    //a set of recently unlocked achievements (usually achievements unlocked within the last server update)
    //this is retrieved and cleared every time we want to display popups for unlocking achievements for this player
    private final Map<String, HashSet<String>> recentUnlocks = new HashMap<>();

    /**
     * Constructor
     * @param name The player name to which this profile belongs
     */
    public PlayerAchievementProfile(String name) {
        this.name = name;
    }

    /**
     * @return gets the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * adds an achievement to the player's list of unlocked achievements. This permanantly flags the achievement for
     * that game as 'unlocked' for this player.
     * @param gameID The game that we wish to unlock achievements for
     * @param achievementID the specific achievement that was unlocked.
     */
    public void addAchievement(String gameID, String achievementID) {
        if (!achievements.containsKey(gameID))
            achievements.put(gameID, new HashSet<>());
        achievements.get(gameID).add(achievementID);

        //also add to recent unlocks for popups
        if (!recentUnlocks.containsKey(gameID))
            recentUnlocks.put(gameID, new HashSet<>());
        recentUnlocks.get(gameID).add(achievementID);
    }

    /**
     * Helper class to quickly assess whether a player has unlocked a particular game's achievement
     * @param gameID The game we which to check achievements for
     * @param achievementID the specific achievement we want to check
     * @return true if the player has unlocked the achievement, false otherwise
     */
    public boolean hasEarnedAchievement(String gameID, String achievementID) {
        return achievements.containsKey(gameID) && achievements.get(gameID).contains(achievementID);
    }

    /**
     * Gets all the recently unlocked achievements for this player, and then clears the recents lists so
     * we don't duplicate notifications
     * @return a map of unlocked achievements (ones that were unlocked since this was last called)
     */
    public HashMap<String, HashSet<String>> getRecentUnlocks(){
        HashMap<String,HashSet<String>> result = new HashMap<>();
        for (Map.Entry<String, HashSet<String>> entry : recentUnlocks.entrySet()) {
            HashSet<String> achievements = new HashSet<>(entry.getValue());
            result.put(entry.getKey(), achievements);
        }
        recentUnlocks.clear();
        return result;
    }

    /**
     * Overriding the hashcode function so that players are identified by their name (for storing unique
     * player profiles in a set/hashmap etc)
     * @return the hashcode for the player's ID/name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * A player profile is 'equal' to another if their names are the same, as we are assuming player names are unique
     * @param obj the other player profile
     * @return true if the player names are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PlayerAchievementProfile p) && p.name.equals(name);
    }
}
