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

    //manager class responsible for holding all of the player achievement profiles
    private final static PlayerAchievementProfileManager playerManager = new PlayerAchievementProfileManager();

    //This stores recently unlocked achievements, so that notifications can be played on the client.
    private static final Queue<Achievement> recentUnlocks = new PriorityQueue<>();

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
     * Get the handlers ID
     *
     * @return the achievement handler's ID, which is the classname of the server that this handler is for
     */
    public String getHandlerID() {
        return handlerID;
    }

    /**
     * Getter for the list of recently unlocked achievements. This returns ALL achievements that have recently been
     * unlocked (regardless of game - though typically when running only 1 client this is only going to contain 1
     * game's achievements)
     *
     * @return A list of achievements that were just unlocked. Sorted by order that they were added to the queue
     */
    public static List<Achievement> getRecentUnlocks() {
        List<Achievement> result = new ArrayList<>(recentUnlocks.stream().toList());
        //once the recent unlocks are requested, it is presumed they will be turned into notifications, so this queue
        //is now cleared as these are no longer the 'recent' unlocks.
        recentUnlocks.clear();
        return result;
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
        //throw an error if we try to unlock an achievement that either doesnt exist, or isnt ours
        if (database.getAchievement(handlerID, achievementID) == null) {
            throw new IllegalArgumentException("Achievement { " + achievementID + " } does not exist for this game { " +
                    handlerID + " }. Please ensure all achievements are registered before trying to use");
        }
        //this will either add the player, or they were already there
        playerManager.addPlayer(playerID);

        PlayerAchievementProfile player = playerManager.getPlayer(playerID);
        if (!player.hasEarnedAchievement(handlerID, achievementID)) {
            //if the player has not unlocked this achievement before, it is a new achievement and needs to have a
            //notification popup, so we add it to our recents list
            Achievement result = database.getAchievement(handlerID, achievementID);
            recentUnlocks.add(result);
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
        if (player != null) return player.hasEarnedAchievement(handlerID, achievementID);
        else return false;
    }
}
