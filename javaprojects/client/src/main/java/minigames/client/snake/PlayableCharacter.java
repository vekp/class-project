package minigames.client.snake;


/**
 * Represents the playable character in the Snake game, which is the snake itself.
 */
public class PlayableCharacter {

    // The x-coordinates of all parts of the snake.
    private final int[] x;

    // The y-coordinates of all parts of the snake.
    private final int[] y;

    // Number of parts the snake currently has.
    private int bodyParts;

    // The current direction of the snake.
    private Direction direction;
    private final GameBoard gameBoard;

    /**
     * Constructor: Initializes a new snake character.
     *
     * @param gameBoard The game board on which the snake moves.
     */
    public PlayableCharacter(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        x = new int[GameConstants.GAME_PLAY_WIDTH];
        y = new int[GameConstants.GAME_PLAY_HEIGHT];
        bodyParts = 5;
        this.direction = Direction.RIGHT;
        initializeSnake();
    }

    /**
     * Moves the snake in the specified direction and handles collisions.
     *
     * @param direction The direction in which the snake should move.
     * @throws CollisionException If a collision occurs (e.g., with walls, food, or itself).
     */
    public void move(Direction direction) throws CollisionException {
        this.setDirection(direction);
        // Shift the position of body parts to follow the head.
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Determine the next position based on direction
        switch (this.direction) {
            case UP -> y[0] -= 1;
            case DOWN -> y[0] += 1;
            case LEFT -> x[0] -= 1;
            case RIGHT -> x[0] += 1;
        }

        // Check for out-of-bounds
        if (x[0] < 0 || x[0] >= gameBoard.getWidth() || y[0] < 0 || y[0] >= gameBoard.getHeight()) {
            throw new OutOfBoundsException();
        }

        ItemType itemType = gameBoard.getItemTypeAt(x[0], y[0]);

        // Check for collision with itself
        if (itemType == ItemType.SNAKE) {
            throw new SelfCollisionException();
        }

        // Update the snake's position
        gameBoard.setItemTypeAt(x[0], y[0], ItemType.SNAKE);
        if (bodyParts < x.length) {
            gameBoard.setItemTypeAt(x[bodyParts], y[bodyParts], ItemType.VACANT);
        }

        // Check for collisions with food and throw an exception after updating the position
        if (itemType == ItemType.APPLE || itemType == ItemType.CHERRY || itemType == ItemType.WATERMELON ||
                itemType == ItemType.ORANGE || itemType == ItemType.SPOILED_FOOD) {
            throw new FoodCollisionException(itemType);
        }
    }

    /**
     * Increases the size of the snake by one.
     */
    public void grow() {
        bodyParts++;
    }

    // Getters and Setters

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        // Prevent the snake from reversing direction and colliding with itself.
        if ((this.direction == Direction.UP && direction != Direction.DOWN) ||
                (this.direction == Direction.DOWN && direction != Direction.UP) ||
                (this.direction == Direction.LEFT && direction != Direction.RIGHT) ||
                (this.direction == Direction.RIGHT && direction != Direction.LEFT)) {
            this.direction = direction;
        }
    }

    /**
     * Initializes the snake to its starting position and size.
     */
    private void initializeSnake() {
        for (int i = 0; i < GameConstants.INITIAL_SNAKE_PARTS; i++) {
            x[i] = GameConstants.INITIAL_SNAKE_X - i;
            y[i] = GameConstants.INITIAL_SNAKE_Y;
        }
        bodyParts = GameConstants.INITIAL_SNAKE_PARTS;
    }
}

/**
 * A custom exception class for collisions in the Snake game.
 */
class CollisionException extends Exception {
    public CollisionException(String message) {
        super(message);
    }
}

/**
 * An exception thrown when a collision occurs with food.
 */
class FoodCollisionException extends CollisionException {
    public FoodCollisionException(ItemType foodType) {
        super(foodType.name());
    }
}

/**
 * An exception thrown when a collision occurs with the snake itself.
 */
class SelfCollisionException extends CollisionException {
    public SelfCollisionException() {
        super(ItemType.SNAKE.name());
    }
}

/**
 * An exception thrown when the snake goes out of bounds.
 */
class OutOfBoundsException extends CollisionException {
    public OutOfBoundsException() {
        super(ItemType.WALL.name());
    }
}
