package minigames.server.achievements;

import minigames.achievements.*;
import java.util.*;
public class AchievementDatabase {

    /**
     * The achievements registered with the system:
     * Map:
     *  Key - String: A unique game server id
     *  Value - Map:
     *          Key - String: Achievement ID
     *          Value: The achievement data
     */
    Map<String, Map<String, Achievement>> data = new HashMap<>();


    public void addAchievement(String gameID, String achievementID, Achievement achievement){

    }

    public Achievement getAchievement(String gameID, String achievementID){
        return null;
    }
}
