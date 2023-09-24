package minigames.client.snake;

import minigames.client.snake.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoodManagerTest {

    private GameBoard gameBoard;
    private FoodManager foodManager;

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard(10, 10); // Adjust the board size as needed
        foodManager = new FoodManager(gameBoard);
    }

    @Test
    void testInitialization() {
        assertNotNull(foodManager);
        assertEquals(-1, foodManager.getX());
        assertEquals(-1, foodManager.getY());
        assertNull(foodManager.getType());
        assertFalse(foodManager.isSpoiled());
        assertFalse(foodManager.isRemovable());
    }

    @Test
    void testSetPosition() {
        foodManager.generate(); // Generate food to set its position

        int x = 5;
        int y = 5;
        foodManager.setPosition(x, y);

        assertEquals(x, foodManager.getX());
        assertEquals(y, foodManager.getY());
        ItemType type = foodManager.getType();
        assertNotNull(type);
        assertNotEquals(ItemType.SNAKE, type);
        assertNotEquals(ItemType.VACANT, type);
        assertNotEquals(ItemType.WALL, type);
    }

    @Test
    void testGenerate() {
        foodManager.generate();

        assertTrue(foodManager.getX() >= 0);
        assertTrue(foodManager.getX() < gameBoard.getWidth());
        assertTrue(foodManager.getY() >= 0);
        assertTrue(foodManager.getY() < gameBoard.getHeight());
        assertNotNull(foodManager.getType());
        assertFalse(foodManager.isSpoiled());
    }

    @Test
    void testUpdateFoodStatus() {
        foodManager.generate();
        foodManager.updateFoodStatus();

        assertFalse(foodManager.isSpoiled());
        assertFalse(foodManager.isRemovable());

        // Simulate time elapsed to spoil the food
        int spoilThreshold = GameConstants.SPOILED_FOOD_THRESHOLD;
        sleepSeconds(spoilThreshold + 1); // Wait for spoilThreshold + 1 seconds

        foodManager.updateFoodStatus();

        assertTrue(foodManager.isSpoiled());
        assertFalse(foodManager.isRemovable());

        // Simulate time elapsed to make the spoiled food removable
        int removeDelay = GameConstants.SPOILED_FOOD_REMOVE_DELAY;
        sleepSeconds(removeDelay + 1); // Wait for removeDelay + 1 seconds

        foodManager.updateFoodStatus();

        assertTrue(foodManager.isSpoiled());
        assertTrue(foodManager.isRemovable());
    }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
