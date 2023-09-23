package minigames.client.achievements;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
import minigames.client.Animator;
import minigames.client.notifications.DialogManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class AchievementPresenterRegistryTest {
    static AchievementPresenterRegistry apRegistry;

    @BeforeAll
    public static void makeTestAPRegistry() {
        List<Achievement> unlocked = new ArrayList<>();
        List<Achievement> locked = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // Create a new achievement.  Even numbered ones are hidden.
            Achievement a = new Achievement(
                    "Achievement " + i,
                    "This is achievement " + i + " of the game",
                    0,
                    "",
                    i % 2 == 0
            );
            // Add the first 5 to unlocked, the rest to locked.
            if (i < 5) unlocked.add(a);
            else locked.add(a);
        }
        GameAchievementState gaState = new GameAchievementState("Test Game ID", unlocked, locked);
        apRegistry = new AchievementPresenterRegistry(gaState, new Animator());

    }

    @DisplayName("Test achievementListPanel")
    @Test
    public void constructorTest() {
        JPanel panel = apRegistry.achievementListPanel(mock(DialogManager.class));
        for (int i = 0; i < panel.getComponents().length; i++) {
            Component component = panel.getComponent(i);
            // For unlocked, expect 1 MouseListener.
            if (i < 5) assertEquals(1, component.getMouseListeners().length);
            // If locked, no MouseListeners should be present
            else assertEquals(0, component.getMouseListeners().length);
        }
    }

    @DisplayName("Test carousel")
    @Test
    public void carouselTest() {
        // No images
        for (int i = 0; i < 10; i++) {
            JPanel carousel = apRegistry.achievementCarousel.achievementCarouselPanel(i);
            // For unlocked achievements - 4 components
            if (i < 5) {
                assertEquals(4, carousel.getComponents().length);
                // the position JLabel should contain its index + 1
                for (Component c : carousel.getComponents())
                    assert !(c instanceof JLabel label) || label.getText().contains("achievement " + (i + 1));
            }
        }

    }
}
