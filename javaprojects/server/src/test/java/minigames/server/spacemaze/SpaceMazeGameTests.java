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

/**
 * Class for testing the server side game classes
 */
public class SpaceMazeGameTests {

    // Timer class tests
    private GameTimer gameTimer = new GameTimer();

    @DisplayName("Check timer constructor doesn't start the timing")
    @Test
    public void testTimerConstructor(){
        assertFalse(gameTimer.getIsTimerRunning());
    }

    @DisplayName("Check startTimer starts the timer")
    @Test
    public void testTimerStarts(){
        assertFalse(gameTimer.getIsTimerRunning());
        gameTimer.startTimer();
        assertTrue(gameTimer.getIsTimerRunning());
    }
    Point startLocation = new Point(0,0);
    // Bot class tests
    private SpaceBot bot = new SpaceBot(startLocation);
    @DisplayName("Check the bot constructor")
    @Test
    public void testBotConstructor() {
        Point cLoc = bot.getLocation();
        
        assertEquals(startLocation.x, cLoc.x);
        assertEquals(startLocation.y, cLoc.y);
        assertEquals('.', bot.getTile());

    }

    @DisplayName("Check the bot movement up")
    @Test
    public void testBotMoveUp() {
        Point cLoc = bot.getLocation(); 
        Point moveUp = bot.getMoveAttempt(0);
        
        assertEquals(cLoc.x, moveUp.x);
        assertEquals((cLoc.y-1), moveUp.y);
        
    }

    @DisplayName("Check the bot movement right")
    @Test
    public void testBotMoveRight() {
        Point cLoc = bot.getLocation();
        Point moveRight = bot.getMoveAttempt(1);
       
        assertEquals((cLoc.x+1), moveRight.x);
        assertEquals(cLoc.y, moveRight.y);

    }

    @DisplayName("Check the bot movement down")
    @Test
    public void testBotMoveDown() {
        Point cLoc = bot.getLocation();
        Point moveDown = bot.getMoveAttempt(2);
       
        assertEquals(cLoc.x, moveDown.x);
        assertEquals((cLoc.y+1), moveDown.y);

    }

    @DisplayName("Check the bot movement left")
    @Test
    public void testBotMoveLeft() {
        Point cLoc = bot.getLocation();
        Point moveLeft = bot.getMoveAttempt(3);
       
        assertEquals((cLoc.x-1), moveLeft.x);
        assertEquals(cLoc.y, moveLeft.y);

    }

    @DisplayName("Check for legal move commands")
    @Test
    public void testBotMoveCommand() {

        // up, right, down, left
        assertDoesNotThrow(() -> { bot.getMoveAttempt(0); });
        assertDoesNotThrow(() -> { bot.getMoveAttempt(1); });
        assertDoesNotThrow(() -> { bot.getMoveAttempt(2); });
        assertDoesNotThrow(() -> { bot.getMoveAttempt(3); });

    }

    @DisplayName("Check for illegal move commands")
    @Test
    public void testIllegalBotMoveCommand() {

       // a command not between 0 - 3 inclusive
       assertThrows(RuntimeException.class, () -> { bot.getMoveAttempt(4); });
    }

    @DisplayName("Check setting of occupied tile")
    @Test
    public void testBotUpdateTile() {
        char testChar = 'K';
       // a command not between 0 - 3 inclusive
       char currChar = bot.getTile();
       bot.updateTile(testChar);
       assertEquals(bot.getTile(), testChar);
    }




    // Player class tests
    
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
        System.out.println("exitStatus: " + maze1.getExitUnLockedStatus());
        maze1.bypassUnlockExit(true);
        System.out.println("exitStatus: " + maze1.getExitUnLockedStatus());
        Point moveToUnLockedExit = new Point(maze1.getExitLocation());
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
        System.out.println("playerCurrentKeys: " + playerCurrentKeys);
        //System.out.print("playerCurrentKeys: " + playerCurrentKeys);
        // update keyStatus and check
        maze1.updateKeyStatus(player1, new Point(17, 5));
        assertTrue(maze1.getKeyStatus().get(new Point(17, 5)));
        int playerUpdatedKeys = player1.checkNumberOfKeys();
        System.out.println("playerUpdatedKeys: " + playerUpdatedKeys);
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
    @DisplayName("CheckGameOver/gameFinished") 
    @Test
    public void testCheckGameOver() {
        // check gameFinished value

        // update player1 to have key/unlocked exit/location to exit

        // check gameFinished value
    }

    /*
     * Test MazeInit class
     */

    private MazeInit mazeSetup = new MazeInit(1);
    
    

    
    @DisplayName("test MazeInit - mazeArray creation")
    @Test
    public void testMazeInit() {
        // Compare MazeInit mazeArray to MazeControl mazeArray
        assertArrayEquals(mazeSetup.getMazeInitArray(), maze1.getMazeArray());
    }
}