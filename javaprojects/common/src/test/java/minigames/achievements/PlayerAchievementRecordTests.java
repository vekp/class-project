package minigames.achievements;

import java.util.*;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Tests the conversion of player achievement record data to/from JSON
 */
public class PlayerAchievementRecordTests {

    @Test
    public void constructsPackageFromJSON(){
        List<Achievement> list1 = new ArrayList<>();
        List<Achievement> list2 = new ArrayList<>();
        list1.add(new Achievement("Ach 1", "My Achievement", "None", false));
        list1.add(new Achievement("Achieve 1", "My Other Achievement", "None", false));
        list2.add(new Achievement("Achievement 2", "My Also another Achievement", "None", false));
        list2.add(new Achievement("Ach 2", "My First Achievement", "None", false));
        list2.add(new Achievement("Achieve 2", "My Second Achievement", "None", false));

        List<GameAchievementState> gameList = new ArrayList<>();
        gameList.add(new GameAchievementState("Game 1", list1, list2));
        gameList.add(new GameAchievementState("Game 2", list2, list1));

        PlayerAchievementRecord playerRecord = new PlayerAchievementRecord("James", gameList);
        String jsonString = playerRecord.toJSON();

        PlayerAchievementRecord afterJSON = PlayerAchievementRecord.fromJSON(jsonString);

        assertEquals(afterJSON.playerID(), "James");
        assertEquals(afterJSON.gameAchievements().get(0).gameID(), "Game 1");
        assertEquals(afterJSON.gameAchievements().get(1).gameID(), "Game 2");
        //2nd locked achievement for the 2nd game should be 'Achieve 1'
        assertEquals(afterJSON.gameAchievements().get(1).locked().get(1).name(), "Achieve 1");
    }
}
