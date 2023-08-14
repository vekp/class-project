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

    private final static PlayerAchievementProfileManager playerManager = new PlayerAchievementProfileManager();

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

    public String getHandlerID() {
        return handlerID;
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
        //this will either add the player, or they were already there
        playerManager.addPlayer(playerID);

        PlayerAchievementProfile player = playerManager.getPlayer(playerID);
        if(!player.hasEarnedAchievement(handlerID, achievementID)){
            //todo if this is first time player has earned achievement, we can do a notification /popup
        }
        //this won't add duplicates if player already has achievement
        player.addAchievement(handlerID, achievementID);
    }

    /**
     * Checks whether a certain achievement has already been unlocked by a certain player
     *
     * @param playerID      The ID of the player to be checked
     * @param achievementID The ID of the achievement being checked
     * @return Whether the achievement has been unlocked by the player
     */
    public boolean playerHasEarnedAchievement(String playerID, String achievementID) {
        PlayerAchievementProfile player = playerManager.getPlayer(playerID);
        if(player != null) return player.hasEarnedAchievement(handlerID, achievementID);
        else return false;
    }
}
