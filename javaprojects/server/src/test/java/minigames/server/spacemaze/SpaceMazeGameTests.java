package minigames.server.spacemaze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Class for testing the server side game classes
 */
public class SpaceMazeGameTests {

    // Work in progress, just getting the directory/file up
    private Timer timer = new Timer();

    @DisplayName("Check timer constructor doesn't start the timing")
    @Test
    public void testTimerConstructor(){
        assertFalse(timer.getIsTimerRunning());
    }

    @DisplayName("Check startTimer starts the timer")
    @Test
    public void testTimerStarts(){
        assertFalse(timer.getIsTimerRunning());
        timer.startTimer();
        assertTrue(timer.getIsTimerRunning());
    }

    int playerX = 0;
    int playerY = 0;
    // Player class tests
    private SpacePlayer player = new SpacePlayer(playerX,playerY);

    @DisplayName("Check the player constructor")
    @Test
    public void testPlayerConstructor() {
        int[] cLoc = player.getLocation();
        int numKeys = player.checkNumberOfKeys();

        assertEquals(playerX, cLoc[0]);
        assertEquals(playerY, cLoc[1]);
        assertEquals(0, numKeys);
        
        
    }

    @DisplayName("Check player getLocation")
    @Test
    public void testPlayerGetLocation() {
        int[] cLoc = player.getLocation();

        assertEquals(playerX, cLoc[0]);
        assertEquals(playerY, cLoc[1]);

    }

    @DisplayName("Check player updateLocation")
    @Test
    public void testPlayerUpdateLocation() {
        int playerXNew = 2;
        int playerYNew = 3;

        player.updateLocation(playerXNew,playerYNew);

        int[] cLoc = player.getLocation();

        assertEquals(playerXNew, cLoc[0]);
        assertEquals(playerYNew, cLoc[1]);

    }
    /*
    *   score = initialSCore - (reductionFactor*timeTaken)
    *   reductionFactor = 50
    */
    @DisplayName("Check player calculateScore")
    @Test
    public void testPlayerCalculateScore() {
        long timeTaken = 40;
        int initialScore = 10000;

        int correctScore = (int)(initialScore - (50*40));

        assertEquals(correctScore, player.calculateScore(timeTaken, initialScore));
    }

    @DisplayName("Check player checkNumberOfKeys")
    @Test
    public void testPlayerCheckNumberOfKeys() {

        assertEquals(0, player.checkNumberOfKeys());
    }

    @DisplayName("Check player addKey")
    @Test
    public void testPlayerAddKey() {
        int currentNumKeys = player.checkNumberOfKeys();
        player.addKey();

        assertEquals((currentNumKeys+1), player.checkNumberOfKeys());
    }

}