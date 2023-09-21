package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FoodManagerTest {
    private GameBoard board;
    private FoodManager foodManager;

    @BeforeEach
    void setUp() {
        board = new GameBoard(10, 10);
        foodManager = new FoodManager(board);
    }

    /**
     * Test the constructor for proper initialization.
     */
    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new FoodManager(null));
        assertEquals(-1, foodManager.getX());
        assertEquals(-1, foodManager.getY());
    }

    /**
     * Test the regeneration of food.
     */
    @Test
    void testRegenerate() {
        foodManager.regenerate();
        assertTrue(foodManager.getX() >= 0 && foodManager.getX() < 10);
        assertTrue(foodManager.getY() >= 0 && foodManager.getY() < 10);
        assertNotNull(foodManager.getType());
        assertFalse(foodManager.isSpoiled());
    }

    /**
     * Test setting the position of food.
     */
    @Test
    void testSetPosition() {
        foodManager.setPosition(3, 3);
        assertEquals(3, foodManager.getX());
        assertEquals(3, foodManager.getY());
        assertEquals(board.getItemTypeAt(3, 3), foodManager.getType());
    }

    /**
     * Test the spoilage of food based on a custom threshold.
     */
    @Test
    void testHasSpoiled() throws InterruptedException {
        foodManager.regenerate();
        // Simulate the passage of time to make the food spoil.
        Thread.sleep(2000);  // 2 seconds
        assertTrue(foodManager.hasSpoiled(1));  // Should spoil after 1 second
        assertFalse(foodManager.hasSpoiled(3));  // Should not spoil before 3 seconds
    }

    /**
     * Test the update of food status.
     */
    @Test
    void testUpdateFoodStatus() throws InterruptedException {
        foodManager.regenerate();
        assertFalse(foodManager.isSpoiled());
        // Simulate the passage of time to make the food spoil.
        Thread.sleep(GameConstants.SPOILED_FOOD_THRESHOLD * 1000 + 1000);
        foodManager.updateFoodStatus();
        assertTrue(foodManager.isSpoiled());
    }
}
