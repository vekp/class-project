package minigames.server.spacemaze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import java.awt.Point;

import org.junit.jupiter.api.Disabled;
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

    /*
     * Test MazeControl
     */

    private MazeControl Maze1 = new MazeControl();
    @Disabled
    @DisplayName("Check validMove -- invalid")
    @Test
    public void testValidMoveInvalid() {
        // Set player location and some invalid moveTos
        //Point playerLocation = new Point(1, 0);
        Point moveToNegX = new Point(-1, 10);
        Point moveToNegY = new Point(10, -10);
        Point moveToWallA = new Point(0, 5);
        Point moveToWallB = new Point(5, 2);
        Point moveToLockedExit = new Point(24, 23);
        // Assert validMove() returns false for invalid locations - wall or negative location
        assertFalse(Maze1.validMove(moveToNegX));
        assertFalse(Maze1.validMove(moveToNegY));
        assertFalse(Maze1.validMove(moveToWallA));
        assertFalse(Maze1.validMove(moveToWallB));
        // Assert validMove() returns false for moving to a locked exit
        assertFalse(Maze1.validMove(moveToLockedExit));
    }

    @Disabled
    @DisplayName("Check validMove -- valid")
    @Test
    public void testValidMoveValid() {
        // Set player location to start and set some valid moveTos
        //Point playerLocation = new Point(1, 0);
        Point moveToA = new Point(1, 1);
        Point moveToB = new Point(9, 1);
        Point moveToC = new Point(9, 8);
        // Assert validMove() returns true for valid (non-wall) locations
        assertTrue(Maze1.validMove(moveToA));
        assertTrue(Maze1.validMove(moveToB));
        assertTrue(Maze1.validMove(moveToC));
        // Assert validMove() returns true for moving to an unlocked exit 
        // Point moveToUnLockedExit = new Point(24, 23);
        // Set exit to unloced and Add assert true here
    }
}