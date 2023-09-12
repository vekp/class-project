package minigames.server.achievements;

import minigames.achievements.*;

import java.util.*;

/**
 * This is a makeshift 'database' class to hold achievements that have been registered by Game Servers. As Game
 * Servers register their achievements every time the server starts (in code), there is technically no real need to
 * store this information in a database, but there is potential to do so, so that achievements could be registered
 * outside of code (e.g. by importing a csv file or entering data into the database manually).
 */
public class AchievementDatabase {

    /**
     * The achievements registered with the system:
     * Map:
     * Key - String: A unique game server id
     * Value - Map:
     * Key - String: Achievement ID
     * Value: The achievement data
     */
    Map<String, Map<String, Achievement>> data = new HashMap<>();


    /**
     * Attempts to add an achievement to the database. Will throw an error if a duplicate achievement is detected.
     *
     * @param gameID      The same server ID that this achievement is for
     * @param achievement the achievement to register. The name of the achievement will be its ID, and must be unique
     *                    for every achievement within the same game ID
     * @return true if the achievement was successfully added (not a duplicate), false otherwise
     */

    public boolean addAchievement(String gameID, Achievement achievement) {
        if (!data.containsKey(gameID))
            data.put(gameID, new HashMap<>());

        Map<String, Achievement> gameMap = data.get(gameID);
        if (gameMap.containsKey(achievement.name())) {
            return false;
        } else {
            gameMap.put(achievement.name(), achievement);
            return true;
        }
    }

    public Achievement getAchievement(String gameID, String achievementName) {
        Map<String, Achievement> gameMap = data.get(gameID);
        if (gameMap != null) {
            return gameMap.get(achievementName);
        }
        return null;
    }

    /**
     * Get a list of achievements associated with a game server
     *
     * @param gameID The ID for the game server we want
     * @return list of achievements for that game
     */
    public List<Achievement> getAchievementsByGame(String gameID) {
        List<Achievement> result = new ArrayList<>();
        if (data.containsKey(gameID)) {
            Map<String, Achievement> gameMap = data.get(gameID);

            for (Achievement value : gameMap.values()) {
                result.add(value);
            }
        }
        return result;
    }
}
