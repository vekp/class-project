package minigames.client.achievements;

import minigames.achievements.Achievement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AchievementPresenterTest {
    static Achievement testAchievement;
    static AchievementPresenter unlockedAchievement;
    static AchievementPresenter lockedAchievement;

    @BeforeAll
    static void makeTestAchievement() {
        testAchievement  = new Achievement(
                "Test Achievement",
                "Test achievement description",
                1000,
                "",
                true
        );
        unlockedAchievement = new AchievementPresenter(testAchievement, true);
        lockedAchievement = new AchievementPresenter(testAchievement, false);
    }


    @DisplayName("Test number of components in largeAchievementPanel")
    @Test
    public void testLargeAchievementPanel() {
        JPanel testPanel = unlockedAchievement.largeAchievementPanel();
        assertEquals(5, testPanel.getComponents().length);

        testPanel = lockedAchievement.largeAchievementPanel();
        assertEquals(1, testPanel.getComponents().length);
    }

    @DisplayName("Test mouse listeners in mediumAchievementPanel")
    @Test
    public void testMediumAchievementPanel() {
        JPanel testPanel = unlockedAchievement.mediumAchievementPanel(true);
        // Test that there is 1 MouseListener
        assertEquals(1, testPanel.getMouseListeners().length);

        testPanel = lockedAchievement.mediumAchievementPanel(false);
        // Test that no MouseListeners are added
        assertEquals(0, testPanel.getMouseListeners().length);
    }


    @DisplayName("Test the text in smallAchievementPanel")
    @Test
    public void testSmallAchievementPanel() {
        JPanel testPanel = unlockedAchievement.smallAchievementPanel();
        Component c = testPanel.getComponents()[testPanel.getComponents().length - 1];
        if (c instanceof JLabel l) {
            assertEquals(l.getText(), "Test Achievement");
        }

    }
}
