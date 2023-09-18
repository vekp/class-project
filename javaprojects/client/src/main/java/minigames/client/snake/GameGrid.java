package minigames.client.snake;

import java.awt.Point;
import java.util.Random;

/**
 * Represents the game grid for the Snake game.
 */
public class GameGrid {

    private final GridItem[][] grid; //  Two-dimensional array representing the game grid.

    private final Snake snake; // Instance of the Snake in the game.

    private Point foodPosition; // Position of the food in the game grid.

    private static final int WIDTH = 40; // Width of the game grid.

    private static final int HEIGHT = 30; // Height of the game grid.

    private final Random random = new Random(); // Random number generator
    // for generating food positions.


    private static final int HEIGHT_OFFSET = 1; // Offset for the top row


    /**
     * Initializes the game grid and places the snake and food.
     */
    public GameGrid() {
        this.grid = new GridItem[WIDTH][HEIGHT];
        this.snake = new Snake();
        generateFoodPosition();
        updateGrid();
    }

    /**
     * Returns the Snake object in the game.
     *
     * @return Instance of Snake.
     */
    public Snake getSnake() {
        return this.snake;
    }

    /**
     * Returns the position of the food.
     *
     * @return Point representing food's position.
     */
    public Point getFoodPosition() {
        return this.foodPosition;
    }

    /**
     * Returns the current game grid.
     *
     * @return Two-dimensional array of GridItem.
     */
    public GridItem[][] getGrid() {
        return this.grid;
    }

    /**
     * Updates the game grid based on the current state of the game.
     */
    public void updateGrid() {
        // Clear grid
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                grid[i][j] = GridItem.EMPTY;
            }
        }

        // Place snake
        for (Point p : snake.getBody()) {
            grid[p.x][p.y] = GridItem.SNAKE;
        }

        // Place food
        grid[foodPosition.x][foodPosition.y] = GridItem.FOOD;
    }

    /**
     * Generates a new position for the food on the grid.
     */
    public void generateFoodPosition() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT - HEIGHT_OFFSET) + HEIGHT_OFFSET;
        } while (grid[x][y] != GridItem.EMPTY);

        this.foodPosition = new Point(x, y);
    }

    /**
     * Checks if the snake has consumed the food.
     *
     * @return true if food is consumed, false otherwise.
     */
    public boolean checkFoodConsumption() {
        return snake.getBody().getFirst().equals(foodPosition);
    }

    /**
     * Checks if the snake has collided with itself or the wall.
     *
     * @return true if collision occurs, false otherwise.
     */
    public boolean checkCollision() {
        return snake.isCollidingWithSelf() || snake.isCollidingWithWall(WIDTH, HEIGHT);
    }
}
