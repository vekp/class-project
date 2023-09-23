package minigames.client.snake;

import java.util.Random;

/**
 * Represents a food item in the Snake game.
 * Each food item has a type, position, and associated image.
 * Food can spoil after a certain duration and becomes vacant after an additional duration.
 */
public class FoodManager {
    private int x;  // X-coordinate of the food on the game board
    private int y;  // Y-coordinate of the food on the game board
    private ItemType type;  // Type of the food (e.g., APPLE, CHERRY, etc.)
    private final GameBoard gameBoard;  // The game board on which the food is placed
    private boolean isSpoiled;  // Whether the food has spoiled
    private int creationTime;  // Time when the food was created (or regenerated)
    private int spoilTime;
    private boolean isRemovable;

    /**
     * Constructor for the FoodManager class.
     *
     * @param gameBoard The game board on which the food is to be placed.
     * @throws IllegalArgumentException if gameBoard is null.
     */
    public FoodManager(GameBoard gameBoard) {
        if (gameBoard == null) {
            throw new IllegalArgumentException("GameBoard cannot be null.");
        }
        this.gameBoard = gameBoard;
        this.x = -1;  // Initialize to an invalid value
        this.y = -1;  // Initialize to an invalid value
        this.isRemovable = false;
    }

    /**
     * Randomly selects a food type and its associated image.
     * The food type is selected from a predefined set of food images.
     * The selected image is associated with the food type.
     */
    private void selectRandomFoodTypeAndImage() {
        Random rand = new Random();
        int foodIndex = rand.nextInt(MultimediaManager.getFoodImagesSize());

        // The image representing the food
        ImageResource foodImage = MultimediaManager.getFoodImageByIndex(foodIndex);

        // Setting the ItemType based on the chosen ImageResource
        if (foodImage == MultimediaManager.getAppleResource()) {
            type = ItemType.APPLE;
        }
        else if (foodImage == MultimediaManager.getCherryResource()) {
            type = ItemType.CHERRY;
        }
        else if (foodImage == MultimediaManager.getOrangeResource()) {
            type = ItemType.ORANGE;
        }
        else if (foodImage == MultimediaManager.getWatermelonResource()) {
            type = ItemType.WATERMELON;
        }
        else {
            throw new RuntimeException("Unknown food image resource.");
        }
    }

    /**
     * Gets the X-coordinate of the food on the game board.
     *
     * @return The X-coordinate of the food.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y-coordinate of the food on the game board.
     *
     * @return The Y-coordinate of the food.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the type of the food (e.g., APPLE, CHERRY, etc.).
     *
     * @return The type of the food.
     */
    public ItemType getType() {
        return type;
    }

    /**
     * Checks if the food has spoiled.
     *
     * @return True if the food has spoiled, otherwise false.
     */
    public boolean isSpoiled() {
        return isSpoiled;
    }

    /**
     * Sets the position of the food on the game board.
     * If the specified position is vacant, the food is placed at that position.
     *
     * @param x The X-coordinate for the food's position.
     * @param y The Y-coordinate for the food's position.
     */
    public void setPosition(int x, int y) {
        if (gameBoard.getItemTypeAt(x, y) == ItemType.VACANT) {
            // Set previous position to VACANT
            if (this.x >= 0 && this.y >= 0) {
                gameBoard.setItemTypeAt(this.x, this.y, ItemType.VACANT);
            }
            // Set new position
            this.x = x;
            this.y = y;
            gameBoard.setItemTypeAt(x, y, type);
        }
    }

    /**
     * Regenerates the food: selects a new random type and places it at a vacant position on the
     * board.
     * The food type and position are randomly chosen, and the food is marked as not spoiled.
     */
    public void generate() {
        selectRandomFoodTypeAndImage();
        Random rand = new Random();
        int randX;
        int randY;
        do {
            randX = rand.nextInt(gameBoard.getWidth());
            randY = rand.nextInt(gameBoard.getHeight());
        } while (gameBoard.getItemTypeAt(randX, randY) != ItemType.VACANT);

        setPosition(randX, randY);
        this.isSpoiled = false;
        this.creationTime = (int) System.currentTimeMillis();
    }

    /**
     * Updates the status of the food based on elapsed time.
     * The food spoils after the specified spoilThreshold duration and becomes vacant after an
     * additional
     * spoiledDelay seconds.
     */
    public void updateFoodStatus() {
        ItemType itemTypeAtLocation = gameBoard.getItemTypeAt(x, y);

        // Check if the food's location on the game board is vacant
        if (itemTypeAtLocation == ItemType.VACANT) {
            this.isRemovable = true;
            return;
        }

        int spoilThreshold = GameConstants.SPOILED_FOOD_THRESHOLD;
        int removeDelay = GameConstants.SPOILED_FOOD_REMOVE_DELAY;

        // Check for spoilage if the food is not already spoiled
        if (!isSpoiled && timeElapsed(creationTime, spoilThreshold)) {
            isSpoiled = true;
            spoilTime = (int) System.currentTimeMillis();  // Store the time when the food spoils
            type = ItemType.SPOILED_FOOD;
            gameBoard.setItemTypeAt(x, y, type);
        }
        // Check for removal if the food is already spoiled
        else if (isSpoiled && timeElapsed(spoilTime, removeDelay)) {
            gameBoard.setItemTypeAt(x, y, ItemType.VACANT);
            this.isRemovable = true;
        }
    }

    /**
     * Checks if the food has spoiled based on a provided threshold.
     *
     * @param interval The threshold (in seconds) to check against.
     * @return True if the food has spoiled, otherwise false.
     */
    public boolean timeElapsed(int startTime, int interval) {
        int currentTime = (int) System.currentTimeMillis();
        int elapsedTime = (currentTime - startTime) / 1000;
        return elapsedTime > interval;
    }

    /**
     * Checks if the food is removable (vacant).
     *
     * @return True if the food is removable, otherwise false.
     */
    public boolean isRemovable() {
        return this.isRemovable;
    }
}
