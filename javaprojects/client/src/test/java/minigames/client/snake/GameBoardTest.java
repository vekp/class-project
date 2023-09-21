package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard(10, 10);
    }

    /**
     * Test the constructor for proper board initialization.
     */
    @Test
    void testConstructor() {
        assertEquals(10, board.getWidth());
        assertEquals(10, board.getHeight());
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                assertEquals(ItemType.VACANT, board.getItemTypeAt(x, y));
            }
        }
    }

    /**
     * Test the getWidth method.
     */
    @Test
    void testGetWidth() {
        assertEquals(10, board.getWidth());
    }

    /**
     * Test the getHeight method.
     */
    @Test
    void testGetHeight() {
        assertEquals(10, board.getHeight());
    }

    /**
     * Test getting and setting an ItemType at a specific coordinate.
     */
    @Test
    void testGetAndSetItemType() {
        board.setItemTypeAt(5, 5, ItemType.APPLE);
        assertEquals(ItemType.APPLE, board.getItemTypeAt(5, 5));
    }

    /**
     * Test clearing all tiles of a specific ItemType.
     */
    @Test
    void testClearTilesOfType() {
        board.setItemTypeAt(5, 5, ItemType.APPLE);
        board.setItemTypeAt(6, 6, ItemType.APPLE);
        board.clearTilesOfType(ItemType.APPLE);
        assertEquals(ItemType.VACANT, board.getItemTypeAt(5, 5));
        assertEquals(ItemType.VACANT, board.getItemTypeAt(6, 6));
    }

    /**
     * Test for IndexOutOfBoundsException when coordinates are out of bounds.
     */
    @Test
    void testIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getItemTypeAt(11, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> board.getItemTypeAt(5, 11));
        assertThrows(IndexOutOfBoundsException.class, () -> board.setItemTypeAt(11, 5, ItemType.APPLE));
        assertThrows(IndexOutOfBoundsException.class, () -> board.setItemTypeAt(5, 11, ItemType.APPLE));
    }
}
