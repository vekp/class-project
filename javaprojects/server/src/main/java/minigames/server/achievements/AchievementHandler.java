package minigames.server.achievements;

import minigames.achievements.Achievement;

import java.util.*;

/**
 * A class holding registered achievements for a game server. Also, currently stores the unlock state for a player
 * for this game's achievements (which can be moved to a more appropriate place once user accounts are in)
 */
public class AchievementHandler {

    //This acts as our pseudo-database for registered achievements (may switch to a database system if one is
    //implemented later)
    static AchievementDatabase database = new AchievementDatabase();

    //Keyed by player ID, the hashset within contains the set of Achievement IDs for this handler that
    //a particular player has unlocked.
    //todo replace this with a player profile or some sort of player database
    private final Map<String, HashSet<String>> playerUnlockList = new HashMap<>();

    //the game server name associated with this handler. It is unique for each game server (though multiple
    //handlers can have this same id - they just access and handle achievements for the same server)
    private final String handlerID;

    /**
     * Constructor
     *
     * @param gameName the game/server name this handler will manage achievements for.
     */
    public AchievementHandler(String gameName) {
        handlerID = gameName;
    }

    public void registerAchievement(Achievement achievement) {
        if (database.getAchievement(handlerID, achievement.name()) != null) {
            //todo exception? cannot register duplicate achievements
            return;
        }
        database.addAchievement(handlerID, achievement.name(), achievement);
    }

    /**
     * Returns a list of all achievements available for the current game
     *
     * @return An ArrayList containing all available achievements
     */
    public ArrayList<Achievement> getAllAchievements() {
        //todo implement
        return new ArrayList<>();
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

        if (database.getAchievement(handlerID, achievementID) != null) {
            playerUnlockList.get(playerID).add(achievementID);
        } else {
            //todo throw exception? Cannot unlock nonexistant achievement
        }
    }

    /**
     * Checks whether a certain achievement has already been unlocked by a certain player
     *
     * @param playerID      The ID of the player to be checked
     * @param achievementID The ID of the achievement being checked
     * @return Whether the achievement has been unlocked by the player
     */
    public boolean playerHasEarnedAchievement(String playerID, String achievementID) {
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

}
