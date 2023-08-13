package minigames.server.spacemaze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class for testing the server side game classes
 */
public class SpaceMazeGameTests {

    // Work in progress, just getting the directory/file up
    private Timer timer;

    timer = new Timer();

    @DisplayName("Check timer constructor doesn't start the timing");
    @Test
    public void testTimerConstructor(){
        assertFalse(timer.timerRunning);
    }

    @DisplayName("Check startTimer starts the timer");
    @Test
    public void testTimerStarts(){
        assertFalse(timer.timerRunning);
        timer.startTimer();
        assertTrue(timer.timerRunning);
    }
}