package minigames.achievements;

import minigames.achievements.*;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//Test class for setting up a fake Achievement registry to populate a UI with
public class AchievementUITestData {

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
                    "Some Generic Achievement", "", (i > 7)));
            //same as for list 1, but first 2 achievements are hidden
            list2.add(new Achievement("Super Achievement! Number " + i,
                    "This is a SPECIAL achievement (not really)", "", i < 2));
        }
        List<GameAchievementState> gameList = new ArrayList<>();
        gameList.add(new GameAchievementState("Game 1", list1, list2));
        gameList.add(new GameAchievementState("Game 2", list2, list1));

        PlayerAchievementRecord playerRecord = new PlayerAchievementRecord(player, gameList);
        return playerRecord;
    }

    public static AchievementRegister getTestData() {
        AchievementRegister reg = new AchievementRegister();
        Random r = new Random();
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

        AchievementHandler[] handlers = new AchievementHandler[8];
        for (int i = 0; i < handlers.length; i++) {
            String name = "Game " + i;
            handlers[i] = reg.getHandler(name);

            int count = r.nextInt(3, 12);
            for (int k = 0; k < count; k++) {
                String achieveName = name + ": Achievement - " + k;
                String desc = "This is achievement number " + k + " for the game!";
                handlers[i].addAchievement(new Achievement(achieveName, desc, "DeFa Lt", false));

                int p1 = r.nextInt(0, players.length - 1);
                int p2 = (p1 + r.nextInt(1, 4)) % players.length;
                int p3 = (p2 + r.nextInt(1, 4)) % players.length;

                int toAdd = r.nextInt(1, 4);
                if (toAdd >= 3) handlers[i].unlockAchievement(players[p3], achieveName);
                if (toAdd >= 2) handlers[i].unlockAchievement(players[p2], achieveName);
                if (toAdd >= 1) handlers[i].unlockAchievement(players[p1], achieveName);
            }
        }

        return reg;
    }
}
