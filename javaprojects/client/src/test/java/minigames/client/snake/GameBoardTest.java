package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    private GameBoard gameBoard;

    @BeforeEach
    public void setUp() {
        gameBoard = new GameBoard(5, 5);
    }

    /**
     * Test that the game board is initialized correctly.
     */
    @Test
    public void testInitialization() {
        assertNotNull(gameBoard);
        assertEquals(5, gameBoard.getWidth());
        assertEquals(5, gameBoard.getHeight());
    }

    /**
     * Test getting the ItemType at a specific coordinate.
     */
    @Test
    public void testGetItemTypeAt() {
        assertEquals(ItemType.VACANT, gameBoard.getItemTypeAt(0, 0));
    }

    /**
     * Test getting the ItemType at an out-of-bounds coordinate.
     */
    @Test
    public void testGetItemTypeAtOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            gameBoard.getItemTypeAt(-1, 0);
        });
    }

    /**
     * Test setting the ItemType at a specific coordinate.
     */
    @Test
    public void testSetItemTypeAt() {
        gameBoard.setItemTypeAt(1, 1, ItemType.APPLE);
        assertEquals(ItemType.APPLE, gameBoard.getItemTypeAt(1, 1));
    }

    /**
     * Test setting the ItemType at an out-of-bounds coordinate.
     */
    @Test
    public void testSetItemTypeAtOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            gameBoard.setItemTypeAt(5, 0, ItemType.APPLE);
        });
    }

    /**
     * Test clearing all cells of a specific ItemType.
     */
    @Test
    public void testClearTilesOfType() {
        gameBoard.setItemTypeAt(1, 1, ItemType.APPLE);
        gameBoard.setItemTypeAt(2, 2, ItemType.APPLE);

        gameBoard.clearTilesOfType(ItemType.APPLE);

        assertEquals(ItemType.VACANT, gameBoard.getItemTypeAt(1, 1));
        assertEquals(ItemType.VACANT, gameBoard.getItemTypeAt(2, 2));
    }

}
