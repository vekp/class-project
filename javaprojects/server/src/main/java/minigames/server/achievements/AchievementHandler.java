package minigames.server.achievements;

import minigames.achievements.Achievement;
import minigames.server.GameServer;

import java.util.*;

/**
 * A class holding registered achievements for a game server. This class acts as a middleman to interact with the
 * database (which for now is just another class), but also provides functionality for unlocking achievements
 * (manually, or using a watcher)
 */
public class AchievementHandler {

    //This acts as our pseudo-database for registered achievements (may switch to a database system if one is
    //implemented later)
    private final static AchievementDatabase database = new AchievementDatabase();

    //Keyed by player ID, the hashset within contains the set of Achievement IDs keyed to this handler that
    //a particular player has unlocked.
    //todo replace this with a player profile or some sort of player database
    private final static Map<String, Map<String, String>> playerUnlockList = new HashMap<>();

    //the game server name associated with this handler. It is unique for each game server (though multiple
    //handlers can have this same id - they just access and handle achievements for the same server)
    private final String handlerID;

    /**
     * Constructor
     *
     * @param type the Game server type/class for which this handler should be managing achievements.
     *             Multiple handlers can share the same type - they will access the same entries in the database
     */
    public AchievementHandler(Class<? extends GameServer> type) {
        handlerID = type.getName();
    }

    /**
     * A method that provides access to the achievement database to add achievements to it. Will check to make sure that
     * the achievement is not a duplicate and throw an error if so.
     *
     * @param achievement the achievement to register
     */
    public void registerAchievement(Achievement achievement) {
        if (!database.addAchievement(handlerID, achievement)) {
            throw new IllegalArgumentException("Duplicate Achievement! Achievement - " + achievement.name() +
                    "has already been registered for handler ID: " + handlerID);
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
        //make a new player profile if needed
        if (!playerUnlockList.containsKey(playerID))
            playerUnlockList.put(playerID, new HashMap<>());

        //we will throw an error if we try to unlock an achievement that does not exist
        if (database.getAchievement(handlerID, achievementID) != null) {
            playerUnlockList.get(playerID).put(handlerID, achievementID);
        } else {
            throw new IllegalArgumentException("Achievement with ID: " + achievementID +
                    "does not exist for handler: " + handlerID);
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
        if (playerUnlockList.containsKey(playerID) &&playerUnlockList.get(playerID).containsKey(handlerID))
            return playerUnlockList.get(playerID).get(handlerID).contains(achievementID);
        else return false;
    }
}
