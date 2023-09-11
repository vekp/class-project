package minigames.client.spacemaze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import java.awt.Point;
import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import java.io.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

public class SpaceMazeClientTests {

    private MazeDisplay maze;

    /**
     * Setting up a maze display for testing logic
     */
    @BeforeEach
    public void dummyMazeArray(){
        SpaceMaze spaceMaze = new SpaceMaze();
        ArrayList<SpaceBot> bots = new ArrayList<>();
        char [][] mazeMap = {
                {'S','.', 'U'},
                {'K', 'B','?'},
                {'M', '$', 'T'},
                {'H', 'E', 'W'},
                {'P', '.', '.'},
        };
        maze = new MazeDisplay(mazeMap, spaceMaze, bots);
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
        //assertEquals('.', bot.getTile());

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
    /*
    @DisplayName("Check setting of occupied tile")
    @Test
    public void testBotUpdateTile() {
        char testChar = 'K';
       // a command not between 0 - 3 inclusive
       char currChar = bot.getTile();
       bot.updateTile(testChar);
       assertEquals(bot.getTile(), testChar);
    }
    */

    /**
     * Test to check if moving on to a locked exit or wall is
     * returned as false
     *
     * @author Andrew McKenzie
     */
    @Test
    @DisplayName("Testing in valid move requests are false")
    public void testInvalidMoves() {
        // Locked Exit
        assertFalse(maze.isMoveValid(new Point(1, 3)));
        // Wall
        assertFalse(maze.isMoveValid(new Point(2, 3)));
        // Out of bounds in all four directions
        assertFalse(maze.isMoveValid(new Point(0, -1)));
        assertFalse(maze.isMoveValid(new Point(-1, 0)));
        assertFalse(maze.isMoveValid(new Point(1, 5)));
        assertFalse(maze.isMoveValid(new Point(3, 0)));
    }

    /**
     * Test to check if moving onto a valid tile returns true
     *
     * @author Andrew McKenzie
     */
    @Test
    @DisplayName("Testing valid moves are true")
    public void testValidMovesAreValid() {
        // Start tile
        assertTrue(maze.isMoveValid(new Point(0, 0)));
        // Path tile
        assertTrue(maze.isMoveValid(new Point(1, 0)));
        // Unlocked Exit tile
        assertTrue(maze.isMoveValid(new Point(2, 0)));
        // Key
        assertTrue(maze.isMoveValid(new Point(0, 1)));
        // Bot
        assertTrue(maze.isMoveValid(new Point(1, 1)));
        // Pick Ups
        assertTrue(maze.isMoveValid(new Point(2, 1)));
        // Mine
        assertTrue(maze.isMoveValid(new Point(0, 2)));
        // Bonus Chest
        assertTrue(maze.isMoveValid(new Point(1, 2)));
        // Timewarp
        assertTrue(maze.isMoveValid(new Point(2, 2)));
        // Wormhole
        assertTrue(maze.isMoveValid(new Point(0, 3)));
    }

    /**
     * Test to confirm the method findCharOnMap returns the
     * correct Point object
     *
     * @author Andrew McKenzie
     */
    @Test
    @DisplayName("Checking find char returns the correct point")
    public void testFindingAChar() {
        Point testExit = new Point();
        testExit = maze.findCharOnMap(maze.mazeMap, 'E');
        Point actualExit = new Point(1, 3);
        assertTrue(testExit.equals(actualExit));
    }

}