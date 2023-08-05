package minigames.client.achievementui;

import minigames.achievements.*;

import java.util.Arrays;
import java.util.Random;

//Test class for setting up a fake Achievement registry to populate a UI with
public class AchievementUITestData {

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
                handlers[i].addAchievement(new Achievement(achieveName, desc, false));

                int p1 = r.nextInt(0,players.length-1);
                int p2 = (p1 + r.nextInt(1, 4)) % players.length;
                int p3 = (p2 + r.nextInt(1, 4)) % players.length;

                int toAdd = r.nextInt(1,4);
                if(toAdd >=3) handlers[i].unlockAchievement(players[p3], achieveName);
                if(toAdd >=2) handlers[i].unlockAchievement(players[p2], achieveName);
                if(toAdd >=1) handlers[i].unlockAchievement(players[p1], achieveName);
            }
        }

        return reg;
    }
}
