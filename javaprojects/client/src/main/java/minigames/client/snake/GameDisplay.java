// TODO: Rename to game logic/game mechanics and keep all game play methods here, place all relevant gui bits in their classes

package minigames.client.snake;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Objects;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import javax.imageio.ImageIO;


/**
 * GameDisplay represents the main gameplay area for the Snake game.
 * It handles the display, mechanics, and interactions of the game,
 * including the movement of the snake, apple placement, and game over conditions.
 */
public class GameDisplay extends JPanel implements ActionListener {

    // Constants defining the dimensions of the game screen.
    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 800;

    // Constant representing the size of each unit or square in the game.
    static final int UNIT_SQUARES = 40;

    // Constant representing the total number of units or squares in the game area.
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SQUARES * UNIT_SQUARES);

    // Array containing the paths of different food images for the game.
    private static final String[] FOODS_IMAGE = {
            "/snake/orange.png", "/snake/apple.png", "/snake/cherry.png",
            "/snake/watermelon.png", "/snake/orange.png"
    };

    // Flag to determine if the game is paused.
    private boolean paused = false;

    // Arrays to store the x and y coordinates of the snake's body parts.
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    // Variable representing the initial number of snake parts.
    int snakeParts = 4;

    // Variables to store the x and y coordinates of the apple.
    int appleX;
    int appleY;

    // Variable to keep track of the number of apples eaten by the snake.
    int applesEaten;

    // Variable for controlling the game loop speed.
    int currentDelay = DELAY;

    // Image object to store the current food's image.
    Image foodImage;

    // Variable to keep track of the snake's current direction.
    char direction = 'R';

    // Flag to determine if the game is currently running.
    boolean running = false;

    // Constant defining the initial delay for the game loop timer.
    static final int DELAY = 175;

    // Timer to control the game loop and enforce game mechanics at regular intervals.
    Timer timer;

    // Random number generator used for placing the apple at random positions.
    Random random;

    // Reference to the ScorePanel to update and display the player's score.
    private final ScorePanel scorePanel;


    /**
     * Constructs a new GameDisplay instance, initializing the gameplay area
     * and setting up the necessary configurations and listeners.
     *
     * @param scorePanel The ScorePanel instance used to display and update the player's score.
     */
    GameDisplay(ScorePanel scorePanel) {
        // Initialize the random number generator.
        random = new Random();

        // Set the provided ScorePanel instance.
        this.scorePanel = scorePanel;

        // Set the preferred size for the game display area.
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // Set the background color of the game area to black.
        this.setBackground(Color.black);

        // Make the game display area focusable to capture key events.
        this.setFocusable(true);

        // Add a key listener to capture and process user input.
        this.addKeyListener(new MyKeyAdapter());

        // Start the game mechanics and display.
        startGame();
    }


    /**
     * Initializes and starts the game mechanics.
     *
     * This method performs the following tasks:
     * 1. Places the first apple on the game board using the newApple() method.
     * 2. Sets the game status to running.
     * 3. Creates and starts a timer to control the game loop.
     * 4. Loads the initial food image for the game.
     */
    public void startGame() {
        newApple(); // Place the first apple
        running = true; // Set game to running
        timer = new Timer(currentDelay, this); // Create timer with initial currentDelay
        timer.start(); // Start the timer
        foodImage = loadImage(FOODS_IMAGE[0]); // Load the first food image
    }


    /**
     * Overrides the paintComponent method to render the game components.
     *
     * @param g The Graphics object used for drawing.
     */
    @Override
    public void paintComponent(Graphics g) {
        // Call the parent class's paintComponent method for default painting.
        super.paintComponent(g);

        // Draw the game components.
        draw(g);
    }

    /**
     * Draws the "PAUSED" text on the screen when the game is paused.
     *
     * This method checks if the game is in a paused state. If so, it sets the font
     * and color for the text, calculates the appropriate position to center the "PAUSED"
     * text on the screen, and then draws the text.
     *
     * @param g The Graphics object used for drawing.
     */
    private void drawPaused(Graphics g) {
        if (paused) {
            // Set the color and font for the "PAUSED" text.
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));

            // Retrieve font metrics to calculate the position for centering the text.
            FontMetrics metrics = getFontMetrics(g.getFont());

            // Calculate the X and Y coordinates for centering the "PAUSED" text.
            String pausedText = "PAUSED";
            int pausedX = (SCREEN_WIDTH - metrics.stringWidth(pausedText)) / 2;
            int pausedY = SCREEN_HEIGHT / 2 + metrics.getAscent();

            // Draw the "PAUSED" text on the screen.
            g.drawString(pausedText, pausedX, pausedY);
        }
    }


    /**
     * Draws the game components on the screen.
     *
     * This method is responsible for rendering the main game components, including
     * the apple and the snake. If the game is running, it draws the apple image and
     * all the snake parts on the screen. The snake's head is visually distinguished
     * from its body. If the game is not running and is not paused, it renders the
     * game over screen.
     *
     * @param g The Graphics object used for drawing.
     */
    public void draw(Graphics g) {
        if (running) {
            // Code to draw grid lines which has been commented out. This can be
            // uncommented if grid lines are desired for debugging or visual aesthetics.
        /*
        for (int i = 0; i <= SCREEN_WIDTH / UNIT_SQUARES; i++) {
            g.drawLine(i * UNIT_SQUARES, 0, i * UNIT_SQUARES, SCREEN_HEIGHT);
        }
        for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SQUARES; i++) {
            g.drawLine(0, i * UNIT_SQUARES, SCREEN_WIDTH, i * UNIT_SQUARES);
        }
        */

            // Draw the apple using the foodImage at its current position.
            g.drawImage(foodImage, appleX, appleY, this);

            // Loop through the snake parts and draw them.
            for (int i = 0; i < snakeParts; i++) {
                if (i == 0) {
                    // Set color for snake's head and draw it with rounded corners.
                    g.setColor(new Color(107, 224, 81));
                    g.fillRoundRect(x[i], y[i], UNIT_SQUARES, UNIT_SQUARES, 20, 20);
                } else {
                    // Set color for snake's body and draw rectangular body parts.
                    g.setColor(new Color(12, 204, 117));
                    g.fillRect(x[i], y[i], UNIT_SQUARES, UNIT_SQUARES);
                }
            }
        } else {
            // If the game is not running and it's not paused, render the game over screen.
            if (!paused) {
                gameOver(g);
            }
        }

        // Render the "PAUSED" text on screen if the game is paused.
        drawPaused(g);
    }


    /**
     * Loads an image from the specified path.
     *
     * This method reads and returns an image from the provided path. If the image
     * fails to load, an exception is caught and printed, and the method returns null.
     *
     * @param imagePath The relative path to the image resource.
     * @return The loaded image or null if the loading fails.
     */
    private Image loadImage(String imagePath) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(imagePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Places a new apple on the game screen at a random location.
     *
     * This method randomly selects an apple image from the FOODS_IMAGE array and
     * generates random x and y coordinates for the apple's placement. If the generated
     * coordinates happen to be occupied by the snake, the coordinates are regenerated.
     */
    public void newApple() {
        int randomFoodIndex = random.nextInt(FOODS_IMAGE.length);
        foodImage = loadImage(FOODS_IMAGE[randomFoodIndex]);

        // Generate random coordinates for the apple
        do {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SQUARES - 1)) * UNIT_SQUARES;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SQUARES - 1)) * UNIT_SQUARES;
        } while (snakeOccupies(appleX, appleY)); // Check if snake occupies the generated coordinates
    }

    /**
     * Determines if the snake occupies the given x and y coordinates.
     *
     * This method iterates over the snake's body parts and checks if any part is
     * present at the provided x and y coordinates. If any part of the snake is found
     * at the provided coordinates, the method returns true; otherwise, it returns false.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return True if any part of the snake occupies the given coordinates, false otherwise.
     */
    private boolean snakeOccupies(int x, int y) {
        for (int i = 0; i < snakeParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the snake in the game.
     *
     * This method updates the position of the snake's body parts by moving them to the
     * position of the previous part. The head of the snake is then moved based on the
     * current direction of movement ('U' for up, 'D' for down, 'L' for left, and 'R' for right).
     */
    public void move() {
        // Move body parts
        for (int i = snakeParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Move head based on direction
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SQUARES;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SQUARES;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SQUARES;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SQUARES;
                break;
        }
    }

    /**
     * Checks if the snake's head has collided with an apple.
     *
     * If the snake's head collides with the apple, the snake's length increases,
     * the count of apples eaten is incremented, a new apple is placed in the game,
     * the game's timer delay is reduced to speed up the snake, and a sound effect
     * is played. The score is then updated using the provided ScorePanel.
     */
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            snakeParts++;
            applesEaten++;
            newApple();
            currentDelay -= 5; // Reduce the currentDelay by 5 milliseconds
            timer.setDelay(currentDelay); // Update the timer delay
            playEatAppleSound(); // Play the sound effect
            scorePanel.updateScore(applesEaten);
        }
    }

    /**
     * Plays the sound effect of the snake eating an apple.
     *
     * This method attempts to load and play the "eat apple" sound effect. If there
     * are issues loading or playing the sound, an exception is caught and printed.
     */
    private void playEatAppleSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(getClass().getResourceAsStream("/snake/eat_apple.wav")));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks for potential collisions in the game.
     *
     * This method checks if the snake's head has collided with its body or with the game borders.
     * If a collision is detected, the game status is set to "not running," and the game timer stops.
     */
    public void checkCollisions() {
        // Check if head collides with body
        for (int i = snakeParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // Game over
            }
        }
        // Check if head touches borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false; // Game over
        }

        if (!running) {
            timer.stop();
        }
    }

    /**
     * Renders the game over screen.
     *
     * When the game is over, this method draws the "Game Over" text in the center of the game area
     * and displays the final score below it.
     *
     * @param g The Graphics object used for rendering.
     */
    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 100));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String gameOverText = "Game Over";
        int gameOverX = (SCREEN_WIDTH - metrics2.stringWidth(gameOverText)) / 2;
        int gameOverY = SCREEN_HEIGHT / 2;
        g.drawString(gameOverText, gameOverX, gameOverY);

        // Score text
        g.setColor(new Color(36, 192, 196));
        g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 35));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String scoreText = "Score: " + applesEaten;
        int scoreX = (SCREEN_WIDTH - metrics1.stringWidth(scoreText)) / 2;
        int scoreY = gameOverY + metrics2.getHeight() + metrics1.getAscent(); // Adjust vertical position
        g.drawString(scoreText, scoreX, scoreY);
    }

    /**
     * Handles the main game loop, driven by the timer.
     *
     * If the game is running, this method progresses the game by moving the snake,
     * checking for apple consumption, and verifying collisions.
     * Regardless of the game's running status, the game display is repainted.
     *
     * @param e The action event from the timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint(); // Redraw the panel
    }

    /**
     * A custom KeyAdapter class to handle user input for the game.
     *
     * This class captures key presses to control the snake's direction and to pause or unpause the game.
     */
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
                case KeyEvent.VK_SPACE -> {
                    if (running) {
                        timer.stop(); // Pause the game
                        running = false;
                        paused = true; // Set pause state
                    } else {
                        timer.start(); // Unpause the game
                        running = true;
                        paused = false; // Reset pause state
                    }
                    repaint(); // Repaint the panel to show "PAUSED" or update the game screen
                }
            }
        }
    }
}