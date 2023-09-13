package minigames.client.snake;

import java.awt.Point;
import java.util.Random;

/**
 * Represents the food in the Snake game.
 */
public class Food {

    /**
     * The position of the food on the game board.
     */
    private Point position;

    /**
     * Random number generator to generate new food positions.
     */
    private final Random random;

    /**
     * Constructor to initialize the food position and random generator.
     *
     * @param width  Width of the game board.
     * @param height Height of the game board.
     */
    public Food(int width, int height) {
        this.random = new Random();
        generateNewPosition(width, height);
    }

    /**
     * Returns the current position of the food.
     *
     * @return Point representing the position of the food.
     */
    public Point getPosition() {
        return this.position;
    }

    /**
     * Generates a new position for the food.
     *
     * @param width  Width of the game board.
     * @param height Height of the game board.
     */
    public void generateNewPosition(int width, int height) {
        int x = this.random.nextInt(width);
        int y = this.random.nextInt(height);
        this.position = new Point(x, y);
    }
}
