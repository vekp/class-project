package minigames.client.snake;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultimediaManagerTest {

    @BeforeAll
    public static void setUpBeforeAll() {
        // This method should be called before any tests that require audio resources.
        // Load audio resources here or mock them if necessary.
    }

    @BeforeEach
    public void setUp() {
        // Initialize or reset resources for each test case if needed.
    }

      @Test
    public void testPlaySoundEffect() {
        // Test playing a positive sound effect
        MultimediaManager.playSoundEffect("Positive");
        // Wait for a moment to allow the sound to finish playing
        try {
            Thread.sleep(1000); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test playing a negative sound effect
        MultimediaManager.playSoundEffect("Negative");
        // Wait for a moment to allow the sound to finish playing
        try {
            Thread.sleep(1000); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFoodImagesSize() {
        int size = MultimediaManager.getFoodImagesSize();
        assertTrue(size >= 0);
    }

    @Test
    public void testGetFoodImageByIndex() {
        // Test getting food images by index
        int size = MultimediaManager.getFoodImagesSize();
        for (int i = 0; i < size; i++) {
            ImageResource foodImage = MultimediaManager.getFoodImageByIndex(i);
            assertNotNull(foodImage);
        }

        // Test getting a food image with an out-of-bounds index
        assertThrows(IndexOutOfBoundsException.class, () -> MultimediaManager.getFoodImageByIndex(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> MultimediaManager.getFoodImageByIndex(size));
    }
}
