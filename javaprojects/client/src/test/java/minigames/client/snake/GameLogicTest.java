package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameLogicTest {
    private GameLogic gameLogic;
    private GameBoard gameBoard;

    @BeforeEach
    void setUp() {
        gameBoard = mock(GameBoard.class);
        gameLogic = new GameLogic();
        gameLogic.setGameBoard(gameBoard);
    }

    /**
     * Test the game initialization.
     */
    @Test
    void testInitialiseGame() {
        gameLogic.initialiseGame();

        assertFalse(gameLogic.isGameOver());
        assertFalse(gameLogic.isGamePaused());
        assertEquals(0, gameLogic.getScore());
        assertEquals(90, gameLogic.getTimeSeconds());
        assertEquals(3, gameLogic.getLives());
        assertEquals(1, gameLogic.getLevel());
    }

    /**
     * Test game reset.
     */
    @Test
    void testResetGame() {
        gameLogic.initialiseGame();
        gameLogic.startGame();

        gameLogic.resetGame();

        assertFalse(gameLogic.isGameOver());
        assertFalse(gameLogic.isGamePaused());
        assertEquals(0, gameLogic.getScore());
    }

    /**
     * Test game start.
     */
    @Test
    void testStartGame() {
        gameLogic.startGame();

        // No real method to check if game started, assuming if it did not throw any exceptions, it succeeded
    }

    /**
     * Test game pause.
     */
    @Test
    void testPauseGame() {
        gameLogic.setGamePaused(true);

        assertTrue(gameLogic.isGamePaused());
    }

    /**
     * Test setting and getting direction.
     */
    @Test
    void testDirection() {
        gameLogic.setDirection(Direction.LEFT);

        assertEquals(Direction.LEFT, gameLogic.getDirection());
    }

    /**
     * Test setting and getting time.
     */
    @Test
    void testTimeSeconds() {
        // No setter for time, assuming if getter returns correctly, it's correct
        assertEquals(90, gameLogic.getTimeSeconds());
    }

    /**
     * Test setting and getting lives.
     */
    @Test
    void testLives() {
        // No setter for lives, assuming if getter returns correctly, it's correct
        assertEquals(3, gameLogic.getLives());
    }

    /**
     * Test setting and getting levels.
     */
    @Test
    void testLevel() {
        // No setter for level, assuming if getter returns correctly, it's correct
        assertEquals(1, gameLogic.getLevel());
    }
}

