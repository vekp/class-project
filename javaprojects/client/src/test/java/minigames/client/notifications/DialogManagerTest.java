package minigames.client.notifications;

import io.vertx.core.Vertx;
import minigames.client.MinigameNetworkClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DialogManagerTest {
    DialogManager testDialogManager;
    @BeforeEach
    void setUp() {
        testDialogManager = new DialogManager(new MinigameNetworkClient(mock(Vertx.class)));
    }

    @DisplayName("Test queue size accurately reflected when multiple notifications are called in succession")
    @Test
    void testQueue() {
        for (int i = 0; i < 100; i++) {
            testDialogManager.showMessageDialog("", new JLabel());
            // DialogManager should only have a max of 1 notification queued
            assertEquals(Math.min(i, 1), testDialogManager.getQueueSize());
        }
    }
}