package minigames.server.achievements;

import minigames.achievements.Achievement;

import java.util.*;

/**
 * Main interface for interacting with the system. A handler will store and track the achievement unlocks for
 * a single game server
 */
public interface AchievementHandler {

    void unlockAchievement(String playerId, String achievementID);

}
