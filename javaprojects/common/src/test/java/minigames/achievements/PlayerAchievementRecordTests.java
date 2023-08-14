package minigames.achievements;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Tests the conversion of player achievement record data to/from JSON
 */
public class PlayerAchievementRecordTests {

    /** Checks that an entire PlayerAchievementRecord will be accurately converted to and from a JSON string */
    @Test
    public void playerRecordFromJSON(){
        //Some random test data to fill out the record
        List<Achievement> list1 = new ArrayList<>();
        List<Achievement> list2 = new ArrayList<>();
        list1.add(new Achievement("Ach 1", "My Achievement", 0, "None", false));
        list1.add(new Achievement("Achieve 1", "My Other Achievement", 0, "None", false));
        list2.add(new Achievement("Achievement 2", "My Also another Achievement", 0, "None", false));
        list2.add(new Achievement("Ach 2", "My First Achievement", 0, "None", false));
        list2.add(new Achievement("Achieve 2", "My Second Achievement", 0, "None", false));

        List<GameAchievementState> gameList = new ArrayList<>();
        gameList.add(new GameAchievementState("Game 1", list1, list2));
        gameList.add(new GameAchievementState("Game 2", list2, list1));

        PlayerAchievementRecord playerRecord = new PlayerAchievementRecord("James", gameList);

        //convert to JSON and back. Once reconstructed, the record should have the same data in the same spots
        //as we put in above.
        String jsonString = playerRecord.toJSON();
        PlayerAchievementRecord afterJSON = PlayerAchievementRecord.fromJSON(jsonString);

        //this is the only primitive data in the player record, it should match the name we put in
        assertEquals(afterJSON.playerID(), "James");

        //these should be equal if 1 -the below conversion tests pass (individual components convert properly)
        //and 2 - the player record is properly using the component conversion methods
        assertEquals(afterJSON.gameAchievements().get(0).gameID(), "Game 1");
        assertEquals(afterJSON.gameAchievements().get(1).gameID(), "Game 2");
        assertEquals(afterJSON.gameAchievements().get(1).locked().get(1).name(), "Achieve 1");
    }

    //The below check individual JSON conversion for the classes that are fields of PlayerAchievementRecord

    /** Testing that GameAchievementState converts properly to/from json */
    @Test
    public void gameAchievementStateFromJSON(){
        List<Achievement> list1 = new ArrayList<>();
        List<Achievement> list2 = new ArrayList<>();
        list1.add(new Achievement("Ach 1", "My Achievement", 0, "None", false));
        list1.add(new Achievement("Achieve 1", "My Other Achievement", 0, "None", false));
        list2.add(new Achievement("Achievement 2", "My Also another Achievement", 0, "None", false));
        list2.add(new Achievement("Ach 2", "My First Achievement", 0, "None", false));
        list2.add(new Achievement("Achieve 2", "My Second Achievement", 0, "None", false));

        GameAchievementState state = new GameAchievementState("Test Game ABCD", list1, list2);

        String json = state.toJSON();
        GameAchievementState fromJSON = GameAchievementState.fromJSON(json);

        assertEquals(fromJSON.gameID(), "Test Game ABCD");
        assertEquals(fromJSON.unlocked().get(0).name(), "Ach 1");
        assertEquals(fromJSON.locked().get(2).name(), "Achieve 2");
    }

    /** Testing the conversion of Achievement data to/from JSON */
    @Test
    public void achievementFromJSON(){
        Achievement testAchievement = new Achievement("Tadpole", "ABCD", 0, "", true);

        String json = testAchievement.toJSON();
        Achievement fromJSON = Achievement.fromJSON(json);

        assertEquals(fromJSON.name(), "Tadpole");
        assertEquals(fromJSON.description(), "ABCD");
        assertTrue(fromJSON.hidden());
    }
}
