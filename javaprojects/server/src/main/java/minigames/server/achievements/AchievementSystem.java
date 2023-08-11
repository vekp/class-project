package minigames.server.achievements;

import minigames.achievements.Achievement;


import java.util.*;

/** This is a service provider class that allows game servers to access an achievement handler assigned to them */
class AchievementSystem {

    //Each game server gets 1 handler, to store and track unlocks for their achievements
    final Map<String, AchievementHandler> handlers = new HashMap<>();


    //get a handler for a game server, or create one if it wasn't already
    public AchievementHandler getHandler(String handlerID) {
        if (!handlers.containsKey(handlerID)) {

        }
        return handlers.get(handlerID);
    }

    /**
     * Will return a list of unique players who have been involved in unlocking achievements
     * in at least 1 game.
     *
     * @return A sorted ArrayList containing all players that have unlocked achievements
     */
    public ArrayList<String> getPlayerList() {
        HashSet<String> uniquePlayers = new HashSet<>();
        ArrayList<String> sortedPlayers = new ArrayList<>(uniquePlayers);
        Collections.sort(sortedPlayers);
        return sortedPlayers;
    }


}
