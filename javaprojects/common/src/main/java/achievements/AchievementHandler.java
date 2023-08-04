package achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AchievementHandler {

    private final Map<String, Achievement> achievements = new HashMap<>();
    private final Map<String, HashSet<String>> playerUnlockList = new HashMap<>();
    private final String gameID;

    public AchievementHandler(String gameServer) {
        this.gameID = gameServer;
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
}
