package minigames.achievements;

import java.util.*;

public class AchievementRegister {

    //Each game server gets 1 handler, to store and track unlocks for their achievements
    final Map<String, AchievementHandler> handlers = new HashMap<>();

    //get or create a handler for a game server.
    public AchievementHandler getHandler(String gameID) {
        if (!handlers.containsKey(gameID)) {
            handlers.put(gameID, new AchievementHandler(gameID));
        }
        return handlers.get(gameID);
    }

    public ArrayList<AchievementHandler> getAllHandlers() {
        ArrayList<AchievementHandler> result = new ArrayList<>();
        for (String id : handlers.keySet()) {
            result.add(handlers.get(id));
        }
        return result;
    }

    /**
     * Will return a list of unique players who have been involved in unlocking achievements
     * in at least 1 game.
     *
     * @return
     */
    public ArrayList<String> getPlayerList() {
        HashSet<String> uniquePlayers = new HashSet<>();
        for (AchievementHandler handler : handlers.values()) {
            for (String player : handler.getPlayers()) {
                uniquePlayers.add(player);
            }
        }
        ArrayList<String> sortedPlayers = new ArrayList<>(uniquePlayers);
        Collections.sort(sortedPlayers);
        return sortedPlayers;
    }


    /**
     *
     * @param playerID String of playerID
     * @param gameID String of gameID
     * @param unlocked bool representing whether unlocked or locked achievements are to be returned
     * @return ArrayList of the given player's achievements for the given game.
     */
    public ArrayList<Achievement> getUserAchievements(String playerID, String gameID, boolean unlocked) {
        AchievementHandler handler = handlers.get(gameID);
        ArrayList<Achievement> allAchievements = handler.getAllAchievements();
        HashSet<String> playerUnlockList = handler.getPlayerUnlockList(playerID);
        ArrayList<Achievement> unlockedAchievements = new ArrayList<>();
        for (String achievementID : playerUnlockList) {
            Achievement achievement = handler.getAchievementFromID(achievementID);
            unlockedAchievements.add(achievement);
        }
        if (unlocked) return unlockedAchievements;
        allAchievements.removeAll(unlockedAchievements);
        return allAchievements;
    }
}
