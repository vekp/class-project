package minigames.achievements;

import java.util.*;

/**
 * The AchievementHandler keeps track of Achievements and whether the player has unlocked them.
 */
public class AchievementHandler {

    private final Map<String, Achievement> achievements = new HashMap<>();          // A Map containing all achievable Achievements
    private final Map<String, HashSet<String>> playerUnlockList = new HashMap<>();  // A Map containing all Achievements players have unlocked
    private final String gameID;                                                    // The String for the game's ID

    /**
     * Constructor
     *
     * @param gameServer The id for the current game server
     */
    public AchievementHandler(String gameServer) {
        this.gameID = gameServer;
    }

    /**
     * Returns the ID for the game in-progress
     *
     * @return A String for the game's server ID
     */
    public String getGameID() {
        return gameID;
    }

    /**
     * Returns a list of all Player IDs that achievements against their name
     *
     * @return A HashSet containing all players who are in the playerUnlockList (those who have received achievements)
     */
    public HashSet<String> getPlayers() {
        return new HashSet<>(playerUnlockList.keySet());
    }

    /**
     * Returns a list of all achievements available for the current game
     *
     * @return An ArrayList containing all available achievements
     */
    public ArrayList<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }

    /**
     * Adds an achievement to the list of obtainable achievements
     *
     * @param a The Achievement Object to be added to the Map
     */
    public void addAchievement(Achievement a) {
        String key = a.name();
        if (!achievements.containsKey(key))
            achievements.put(key, a);
    }

    /**
     * Adds multiple achievements to the list of obtainable achievements
     *
     * @param achievementList An ArrayList containing Achievement Objects to be added to the Achievement map
     */
    public void addAchievementMultiple(ArrayList<Achievement> achievementList) {
        for (Achievement achievement : achievementList) {
            addAchievement(achievement);
        }
    }

    /**
     * Adds an achievement and the playerID of the player who has unlocked it to the playerUnlockList Map
     *
     * @param playerID      The ID of the player who has unlocked the achievement
     * @param achievementID The ID of the achievement
     */
    public void unlockAchievement(String playerID, String achievementID) {
        if (!playerUnlockList.containsKey(playerID))
            playerUnlockList.put(playerID, new HashSet<>());

        //cannot unlock achievements this game doesn't have
        if (achievements.containsKey(achievementID)) {
            playerUnlockList.get(playerID).add(achievementID);
        }
    }

    /**
     * Checks whether a certain achievement has already been unlocked by a certain player
     *
     * @param playerID      The ID of the player to be checked
     * @param achievementID The ID of the achievement being checked
     * @return Whether the achievement has been unlocked by the player
     */
    public boolean hasPlayerEarnedAchievement(String playerID, String achievementID) {
        if (playerUnlockList.containsKey(playerID))
            return playerUnlockList.get(playerID).contains(achievementID);
        else return false;
    }

    /**
     * Get a list of all achievements unlocked by a certain player
     *
     * @param playerID String representing player's ID
     * @return the set of unlocked achievements for the given playerID
     */
    public HashSet<String> getPlayerUnlockList(String playerID) {
        if (playerUnlockList.containsKey(playerID))
            return new HashSet<>(playerUnlockList.get(playerID));

        return new HashSet<>();
    }

    /**
     * Get information of a specific Achievement using its ID
     *
     * @param achievementID String representation of achievement
     * @return the associated Achievement
     */
    public Achievement getAchievementFromID(String achievementID) {
        if (achievements.containsKey(achievementID)) return achievements.get(achievementID);
        return null;
    }
}
