package achievements;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure achievements can be unlocked on a per-player, per game basis
 */
public class AchievementTests {

    @Test
    public void TestActive() {
        assertTrue(true);
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


}
