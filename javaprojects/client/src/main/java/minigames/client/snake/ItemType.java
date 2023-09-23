package minigames.client.snake;

/**
 * Enum to represent different types of items available in the Snake game.
 * Each item type corresponds to a specific fruit that the snake can consume,
 * along with other game-related elements.
 */
public enum ItemType {
    APPLE,      // Represents an apple item
    CHERRY,     // Represents a cherry item
    ORANGE,     // Represents an orange item
    WATERMELON, // Represents a watermelon item
    SPOILED_FOOD, // Represents spoiled food
    SNAKE,      // Represents a segment of the snake's body
    SNAKE_HEAD, // Represents the head of the snake
    VACANT,      // Represents an empty or vacant cell on the game board
WALL // Represents the boundary of the playable area
}
