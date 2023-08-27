package minigames.client.spacemaze;

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

public class SpaceMazeClientTests {

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


}