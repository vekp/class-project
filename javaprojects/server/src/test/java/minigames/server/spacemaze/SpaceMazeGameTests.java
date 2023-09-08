package minigames.server.spacemaze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import java.awt.Point;

import org.junit.jupiter.api.Disabled;
import java.io.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.*;

/**
 * Class for testing the server side game classes
 */
public class SpaceMazeGameTests {

    // Timer class tests
    private GameTimer gameTimer = new GameTimer();

    /**
     * Test to check when a timer object is created that the
     * timer is not started as well.
     *
     * @author Andrew McKenzie
     */
    @DisplayName("Check timer constructor doesn't start the timing")
    @Test
    public void testTimerConstructor(){
        assertFalse(gameTimer.getIsTimerRunning());
    }

    /**
     * Test to confirm a call to startTimer actually starts the timer
     *
     * @author Andrew McKenzie
     */
    @DisplayName("Check startTimer starts the timer")
    @Test
    public void testTimerStarts(){
        assertFalse(gameTimer.getIsTimerRunning());
        gameTimer.startTimer();
        assertTrue(gameTimer.getIsTimerRunning());
        gameTimer.stopTimer();
    }

    /**
     * Test to check that a call for the total time taken
     * returns 0 while the timer is still running.
     *
     * @author Andrew McKenzie
     */
    @DisplayName("Check the final time is 0 until game is finished")
    @Test
    public void testTimeTakenWhenGameRunning() {
        gameTimer.startTimer();
        assertEquals(gameTimer.getTimeTaken(), 0);
        gameTimer.stopTimer();
    }

    // Player class tests
    Point startLocation = new Point(0,0);
    
    private SpacePlayer player = new SpacePlayer(startLocation, 5);

    @DisplayName("Check the player constructor")
    @Test
    public void testPlayerConstructor() {
        Point cLoc = player.getLocation();
        int numKeys = player.checkNumberOfKeys();

        assertEquals(startLocation.x, cLoc.x);
        assertEquals(startLocation.y, cLoc.y);
        assertEquals(0, numKeys);
        
        
    }

    @DisplayName("Check player and bot superclass - getLocation")
    @Test
    public void testPlayerGetLocation() {
        Point cLoc = player.getLocation();

        assertEquals(startLocation.x, cLoc.x);
        assertEquals(startLocation.y, cLoc.y);

    }

    @DisplayName("Check player and bot superclass - updateLocation")
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
        int initialScore = 8000;

        int correctScore = (int)(initialScore - (20*40));

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

    @DisplayName("Check player - resetKeys()")
    @Test
    public void testPlayerResetKey() {
        int currentNumKeys = player.checkNumberOfKeys();
        System.out.println("currentNumKeys: " + currentNumKeys);
        player.addKey();
        System.out.println("addKey: " + player.checkNumberOfKeys());
        player.resetKeys();
        assertEquals(currentNumKeys, player.checkNumberOfKeys());
        System.out.println("resetKeys: " + player.checkNumberOfKeys());
    }

    
    /*
     * Test MazeControl - 
     * @author Natasha Hay
     *                   
     */
    Point startLocation1 = new Point(1,0);
    private SpacePlayer player1 = new SpacePlayer(startLocation1, 5);
    private MazeControl maze1 = new MazeControl(player1);      

         
    @DisplayName("Check validMove -- invalid")
    @Test
    public void testValidMoveInvalid() 
    {        
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
    public void testValidMoveValid() 
    {
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
        System.out.println("exitStatus: " + maze1.getExitUnLockedStatus());
        maze1.bypassUnlockExit(true);
        System.out.println("exitStatus: " + maze1.getExitUnLockedStatus());
        Point moveToUnLockedExit = new Point(maze1.getExitLocation());
        // Set exit to unloced and Add assert true here
        assertTrue(maze1.validMove(moveToUnLockedExit));
    }
    
       
    @DisplayName("Check updateKeyStatus")
    @Test
    public void testUpdateKeyStatus() 
    {
        // assert key status' are false upon initalisation 
        HashMap<Point, Boolean> keyStat = maze1.getKeyStatus();
        System.out.println("keyStat: " + keyStat);
        // Level 1 keyLocs = (12, 12), (8, 3), (2, 21), (22, 3), (18,19)
        // Check that for each point, collected status == false
        keyStat.forEach((k, v) -> assertFalse(maze1.getKeyStatus().get(k)));
        // Check that a player collecting a key updates player's number of keys
        int playerCurrentKeys = player1.checkNumberOfKeys();
        System.out.println("playerCurrentKeys: " + playerCurrentKeys);
        // update keyStatus and check
        maze1.updateKeyStatus(player1, new Point(12, 12));
        assertTrue(maze1.getKeyStatus().get(new Point(12, 12)));
        int playerUpdatedKeys = player1.checkNumberOfKeys();
        System.out.println("playerUpdatedKeys: " + playerUpdatedKeys);
        assertEquals((playerCurrentKeys+1), playerUpdatedKeys);
    }

    
    @DisplayName("Check allKeyStatus")
    @Test
    public void testAllKeysStatus()
    {
        // assert allKeysStatus was initalised with false
        assertFalse(maze1.getAllKeysStatus());
        // update allKeysStatus for level 1 
        System.out.println("current keyStatus = " + maze1.getKeyStatus());
        //keyStatus =  {java.awt.Point[x=12,y=12]=false, java.awt.Point[x=8,y=3]=false, java.awt.Point[x=2,y=21]=false, java.awt.Point[x=22,y=3]=false, java.awt.Point[x=18,y=19]=false}
        // update keyStatus for level 1 and check
        maze1.updateKeyStatus(player1, new Point(12, 12));
        maze1.updateKeyStatus(player1, new Point(8, 3));
        maze1.updateKeyStatus(player1, new Point(2, 21));
        maze1.updateKeyStatus(player1, new Point(22, 3));
        maze1.updateKeyStatus(player1, new Point(18, 19));
        System.out.println("collected current keyStatus = " + maze1.getKeyStatus());
        maze1.updateAllKeysStatus(1, maze1.getKeyStatus());
        System.out.println("current allKeyStatus (all levels) = " + maze1.getAllKeysStatus());
        assertFalse(maze1.getAllKeysStatus());
        // Bypass to add all keys collected
        maze1.bypassSetAllKeysCollected();
        assertTrue(maze1.getAllKeysStatus());
    }

    
    @DisplayName("Check unlockExit")
    @Test
    public void testUnlockExit() 
    {
        maze1.bypassUnlockExit(false);  // reset exit lock
        // check lockStatus
        assertFalse(maze1.getExitUnLockedStatus());
        // Give player1 a key and unlock exit
        player1.addKey();
        maze1.unlockExit(player1);
        assertTrue(maze1.getExitUnLockedStatus());
    }

    
    @DisplayName("Check updatePlayerLocationMaze and getPlayerLocationInMaze")
    @Test
    public void testUpdatePlayerLocationMaze() 
    {
        // getPlayerLocationInMaze of player1
        // check player1 location is registered in mazeArray
        maze1.playerEntersMaze(startLocation1);
        Point playerFirstLoc = new Point(maze1.getPlayerLocationInMaze(player1));
        Point playerFirstPoint = new Point(player1.getLocation());
        System.out.println("player1 first Location: " + player1.getLocation());
        System.out.println("mazeplayer1 first Location: " + maze1.getPlayerLocationInMaze(player1));
        
        // Move player1
        maze1.updatePlayerLocationMaze(player1, new Point(5,1));
        Point playerSecondLocation = new Point(maze1.getPlayerLocationInMaze(player1));
        // getPlayerLocationInMaze of player1
        System.out.println("player1 second Location: " + player1.getLocation());
        System.out.println("mazeplayer1 second Location: " + maze1.getPlayerLocationInMaze(player1));
        // check player1 new location in mazeArray
        assertNotEquals(playerFirstLoc, playerSecondLocation);
    }

    
    @DisplayName("Check WormHole trap")
    @Test
    public void testWormHole()  
    {
        // get players current location
        System.out.println("player1 Location: " + player1.getLocation());
        // check random location is different
        Point randomPoint = new Point(maze1.randomRelocationPoint());
        System.out.println("random Location: " + randomPoint);
    }

    
    @DisplayName("Check newLevel")
    @Test
    public void testNewLevel() 
    {
        // test currentlevel return
        int prevCurrentlevel = maze1.getCurrentLevel();
        // store current mazeArray
        char[][] prevMazeArray = maze1.getMazeArray();
        // call newLevel()
        maze1.newLevel();

        // test currentlevel return
        int newCurrentLevel = maze1.getCurrentLevel();
        // store newLevel mazeArray
        char[][] newMazeArray = maze1.getMazeArray();
        // confirm previous currentLevel != new currentLevel
        assertNotEquals(prevCurrentlevel, newCurrentLevel);
        // confirm previous maze != new maze
        assertFalse(Arrays.equals(prevMazeArray, newMazeArray));
    }

    @Disabled
    @DisplayName("CheckGameOver/gameFinished") 
    @Test
    public void testCheckGameOver() 
    {
        // check gameFinished value
        

        // update player1 to have key/unlocked exit/location to exit

        // check gameFinished value
    }


    
    @DisplayName("Check MazeControl Achievement variables")
    @Test
    public void testMazeControlAchievementVariables()
    {
        // TEST LEVELALLKEYSBONUS
        // Should be initalised as false
        assertFalse(maze1.getLevelAllKeyBonusStatus());
        System.out.println("current keyStatus = " + maze1.getKeyStatus());
        System.out.println("current BonusStatus = " + maze1.getBonusStatus());
        // Assume player has collected everything
        // Bypass functions to collect keys and chects
        maze1.bypassAllKeysStatusToTrue();
        System.out.println("current keyStatus = " + maze1.getKeyStatus());
        maze1.bypassAllBonusStatusToTrue();
        System.out.println("current BonusStatus = " + maze1.getBonusStatus());
        // update keys and bonus level status - usually called by newLevel()
        maze1.updateLevelAllKeyBonusStatus();
        assertTrue(maze1.getLevelAllKeyBonusStatus());

        // ALLKEYS tested in keysStatus test above
        // ALLBONUS assumed working as uses same logic as all keys - probably a mistake

    }
    
    
    
}