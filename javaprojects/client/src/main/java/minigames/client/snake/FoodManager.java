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
    private ImageResource foodImage;  // The image representing the food
    private boolean isEaten;  // Whether the food has been eaten by the snake
    private boolean isSpoiled;  // Whether the food has spoiled
    private int creationTime;  // Time when the food was created (or regenerated)
    private int spoilThreshold = GameConstants.SPOILED_FOOD_THRESHOLD;

    /**
     * Constructor for the Food class.
     *
     * @param gameBoard The game board on which the food is to be placed.
     */
    public FoodManager(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Randomly selects a food type and its associated image.
     */
    private void selectRandomFoodTypeAndImage() {
        Random rand = new Random();
        int foodIndex = rand.nextInt(MultimediaManager.getFoodImagesSize());

        foodImage = MultimediaManager.getFoodImageByIndex(foodIndex);

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ItemType getType() {
        return type;
    }

    public ImageResource getFoodImage() {
        return foodImage;
    }

    public boolean isEaten() {
        return isEaten;
    }

    public void setEaten(boolean isEaten) {
        this.isEaten = isEaten;
    }

    public boolean isSpoiled() {
        return isSpoiled;
    }

    public void setSpoiled(boolean isSpoiled) {
        this.isSpoiled = isSpoiled;
    }

    public int getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the position of the food on the game board.
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
     */
    public void regenerate() {
        selectRandomFoodTypeAndImage();
        Random rand = new Random();
        int randX;
        int randY;
        do {
            randX = rand.nextInt(gameBoard.getWidth());
            randY = rand.nextInt(gameBoard.getHeight());
        } while (gameBoard.getItemTypeAt(randX, randY) != ItemType.VACANT);

        setPosition(randX, randY);
        this.isEaten = false;
        this.isSpoiled = false;
        this.creationTime = (int) System.currentTimeMillis();
    }

    /**
     * Updates the status of the food based on elapsed time.
     * The food spoils after the spoilThreshold duration and becomes vacant after an additional
     * spoiledDelay seconds.
     */
    public void updateFoodStatus() {
        if (!isSpoiled && hasSpoiled()) {
            isSpoiled = true;
            type = ItemType.SPOILED_FOOD;
            gameBoard.setItemTypeAt(x, y, type);
        }
        else if (isSpoiled && hasSpoiled(GameConstants.SPOILEDFOOD_REMOVE_DELAY)) {
            gameBoard.setItemTypeAt(x, y, ItemType.VACANT);
        }
    }

    // TODO REVIEW SPOILED LOGIC AND INTERACTIONS - NOT SHOWING AS SPOILED FOR VERY LONG
    /**
     * Checks if the food has spoiled based on a provided threshold.
     *
     * @param customThreshold The threshold (in seconds) to check against.
     * @return True if the food has spoiled, otherwise false.
     */
    public boolean hasSpoiled(int customThreshold) {
        int currentTime = (int) System.currentTimeMillis();
        int elapsedTime = (currentTime - creationTime) / 1000;
        return elapsedTime > customThreshold;
    }

    /**
     * Checks if the food has spoiled based on the default spoilThreshold.
     *
     * @return True if the food has spoiled, otherwise false.
     */
    public boolean hasSpoiled() {
        return hasSpoiled(spoilThreshold);
    }

    /**
     * Decreases the spoil threshold by 1 second, ensuring it never goes below 10.
     */
    public void decreaseSpoilThreshold() {
        spoilThreshold--;
    }
}
