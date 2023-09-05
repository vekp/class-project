package minigames.client.achievements;

import io.vertx.core.Vertx;
import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.notifications.NotificationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        JPanel panel = apRegistry.achievementListPanel(null);
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
            System.out.println(i);
            JPanel carousel = apRegistry.achievementCarousel(i);
            // For unlocked achievements - 4 components
            if (i < 5) {
                assertEquals(4, carousel.getComponents().length);
                // the position JLabel should contain its index + 1
                for (Component c : carousel.getComponents())
                    if (c instanceof JLabel label) assert label.getText().contains("achievement " + (i + 1));
            }
        }

    }
}
