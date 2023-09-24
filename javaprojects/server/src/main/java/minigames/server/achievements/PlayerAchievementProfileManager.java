package minigames.server.achievements;

import java.util.*;


/**
 * Simple wrapper class to act as a 'database' for player profiles, for achievements only
 * This can be replaced by a proper profile manager when user accounts are implemented
 */
public class PlayerAchievementProfileManager {
    private final Set<PlayerAchievementProfile> playerProfiles = new HashSet<>();

    /**
     * Adds a player to the register.
     *
     * @param playerName the unique name of the player
     * @return true if successful, false if not (player already exists - mostly for testing)
     */
    public boolean addPlayer(String playerName) {
        PlayerAchievementProfile profile = new PlayerAchievementProfile(playerName);
        if (!playerProfiles.contains(profile)) {
            playerProfiles.add(profile);
            return true;
        }
        return false;
    }

    /**
     * Adds an achievement to the player's list of unlocked achievements. Will add the player to the profile
     * list if it doesnt exist
     * @param playerName the player ID to unlock achievements for
     * @param gameID the game server associated with the achievement
     * @param achievementID the achievement  to add
     */
    public void addPlayerAchievement(String playerName, String gameID, String achievementID) {
        //this won't add a duplicate player, but will add the name just in case it isn't already there
        addPlayer(playerName);
        PlayerAchievementProfile player = getPlayer(playerName);
        //we just added this profile so it will definitely be there
        //this won't add duplicates if player already has achievement
        player.addAchievement(gameID, achievementID);
    }

    /**
     * gets a map of recently unlocked achievements for a particular player
     * @param playerID the player to get the recents unlocks for
     * @return a map of achievements unlocks, split by game ID
     */
    public Map<String, HashSet<String>> getPlayerUnlocks(String playerID) {
        PlayerAchievementProfile profile = getPlayer(playerID);
        if (profile != null) {
            return profile.getRecentUnlocks();
        }
        return new HashMap<>();
    }

    /**
     * Tells us whether a player has unlocked a particular achievement
     * @param playerName the player to search for
     * @param gameID the game server this achievement is for
     * @param achievementID the achievement to check
     * @return true if the player has this achievement, false otherwise
     */
    public boolean doesPlayerHaveAchievement(String playerName, String gameID, String achievementID) {
        PlayerAchievementProfile player = getPlayer(playerName);
        //if the player exists, check if the achievement is in their profile (if they don't exist, they definitely
        //haven't unlocked anything
        if (player != null) {
            return player.hasEarnedAchievement(gameID, achievementID);
        }
        return false;
    }

    /**
     * Get a player profile by name. Null if it doesn't have one
     *
     * @param playerName name of the player to search for
     * @return the player's profile, or null if it isn't there
     */
    public PlayerAchievementProfile getPlayer(String playerName) {
        PlayerAchievementProfile testProfile = new PlayerAchievementProfile(playerName);
        for (PlayerAchievementProfile profile : playerProfiles) {
            if (profile.equals(testProfile)) return profile;
        }
        return null;
    }
}
