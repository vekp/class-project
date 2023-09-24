package minigames.client.snake;

import javax.swing.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * The GameLogic class handles the game logic for the Snake game.
 */
public class GameLogic {
    private PlayableCharacter snake;
    private GameBoard gameBoard;

    // Game state flags
    private boolean gameStarted;
    private boolean isGamePaused;
    private boolean isGameOver;

    private Direction direction;
    private int score;
    private Random random;
    private int timeLeft;
    private int level;
    private int livesLeft;
    private int minimumFoodDelay;
    private int maximumFoodDelay;
    private int foodGenerationDelay;
    private int foodConsumed = 0;
    private long lastFoodAddedTime = 0;
    private LinkedList<FoodManager> foodList = new LinkedList<>();
    private Timer timer;
    private int gameLoopDelay;

    /**
     * Constructs a GameLogic instance.
     */
    public GameLogic() {
        initialiseGame();
    }

    /**
     * Initializes the game state.
     */
    public void initialiseGame() {
        this.gameLoopDelay = GameConstants.GAME_LOOP_DELAY;
        this.minimumFoodDelay = GameConstants.MINIMUM_FOOD_GENERATION_DELAY;
        this.maximumFoodDelay = GameConstants.MAXIMUM_FOOD_GENERATION_DELAY;
        this.random = new Random();
        this.gameStarted = false;
        this.isGamePaused = false;
        this.isGameOver = false;
        this.score = 0;
        this.timeLeft = 90;
        this.level = 1;
        this.livesLeft = 3;
        this.direction = Direction.RIGHT; // Default direction
        this.snake = new PlayableCharacter(gameBoard);
        timer = new Timer(1000, e -> {
            if (!isGamePaused && gameStarted && !isGameOver) {
                timeLeft--; // Decrease the timeLeft by 1 second
                if (timeLeft <= 0) {
                    // Handle time running out here
                    timer.stop(); // Stop the timer
                    isGameOver = true;
                }
            }
        });
        timer.start(); // Start the timer
    }

    /**
     * The game loop that handles game logic.
     */
    public void gameLoop() {
        if (!isGamePaused && !isGameOver && gameStarted) {
            handleFood();
            handleSnakeMovement();
        }
    }

    /**
     * Handles the logic for snake movement and collisions.
     * The method encapsulates the behavior for moving the snake and handling
     * different types of collisions such as with food, self-collision,
     * and going out of bounds.
     */
    private void handleSnakeMovement() {
        try {
            // Attempt to move the snake in the current direction
            snake.move(this.direction);
        } catch (FoodCollisionException e) {
            // Handle the case when the snake collides with food
            handleFoodCollision(e);
        } catch (SelfCollisionException | OutOfBoundsException e) {
            // Handle the case when the snake collides with itself or goes out of bounds
            handleSelfCollisionOrOutOfBounds();
        } catch (CollisionException e) {
            // For any other type of collision, throw a runtime exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the collision of the snake with food.
     * Updates game state based on the type of food that the snake collides with.
     *
     * @param e The FoodCollisionException containing information about the collided food.
     */
    private void handleFoodCollision(FoodCollisionException e) {
        // Determine the type of food collided with
        ItemType foodType = ItemType.valueOf(e.getMessage());

        // If the food is not spoiled, update time and food consumed
        if (foodType != ItemType.SPOILED_FOOD) {
            timeLeft += 3;
            foodConsumed++;
            if (foodConsumed % GameConstants.LEVEL_CHANGE_THRESHOLD == 0) {
                this.levelUp();
            }
        }

        // Update the score and other game states based on the type of food
        switch (foodType) {
            case SPOILED_FOOD -> score -= GameConstants.SPOILED_FOOD_SCORE;
            case APPLE -> {
                score += GameConstants.APPLE_EATEN_SCORE;
                snake.grow();
            }
            case CHERRY -> {
                score += GameConstants.CHERRY_EATEN_SCORE;
                snake.grow();
            }
            case ORANGE -> {
                score += GameConstants.ORANGE_EATEN_SCORE;
                snake.grow();
            }
            case WATERMELON -> {
                score += GameConstants.WATERMELON_EATEN_SCORE;
                timeLeft += GameConstants.WATERMELON_EATEN_SCORE;
                snake.grow();
            }
        }
    }

    /**
     * Handles the collision of the snake with itself or going out of bounds.
     * Updates the game state, reduces the lives left, and either ends the game
     * or resets the snake's state.
     */
    private void handleSelfCollisionOrOutOfBounds() {
        // Play a negative sound effect to indicate an error
        MultimediaManager.playSoundEffect("Negative");

        // Decrease the number of lives left
        livesLeft--;

        // Check if the game should end
        if (livesLeft <= 0) {
            isGameOver = true;
        }
        else {
            // Clear the current snake tiles and re-initialize the snake
            gameBoard.clearTilesOfType(ItemType.SNAKE);
            gameBoard.clearTilesOfType(ItemType.SNAKE_HEAD);
            snake = new PlayableCharacter(this.gameBoard);
            this.setDirection(Direction.RIGHT);
        }
    }

    /**
     * Handles the food items, including their regeneration and removal.
     */
    private void handleFood() {
        Iterator<FoodManager> iterator = foodList.iterator();
        while (iterator.hasNext()) {
            FoodManager f = iterator.next();
            f.updateFoodStatus();

            // If food has spoiled, you can implement additional logic here, e.g., play a sound
            // effect

            if (f.isRemovable()) {
                iterator.remove();
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFoodAddedTime >= foodGenerationDelay * 1000L) {
            generateNewFood();
        }
    }

    /**
     * Generates new food items on the game board.
     */
    private void generateNewFood() {
        int availableSpaces = 0;

        for (FoodManager f : foodList) {
            if (f.getType() == ItemType.VACANT) {
                availableSpaces++;
            }
        }

        if (availableSpaces < GameConstants.MAXIMUM_FOOD_ON_SCREEN) {
            FoodManager newFood = new FoodManager(gameBoard);
            newFood.generate();
            foodList.add(newFood);
            lastFoodAddedTime = System.currentTimeMillis();
            foodGenerationDelay = random.nextInt(minimumFoodDelay, maximumFoodDelay);
        }
    }

    /**
     * Gets the game loop delay.
     *
     * @return The game loop delay.
     */
    public int getGameLoopDelay() {
        return this.gameLoopDelay;
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        this.initialiseGame();
        foodList = new LinkedList<>();
        snake = new PlayableCharacter(this.gameBoard);
        gameBoard.initBoard();
        timer.restart(); // Restart the timer
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        gameStarted = true;
        if (!timer.isRunning()) {
            timer.start(); // Start the timer if it's not running
        }
    }

    /**
     * Sets the game pause state.
     *
     * @param gamePaused True if the game is paused, false otherwise.
     */
    public void setGamePaused(boolean gamePaused) {
        isGamePaused = gamePaused;
        if (gamePaused) {
            timer.stop(); // Pause the timer
        }
        else {
            timer.start(); // Resume the timer
        }
    }

    // Getter and Setter methods...

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getScore() {
        return score;
    }

    public PlayableCharacter getSnake() {
        return snake;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.snake = new PlayableCharacter(gameBoard);
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getTimeSeconds() {
        return timeLeft;
    }

    public int getLives() {
        return livesLeft;
    }

    public int getLevel() {
        return level;
    }

    public void levelUp() {
        level++;
    }

    public void setSnake(PlayableCharacter snake) {
        this.snake = snake;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

}
