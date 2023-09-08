package minigames.server.achievements;

import minigames.achievements.Achievement;

import minigames.server.GameServer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure achievements can be unlocked on a per-player, per game basis
 */
public class AchievementTests {

    /** Simple setup to get us a handler that has 1 achievement registered already. Registered achievements are static
     * and grouped by class ID so any further handlers we make will still have access to this achievement
     */
    @BeforeAll
    public static void setup(){
        Achievement a = new Achievement("Achievement 1", "Description", 0, "someMedia", false);
        AchievementHandler handler = new AchievementHandler(GameServer.class);
        handler.registerAchievement(a);
    }

    /**
     * Checks that an achievement handler will error if we try to add duplicate achievements (e.g. ones with the
     * same name. This also serves to test that achievements created with the same class type point to the same
     * database entries (as we already registered an "Achievement 1" with a different handler in setup)
     */
    @Test
    public void handlerRejectsDuplicateAchievement() {
        //achievements are considered the same if they share the same ID (name). Achievement names should be unique
        Achievement b = new Achievement("Achievement 1", "A different description", 0, "blah", true);
        Achievement c = new Achievement("Different Achievement", "A description", 0, "", true);

        AchievementHandler handler = new AchievementHandler(GameServer.class);

        //check that registering a different instance, but with the same name, throws an error
        assertThrows(IllegalArgumentException.class, ()->handler.registerAchievement(b));
        //it should allow achievements with different names
        assertDoesNotThrow(()->handler.registerAchievement(c));
    }

    /** Checks that an achievement will error if we try to unlock a nonexistent achievement */
    @Test
    public void handlerNonexistentAchievementUnlock(){
        Achievement b = new Achievement("Test 2", "A different description", 0, "blah", true);
        AchievementHandler handler = new AchievementHandler(GameServer.class);
        //we did not add this achievement so it should fail to unlock
        assertThrows(IllegalArgumentException.class, ()-> handler.unlockAchievement("Player1", b.name()));
        //we already registered Achievement 1 in setup, this unlock request should be valid
        assertDoesNotThrow(()-> handler.unlockAchievement("Player1", "Achievement 1"));
    }

    /** checks a player profile will register an achievement and recognise that it has it */
    @Test
    public void playerProfileAchievementRegisters(){
        PlayerAchievementProfile profile = new PlayerAchievementProfile("James");
        profile.addAchievement("Test Game", "Test Achievement");
        assertTrue(profile.hasEarnedAchievement("Test Game", "Test Achievement"));
    }

    /** tests that player profile manager cant add duplicate players. This is mostly testing the hashcode and equals
     * overrides for player profile work correctly
     */
    @Test
    public void playerRegisterRejectsDuplicates(){
        PlayerAchievementProfileManager manager = new PlayerAchievementProfileManager();
        manager.addPlayer("Amy");
        assertFalse(manager.addPlayer("Amy"));
    }
}
