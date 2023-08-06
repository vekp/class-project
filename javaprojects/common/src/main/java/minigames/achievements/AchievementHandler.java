package minigames.achievements;

import java.util.*;

public class AchievementHandler {

    private final Map<String, Achievement> achievements = new HashMap<>();
    private final Map<String, HashSet<String>> playerUnlockList = new HashMap<>();
    private final String gameID;

    public AchievementHandler(String gameServer) {
        this.gameID = gameServer;
    }

    public String getGameID() {
        return gameID;
    }

    public HashSet<String> getPlayers(){
        return new HashSet<>(playerUnlockList.keySet());
    }

    public ArrayList<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }

    public void addAchievement(Achievement a) {
        String key = a.name();
        if (!achievements.containsKey(key))
            achievements.put(key, a);
    }

    public void addAchievementMultiple(ArrayList<Achievement> achievementList) {
        for (Achievement achievement : achievementList) {
            addAchievement(achievement);
        }
    }

    public void unlockAchievement(String playerID, String achievementID) {
        if (!playerUnlockList.containsKey(playerID))
            playerUnlockList.put(playerID, new HashSet<>());

        //cannot unlock achievements this game doesn't have
        if (achievements.containsKey(achievementID)) {
            playerUnlockList.get(playerID).add(achievementID);
        }
    }

    public boolean hasPlayerEarnedAchievement(String playerID, String achievementID) {
        if (playerUnlockList.containsKey(playerID))
            return playerUnlockList.get(playerID).contains(achievementID);
        else return false;
    }

    /**
     *
     * @param playerID String representing player's ID
     * @return the set of unlocked achievements for the given playerID
     */
    public HashSet<String> getPlayerUnlockList (String playerID) {
        if(playerUnlockList.containsKey(playerID))
            return new HashSet<>(playerUnlockList.get(playerID));

        return new HashSet<>();
    }

    /**
     *
     * @param achievementID String representation of achievement
     * @return the associated Achievement
     */
    public Achievement getAchievementFromID (String achievementID) {
        if (achievements.containsKey(achievementID)) return achievements.get(achievementID);
        return null;
    }
}
