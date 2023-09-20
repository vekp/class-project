package minigames.server.achievements;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
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

    //manager class responsible for holding all the player achievement profiles
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
     * @return A list of achievements that were just unlocked.
     */
    public static List<Achievement> getRecentUnlocks(String player) {
        List<Achievement> result = new ArrayList<>();
        PlayerAchievementProfile profile = playerManager.getPlayer(player);
        //if this profile exists, we grab their recent unlocks, and loop through it, retrieving
        //the achievement data for each game from the database and putting it in the results
        if (profile != null) {
            for (Map.Entry<String, HashSet<String>> entry : profile.getRecentUnlocks().entrySet()) {
                for (String achievementName : entry.getValue()) {
                    Achievement achievement = database.getAchievement(entry.getKey(), achievementName);
                    if (achievement != null)
                        result.add(achievement);
                }
            }
        }
        //this will be empty if there are no unlocks (or no existing player)
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
        //throw an error if we try to unlock an achievement that either doesn't exist, or isn't ours
        if (database.getAchievement(handlerID, achievementID) == null) {
            throw new IllegalArgumentException("Achievement { " + achievementID + " } does not exist for this game { " +
                    handlerID + " }. Please ensure all achievements are registered before trying to use");
        }
        //this will either add the player, or they were already there
        playerManager.addPlayer(playerID);
        PlayerAchievementProfile player = playerManager.getPlayer(playerID);
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

    /**
     * Puts together a game state object containing the list of locked, unlocked, and hidden achievements
     * for a particular player. Used by the server to put together data packets to send to the client for display
     *
     * @param playerID the player we want achievements for
     * @param gameName the name we want to attach to the achievement data. Since the handler usually is keyed by a
     *                 class type (not a game name), it might be more appropriate to pass in a game's title for the
     *                 user to read.
     * @return a game state object containing lists of unlocked and locked achievements for this player in this game
     */
    public GameAchievementState getAchievementState(String playerID, String gameName) {
        List<Achievement> gameAchievements = database.getAchievementsByGame(getHandlerID());
        //if there are no achievements for this handler we will not provide a state for it
        if (gameAchievements.size() == 0) return null;

        GameAchievementState state = new GameAchievementState(gameName, new ArrayList<>(), new ArrayList<>());

        //hidden achievements that have not yet been unlocked should be shown at the END of the list of locked
        //achievements, put them in here for now so they can be added afterwards
        List<Achievement> hiddenLocked = new ArrayList<>();
        //the list used depends on whether the player has this achievement or not
        for (Achievement current : gameAchievements) {
            if (playerHasEarnedAchievement(playerID, current.name())) {
                state.unlocked().add(current);
            } else {
                //If player hasn't unlocked it, and it's a hidden achievement, we do not send the 'real'
                //achievement but instead make a dummy 'hidden' achievement - keeps it secret but still shows
                // the player that there is something to unlock
                if (current.hidden()) {
                    Achievement hiddenAchievement = new Achievement(current.name(), "This is a secret " +
                            "achievement, play the game to unlock it", 0, "", true);
                    hiddenLocked.add(hiddenAchievement);
                } else {
                    state.locked().add(current);
                }
            }
        }
        //once we are done sorting through achievments, add any hidden locked achievements to the end of the
        // locked list to show up at the bottom.
        state.locked().addAll(hiddenLocked);
        return state;
    }
}
