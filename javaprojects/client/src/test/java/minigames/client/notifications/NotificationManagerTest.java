package minigames.client.notifications;

import io.vertx.core.Vertx;
import minigames.client.MinigameNetworkClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.*;

class NotificationManagerTest {
    NotificationManager testNotificationManager;
    @BeforeEach
    void setUp() {
         testNotificationManager = new NotificationManager(new MinigameNetworkClient(mock(Vertx.class)));
    }

    @DisplayName("Test that setAlignmentX, setAlignmentY and setAnimationSpeed accept arguments in range 0.0f-1.0f and throw IllegalArgumentException outside range")
    @Test
    void testFloatParamSetters() {
        // Assert error thrown for illegal arg
        float[] illegalArgs = {1.1f, 5.3f, 220, -1, -0.00001f, 1.00001f};
        for (float arg : illegalArgs) {
            assertThrows(IllegalArgumentException.class, () -> {
                testNotificationManager.setAlignmentX(arg);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                testNotificationManager.setAlignmentY(arg);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                testNotificationManager.setAnimationSpeed(arg);
            });
        }
        // Assert no error for legal arg
        float[] legalArgs = {0, 1, 0.00001f, 0.220f, 0.5f, 0.72345f, 0.999999f};
        for (float arg : legalArgs) {
            assertDoesNotThrow(() -> {
                testNotificationManager.setAlignmentX(arg);
                testNotificationManager.setAlignmentY(arg);
                testNotificationManager.setAnimationSpeed(arg);
            });
        }
    }

    @DisplayName("Test queue size accurately reflected when multiple notifications are called in succession")
    @Test
    void testQueue() {
        for (int i = 0; i < 100; i++) {
            testNotificationManager.showNotification(new JLabel());
            assertEquals(i, testNotificationManager.getQueueSize());
        }
    }

}