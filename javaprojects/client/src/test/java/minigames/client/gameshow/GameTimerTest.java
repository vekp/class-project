package minigames.client.gameshow;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import static org.mockito.Mockito.*;

public class GameTimerTest {

    @Mock
    private Font pixelFont;

    @Mock
    private GameShowUI gameShowUI;

    @Mock
    private Graphics graphicsMock;

    @Mock
    private Timer timerMock;
    private GameTimer gameTimer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameShowUI.pixelFont = mock(Font.class);
        gameTimer = new GameTimer(60000); // 60 seconds
    }

    @Test
    public void testStart() {
        gameTimer.timer = timerMock;

        gameTimer.start();

        assertTrue(gameTimer.isRunning);
        verify(timerMock).start();
    }

    @Test
    public void testStop() {
        gameTimer.timer = timerMock;
        gameTimer.isRunning = true;

        gameTimer.stop();

        assertFalse(gameTimer.isRunning);
        verify(timerMock).stop();
    }

    @Test
    public void testFormatTime() {
        String formattedTime = gameTimer.formatTime(60000); // 60 seconds

        assertEquals("01:00", formattedTime);
    }

    @Test
    public void testGetCurrentTime() {
        gameTimer.startTime = System.currentTimeMillis() - 30000; // 30 seconds ago
        gameTimer.isRunning = true;

        long currentTime = gameTimer.getCurrentTime();

        assertTrue(currentTime >= 30000 && currentTime < 60000); // Should be between 30 and 60 seconds
    }

    @Test
    public void testGetRemainingTime() {
        gameTimer.startTime = System.currentTimeMillis() - 30000; // 30 seconds ago
        gameTimer.isRunning = true;

        long remainingTime = gameTimer.getRemainingTime();

        assertTrue(remainingTime >= 30000 && remainingTime < 60000); // Should be between 30 and 60 seconds
    }

    @Test
    public void testGetTimeLimit() {
        long timeLimit = gameTimer.getTimeLimit();

        assertEquals(60000, timeLimit); // Time limit should be 60 seconds
    }

    @Test
    public void testCalculateScore() {
        gameTimer.startTime = System.currentTimeMillis() - 30000; // 30 seconds ago
        gameTimer.isRunning = true;

        int score = gameTimer.calculateScore();

        assertEquals(30, score); // Score should be 30 (30 seconds)
    }
}
