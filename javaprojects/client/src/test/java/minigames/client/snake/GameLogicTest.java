package minigames.client.snake;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameLogicTest {
    private GameLogic gameLogic;
    private GameBoard gameBoard;

    @Before
    public void setUp() {
        gameLogic = new GameLogic();
        gameBoard = new GameBoard(10, 10);
        gameLogic.setGameBoard(gameBoard);
    }

    @Test
    public void testInitializeGame() {
        gameLogic.initialiseGame();
        assertFalse(gameLogic.isGameStarted());
        assertFalse(gameLogic.isGamePaused());
        assertFalse(gameLogic.isGameOver());
        assertEquals(0, gameLogic.getScore());
        assertEquals(90, gameLogic.getTimeSeconds());
        assertEquals(1, gameLogic.getLevel());
        assertEquals(3, gameLogic.getLives());
        assertNotNull(gameLogic.getSnake());
    }

    @Test
    public void testGameLoop() {
        gameLogic.initialiseGame();
        gameLogic.setGameStarted(true);

        // Simulate a game loop cycle
        gameLogic.gameLoop();

        // Assert expected game state changes
        // You can check if the snake moves, food is generated, and other game mechanics are working as expected
    }
}