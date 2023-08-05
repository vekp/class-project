package achievements;

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
}
