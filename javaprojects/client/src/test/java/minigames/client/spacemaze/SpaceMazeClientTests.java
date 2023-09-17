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
                {'S','.', 'U', '.'},
                {'K', 'B','?', '.'},
                {'M', '$', 'T', '.'},
                {'H', 'E', '.', '.'},
                {'P', '.', 'W', '.'},
                {'W', '.', '.', '.'},
                {'W', 'W', '.', 'W'},
                {'.', 'W', '.', 'W'},
                {'.', '.', '.', 'W'},
        };
        maze = new MazeDisplay(mazeMap, spaceMaze, bots);
    }

    //-------------------Bot class tests------------------------//

    /**
     * Test to check the bot constructor
     * @author Nik Olins
     */
    Point startLocation = new Point(1,8);

    private SpaceBot bot = new SpaceBot(startLocation);
    @DisplayName("Check the bot constructor")
    @Test
    public void testBotConstructor() {
        Point cLoc = bot.getLocation();
        
        assertEquals(startLocation.x, cLoc.x);
        assertEquals(startLocation.y, cLoc.y);
        assertNotNull(bot.testingGetMovesList(), "bot move ArrayList is not null");
        assertTrue(bot.testingIsSeeking(), "bot is seeking");

    }
    /**
     * Test to check an up move
     * @author Nik Olins
     */
    @DisplayName("Check the bot movement up")
    @Test
    public void testBotMoveUp() {
        Point cLoc = bot.getLocation(); 
        Point moveUp = bot.testingGetMoveAttempt(0);
        
        assertEquals(cLoc.x, moveUp.x);
        assertEquals((cLoc.y-1), moveUp.y);
        
    }
    /**
     * Test to check a right move
     * @author Nik Olins
     */
    @DisplayName("Check the bot movement right")
    @Test
    public void testBotMoveRight() {
        Point cLoc = bot.getLocation();
        Point moveRight = bot.testingGetMoveAttempt(1);
       
        assertEquals((cLoc.x+1), moveRight.x);
        assertEquals(cLoc.y, moveRight.y);

    }
    /**
     * Test to check a down move
     * @author Nik Olins
     */
    @DisplayName("Check the bot movement down")
    @Test
    public void testBotMoveDown() {
        Point cLoc = bot.getLocation();
        Point moveDown = bot.testingGetMoveAttempt(2);
       
        assertEquals(cLoc.x, moveDown.x);
        assertEquals((cLoc.y+1), moveDown.y);

    }
    /**
     * Test to check a left move
     * @author Nik Olins
     */
    @DisplayName("Check the bot movement left")
    @Test
    public void testBotMoveLeft() {
        Point cLoc = bot.getLocation();
        Point moveLeft = bot.testingGetMoveAttempt(3);
       
        assertEquals((cLoc.x-1), moveLeft.x);
        assertEquals(cLoc.y, moveLeft.y);

    }
    /**
     * Test to check all legal move commands are accepted. 0 : Up, 1 : Right, 2 : Down, 3 : Left
     * @author Nik Olins
     */
    @DisplayName("Check for legal move commands")
    @Test
    public void testBotMoveCommand() {

        // up, right, down, left
        assertDoesNotThrow(() -> { bot.testingGetMoveAttempt(0); });
        assertDoesNotThrow(() -> { bot.testingGetMoveAttempt(1); });
        assertDoesNotThrow(() -> { bot.testingGetMoveAttempt(2); });
        assertDoesNotThrow(() -> { bot.testingGetMoveAttempt(3); });

    }
    /**
     * Test to check for an illegal move command <0 or >3
     * @author Nik Olins
     */
    @DisplayName("Check for illegal move commands")
    @Test
    public void testIllegalBotMoveCommand() {

       // a command not between 0 - 3 inclusive
       assertThrows(RuntimeException.class, () -> { bot.testingGetMoveAttempt(4); });
    }
    /**
     * Test to check the seeking movement chooses the move to close the distance to the player.
     * @author Nik Olins
     */
    @DisplayName("Testing seeking move selection behaviour.")
    @Test
    public void testDistanceCloser() {
        // Map for reference
        /*
         char [][] mazeMap = {
                {'S','.', 'U', '.'},
                {'K', 'B','?', '.'},
                {'M', '$', 'T', '.'},
                {'H', 'E', '.', '.'},
                {'P', '.', 'W', '.'},
                {'W', '.', '.', '.'},
                {'W', 'W', '.', 'W'},
                {'.', 'W', '.', 'W'},
                {'W', 'W', '.', 'W'},
         */
        // Test when two moves valid moves have the same closure distance.
        Point playerPosOne = new Point(2,3);
        bot.updateLocation(new Point(2,5));
        ArrayList<Point> validMovesOne = new ArrayList<Point>();
        Point posOneInvalidMove = new Point(3,5);
        validMovesOne.add(posOneInvalidMove);
        validMovesOne.add(new Point(2,6));
        Point posOneValidMove = new Point(1,5);
        validMovesOne.add(posOneValidMove);
        Point seekingMoveDecisionOne = bot.testingDistanceCloser(playerPosOne, validMovesOne);
         // test 3, 2, 1, 0 preference order
        assertTrue(posOneValidMove.equals(seekingMoveDecisionOne), "should have selected move 3");
        // Test invalid move choice (0, 1, 2, 3) counter preference
        assertFalse(posOneInvalidMove.equals(seekingMoveDecisionOne), "move 1 should have been invalid");

        // Test when there are no valid moves to get to the player, returns Point(-1,-1)
        Point playerPosTwo = new Point(2,7);
        bot.updateLocation(new Point(0,7));
        ArrayList<Point> validMovesTwo = new ArrayList<Point>();

        Point seekingMoveDecisionTwo = bot.testingDistanceCloser(playerPosTwo, validMovesTwo);
        Point expectedResult = new Point(-1,-1);

        assertTrue(expectedResult.equals(seekingMoveDecisionTwo), "expected Point(-1,-1)");

    }

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
        assertFalse(maze.isMoveValid(new Point(2, 4)));
        // Out of bounds in all four directions
        assertFalse(maze.isMoveValid(new Point(0, -1)));
        assertFalse(maze.isMoveValid(new Point(-1, 0)));
        assertFalse(maze.isMoveValid(new Point(1, 9)));
        assertFalse(maze.isMoveValid(new Point(4, 0)));
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