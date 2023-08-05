package minigames.achievements;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure achievements can be unlocked on a per-player, per game basis
 */
public class AchievementTests {

    @Test
    public void TestActive() {
        assertTrue(true);
    }

    //make sure that requesting a handler always returns one, and that it will be placed in the entire
    //registry of handlers (if not already there)
    @Test
    public void RegisterAddsNewHandler() {
        AchievementRegister register = new AchievementRegister();
        AchievementHandler handler = register.getHandler("MyTest");
        ArrayList<AchievementHandler> allHandlers = register.getAllHandlers();

        //we asked for a handler from a new register, so it should have made/returned something valid
        assertNotNull(handler);
        //this returned handler should also now exist in the register's 'all handler' list
        assertTrue(allHandlers.contains(handler));
    }

    //checks that we can unlock and achievement when given a player ID and an achievement ID that matches
    //one found in that game
    @Test
    public void AchievementUnlocked() {
        AchievementHandler handler = new AchievementHandler("TestGame");

        Achievement test = new Achievement("FirstAchievement", "", false);
        handler.addAchievement(test);

        handler.unlockAchievement("Player1", "FirstAchievement");
        assertTrue(handler.hasPlayerEarnedAchievement("Player1", "FirstAchievement"));
    }

    //checks that we do not unlock achievements for invalid entries
    @Test
    public void AchievementRefused() {
        AchievementHandler handler = new AchievementHandler("TestGame");

        Achievement test = new Achievement("FirstAchievement", "", false);
        handler.addAchievement(test);

        handler.unlockAchievement("Player1", "FirstAchievement");
        assertFalse(handler.hasPlayerEarnedAchievement("Player1", "SecondAchievement"));
    }

    @Test
    public void PlayerListObtainedSorted() {
        AchievementRegister register = new AchievementRegister();
        AchievementHandler handlerA = register.getHandler("HandlerA");
        AchievementHandler handlerB = register.getHandler("HandlerB");
        handlerA.addAchievement(new Achievement("TestA1", "", false));
        handlerA.addAchievement(new Achievement("TestA2", "", false));
        handlerA.addAchievement(new Achievement("TestA3", "", false));
        handlerB.addAchievement(new Achievement("TestB1", "", false));
        handlerB.addAchievement(new Achievement("TestB2", "", false));

        handlerA.unlockAchievement("Albert", "TestA1");
        handlerA.unlockAchievement("Zebra", "TestA3");
        handlerA.unlockAchievement("Albert", "TestA2");
        handlerB.unlockAchievement("Fred", "TestB2");
        handlerB.unlockAchievement("Achew", "TestB1");

        ArrayList<String> allPlayers = register.getPlayerList();
        //there was a duplicate name, it should not be included
        assertEquals(4, allPlayers.size());
        //The names should be alphabetical
        assertEquals("Achew", allPlayers.get(0));
        assertEquals("Albert", allPlayers.get(1));
        assertEquals("Fred", allPlayers.get(2));
        assertEquals("Zebra", allPlayers.get(3));
    }

}
