package minigames.server.achievements;

import java.util.Set;
import minigames.achievements.*;


/** Implemented by any game server that wishes to be involved in the achievement system.
 * Achievements will be registered at server start up (just after all game servers are registered). This is so that
 * all achievement data is available for users to view in the achievement menu.
 */
public interface AchievementProvider {
    //This will be called to ask the game server to provide the achievements it wishes to include in the system.
    Set<Achievement> registerAchievements();
}
