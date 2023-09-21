package minigames.client.snake;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * The GameLogic class handles the game logic for the Snake game.
 */
public class GameLogic {
    private PlayableCharacter snake;
    private GameBoard gameBoard;

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
    private final LinkedList<FoodManager> foodList = new LinkedList<>();

    /**
     * Constructs a GameLogic instance.
     */
    public GameLogic() {
        initialiseGame();
    }

    public void initialiseGame() {
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
    }

    /**
     * The game loop that handles game logic.
     */
    public void gameLoop() {
        if (!isGamePaused && !isGameOver && gameStarted) {
            handleFood();

            try {
                snake.move(this.direction);
            } catch (FoodCollisionException e) {
                ItemType foodType = ItemType.valueOf(e.getMessage());
                if (foodType != ItemType.SPOILED_FOOD) {
                    timeLeft += 15;
                    foodConsumed++;
                    if (foodConsumed % GameConstants.LEVEL_CHANGE_THRESHOLD == 0) {
                        levelUp();
                    }
                }

                switch (foodType) {
                    case SPOILED_FOOD -> score -= 100;
                    case APPLE -> {
                        score += 1;
                        snake.grow();
                    }
                    case CHERRY -> {
                        score += 5;
                        timeLeft += 15;
                        snake.grow();
                    }
                    case ORANGE -> {
                        score += 10;
                        timeLeft += 15;
                        snake.grow();
                    }
                    case WATERMELON -> {
                        score += 20;
                        timeLeft += 20;
                        snake.grow();
                    }
                }
            } catch (SelfCollisionException | OutOfBoundsException e) {
                MultimediaManager.playSoundEffect("Negative");
                livesLeft--;
                if (livesLeft <= 0) {
                    isGameOver = true;
                }
                else {
                    gameBoard.clearTilesOfType(ItemType.SNAKE);
                    gameBoard.clearTilesOfType(ItemType.SNAKE_HEAD);
                    snake = new PlayableCharacter(this.gameBoard);
                }
            } catch (CollisionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void levelUp() {
        level++;
        score += 100;
//        maximumFoodDelay--;
    }

    /**
     * Handles the food items, including their regeneration and removal.
     */
    private void handleFood() {
        Iterator<FoodManager> iterator = foodList.iterator();
        while (iterator.hasNext()) {
            FoodManager f = iterator.next();
            f.updateFoodStatus();

            if (f.getType() == ItemType.VACANT && f.isSpoiled()) {
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
            newFood.regenerate();
            foodList.add(newFood);
            lastFoodAddedTime = System.currentTimeMillis();
            foodGenerationDelay = random.nextInt(minimumFoodDelay, maximumFoodDelay);
        }
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        this.initialiseGame();
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        gameStarted = true;
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

    public void setGamePaused(boolean gamePaused) {
        isGamePaused = gamePaused;
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
}
