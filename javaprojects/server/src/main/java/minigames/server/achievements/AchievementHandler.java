package minigames.server.achievements;

import minigames.achievements.Achievement;

import java.util.*;

/**
 * A class holding registered achievements for a game server. This class acts as a middleman to interact with the
 * database (which for now is just another class), but also provides functionality for unlocking achievements
 * (manually, or using a watcher)
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

    /**
     * A method that provides access to the achievement database to add achievements to it. Will check to make sure that
     * the achievement is not a duplicate and throw an error if so.
     * @param achievement the achievement to register
     */
    public void registerAchievement(Achievement achievement) {
        if (!database.addAchievement(handlerID, achievement)) {
            //todo probably throw an exception if we tried to add a duplicate achievement
        }
    }

    /**
     * Returns a list of all achievements available for this handler
     *
     * @return An ArrayList containing all available achievements
     */
    public List<Achievement> getAllAchievements() {
        return database.getAchievementsByGame(handlerID);
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
