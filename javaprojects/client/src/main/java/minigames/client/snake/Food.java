package minigames.client.snake;

/**
 * Represents the Food object in the Snake game.
 * The Food object has a position defined by x and y coordinates on the game grid.
 */
public class Food {

    private int x;  // The x-coordinate of the food on the game grid
    private int y;  // The y-coordinate of the food on the game grid

    /**
     * Retrieves the x-coordinate of the food.
     *
     * @return The x-coordinate of the food.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate of the food.
     *
     * @return The y-coordinate of the food.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the position of the food on the game grid.
     *
     * @param x The x-coordinate to set for the food.
     * @param y The y-coordinate to set for the food.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
