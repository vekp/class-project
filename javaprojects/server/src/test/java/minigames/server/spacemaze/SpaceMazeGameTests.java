package minigames.server.spacemaze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import java.awt.Point;

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

    
    // Player class tests
    Point startLocation = new Point(0,0);
    private SpacePlayer player = new SpacePlayer(startLocation);

    @DisplayName("Check the player constructor")
    @Test
    public void testPlayerConstructor() {
        int[] cLoc = player.getLocation();
        int numKeys = player.checkNumberOfKeys();

        assertEquals(startLocation.x, cLoc[0]);
        assertEquals(startLocation.y, cLoc[1]);
        assertEquals(0, numKeys);
        
        
    }

    @DisplayName("Check player getLocation")
    @Test
    public void testPlayerGetLocation() {
        Point cLoc = player.getLocation();

        assertEquals(startLocation.x, cLoc.x);
        assertEquals(startLocation.y, cLoc.y);

    }

    @DisplayName("Check player updateLocation")
    @Test
    public void testPlayerUpdateLocation() {
        Point newLocation = Point(2,3);

        player.updateLocation(newLocation);

        Point cLoc = player.getLocation();

        assertEquals(newLocation.x, cLoc.x);
        assertEquals(newLocation.y, cLoc.y);

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