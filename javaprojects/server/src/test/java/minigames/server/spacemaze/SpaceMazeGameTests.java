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
        Point cLoc = player.getLocation();
        int numKeys = player.checkNumberOfKeys();

        assertEquals(startLocation.x, cLoc.x);
        assertEquals(startLocation.y, cLoc.y);
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
        Point newLocation = new Point(2,3);

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
     * Test MazeControl - 
     *                   
     */
    //Point startLocation1 = new Point(1,0);
    private SpacePlayer player1 = new SpacePlayer(startLocation);
    private MazeControl maze1 = new MazeControl();      

     
    @DisplayName("Check validMove -- invalid")
    @Test
    public void testValidMoveInvalid() {
        
        // Set player location and some invalid moveTos
        //Point playerLocation = new Point(1, 0);
        //Point moveToNegX = new Point(-1, 10);     // throw ArrayOutOfBoundsException
        //Point moveToNegY = new Point(10, -10);
        Point moveToWallA = new Point(0, 5);
        Point moveToWallB = new Point(24, 2);
        Point moveToWallC = new Point(5, 2);
        Point moveToLockedExit = new Point(24, 23);
        // Assert validMove() returns false for invalid locations - wall or negative location
        //assertFalse(maze1.validMove(moveToNegX));
        //assertFalse(maze1.validMove(moveToNegY));
        assertFalse(maze1.validMove(moveToWallA));
        assertFalse(maze1.validMove(moveToWallB));
        assertFalse(maze1.validMove(moveToWallC));
        // Assert validMove() returns false for moving to a locked exit
        assertFalse(maze1.validMove(moveToLockedExit));
    }

    @DisplayName("Check validMove -- valid")
    @Test
    public void testValidMoveValid() {
        // Set player location to start and set some valid moveTos
        //Point playerLocation = new Point(1, 0);
        Point moveToA = new Point(1, 1);
        Point moveToB = new Point(9, 1);
        Point moveToC = new Point(9, 8);
        // Assert validMove() returns true for valid (non-wall) locations
        assertTrue(maze1.validMove(moveToA));
        assertTrue(maze1.validMove(moveToB));
        assertTrue(maze1.validMove(moveToC));
        // Assert validMove() returns true for moving to an unlocked exit 
        maze1.bypassUnlockExit(true);
        Point moveToUnLockedExit = new Point(24, 23);
        // Set exit to unloced and Add assert true here
        assertTrue(maze1.validMove(moveToUnLockedExit));
    }
    
       
    @DisplayName("Check updateKeyStatus")
    @Test
    public void testUpdateKeyStatus() {
        // assert key status' are false upon initalisation 
        // keyLocs = [new Point(17, 5), new Point(7, 17)];
        
        assertFalse(maze1.getKeyStatus().get(new Point(17, 5)));
        assertFalse(maze1.getKeyStatus().get(new Point(7, 17)));
        int playerCurrentKeys = player1.checkNumberOfKeys();
        //System.out.print("playerCurrentKeys: " + playerCurrentKeys);
        // update keyStatus and check
        maze1.updateKeyStatus(player1, new Point(17, 5));
        assertTrue(maze1.getKeyStatus().get(new Point(17, 5)));
        int playerUpdatedKeys = player1.checkNumberOfKeys();
        assertEquals((playerCurrentKeys+1), playerUpdatedKeys);
    }

    
    @DisplayName("Check unlockExit")
    @Test
    public void testUnlockExit() {
        maze1.bypassUnlockExit(false);  // reset exit lock
        // check lockStatus
        assertFalse(maze1.getExitUnLockedStatus());
        // Give player1 a key and unlock exit
        player1.addKey();
        maze1.unlockExit(player1);
        assertTrue(maze1.getExitUnLockedStatus());
    }

    @Disabled
    @DisplayName("Check updatePlayerLocationMaze and getPlayerLocationInMaze")
    @Test
    public void testUpdatePlayerLocationMaze() {
        // getPlayerLocationInMaze of player1

        // check player1 location is registered in mazeArray

        // Move player1

        // getPlayerLocationInMaze of player1

        // check player1 new location in mazeArray

    }

    @Disabled
    @DisplayName("CheckGameOver/gameFinished") {
        // check gameFinished value

        // update player1 to have key/unlocked exit/location to exit

        // check gameFinished value
    }
}