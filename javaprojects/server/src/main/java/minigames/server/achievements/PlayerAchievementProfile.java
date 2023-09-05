package minigames.server.achievements;

import java.util.*;

public class PlayerAchievementProfile {
    private final String name;
    private final Map<String, HashSet<String>> achievements = new HashMap<>();

    public PlayerAchievementProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAchievement(String gameID, String achievementID) {
        if (!achievements.containsKey(gameID))
            achievements.put(gameID, new HashSet<>());
        achievements.get(gameID).add(achievementID);
    }

    public boolean hasEarnedAchievement(String gameID, String achievementID) {
        return achievements.containsKey(gameID) && achievements.get(gameID).contains(achievementID);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PlayerAchievementProfile p) && p.name.equals(name);
    }
}
