package minigames.achievements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Test class for setting up a fake Achievement profile to populate a UI with
public class AchievementTestData {

    public static List<String> getNames() {
        String[] players = new String[]{
                "Susan",
                "James",
                "Andrew",
                "Beatrice",
                "Lauren",
                "Matthew",
                "Patrick",
                "Noel",
                "Amy",
                "Jamie",
                "Lily",
                "Kevin",
                "Owen",
        };
        return Arrays.asList(players);
    }

    public static PlayerAchievementRecord getPlayerTestData(String player) {
        List<Achievement> list1 = new ArrayList<>();
        List<Achievement> list2 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            //adds a numbered achievement to test data. The last 2 achievements will be hidden
            list1.add(new Achievement("Achievement Number " + i,
                    "Some Generic Achievement", 0, "", (i > 7)));
            //same as for list 1, but first 2 achievements are hidden
            list2.add(new Achievement("Super Achievement! Number " + i,
                    "This is a SPECIAL achievement (not really)", 0, "", i < 2));
        }
        List<GameAchievementState> gameList = new ArrayList<>();
        gameList.add(new GameAchievementState("Game 1", list1, list2));
        gameList.add(new GameAchievementState("Game 2", list2, list1));

        PlayerAchievementRecord playerRecord = new PlayerAchievementRecord(player, gameList);
        return playerRecord;
    }
}
