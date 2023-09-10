package minigames.client.snake;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

/**
 * GUI class to represent the Snake game using Swing.
 */
public class GUI extends JPanel implements ActionListener {

    // Screen size and unit size constants
    private static final int SCREEN_WIDTH = 1200;
    private static final int SCREEN_HEIGHT = 800;
    private static final int UNIT_SIZE = 40;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    // Image paths for food
    private static final String[] FOODS_IMAGE = {"/main/resources/img/orange.png", "/main/resources/img/apple.png",
            "/main/resources/img/cherry.png", "/main/resources/img/watermelon.png", "/main/resources/img/orange.png"};


    private static final int INITIAL_DELAY = 175;

    // Snake's coordinates and other attributes
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int snakeParts = 4;
    private int appleX;
    private int appleY;
    private int applesEaten;
    private int currentDelay = INITIAL_DELAY;
    private Image foodImage;

    private char direction = 'R';
    private boolean running;
    private final Timer timer;
    private int countdownTimer = 60;

    private final Random random;
    private Clip eatSoundClip;

    /**
     * Constructs a new GUI and initializes the game board.
     */
    public GUI() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        timer = new Timer(currentDelay, this);
        startGame();
        loadSounds();
        startCountdown(); // Start the countdown timer
    }

    /**
     * Initializes the game state and starts the timer.
     */
    private void startGame() {
        newApple();
        running = true;
        timer.start();
        foodImage = loadImage(FOODS_IMAGE[0]);
    }

    /**
     * Load sound effects.
     */
    private void loadSounds() {
        try {
            AudioInputStream eatSoundInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/main/resources/eat_apple.wav"));
            eatSoundClip = AudioSystem.getClip();
            eatSoundClip.open(eatSoundInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load an image from the given path.
     *
     * @param imagePath Path to the image
     * @return Loaded image
     */

    private Image loadImage(String imagePath) {
        try {
            return ImageIO.read(getClass().getResource(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Paint the GUI.
     *
     * @param g Graphics object for painting.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        drawCountdownTimer(g);
    }

    /**
     * Draw the countdown timer.
     *
     * @param g Graphics object for drawing.
     */
    private void drawCountdownTimer(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String timerText = "Time: " + countdownTimer + "s";
        FontMetrics metrics = getFontMetrics(g.getFont());
        int x = SCREEN_WIDTH - metrics.stringWidth(timerText) - 10; // Adjusted position
        int y = g.getFont().getSize() + 10; // Adjusted position
        g.drawString(timerText, x, y);
    }

    /**
     * Start the countdown timer.
     */
    private void startCountdown() {
        Timer countdownTmer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdownTimer > 0) {
                    countdownTimer--; // Decrement the countdown timer
                    repaint();
                }
            }
        });
        countdownTmer.start();
    }

    /**
     * Draw the game elements.
     *
     * @param g Graphics object for drawing.
     */
    private void draw(Graphics g) {
        if (running) {
            // Draw the apple
            g.drawImage(foodImage, appleX, appleY, UNIT_SIZE, UNIT_SIZE, this);

            // Draw the snake
            for (int i = 0; i < snakeParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(0x00FF00));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw the score
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten,
                    (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    /**
     * Action performed by the timer.
     *
     * @param e ActionEvent object.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
            checkApple();
        }
        repaint();

        // Update countdown timer
        if (countdownTimer > 0) {
            countdownTimer--;
        }
    }

    /**
     * Move the snake based on its current direction.
     */
    private void move() {
        // Check if the snake's head is in row 1, adjust direction accordingly
        if (y[0] <= UNIT_SIZE) {
            if (direction == 'U') {
                direction = 'D';
            }
        }

        // Move the body
        System.arraycopy(x, 0, x, 1, snakeParts);
        System.arraycopy(y, 0, y, 1, snakeParts);

        // Move the head
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    /**
     * Check for collisions with the wall or the snake's body.
     */
    private void checkCollision() {
        // Check if head collides with body
        for (int i = snakeParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head collides with the left wall
        if (x[0] < 0) {
            running = false;
        }

        // Check if head collides with the right wall
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }

        // Check if head collides with the top wall
        if (y[0] < 0) {
            running = false;
        }

        // Check if head collides with the bottom wall
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    /**
     * Check if the snake has eaten the apple.
     */
    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            snakeParts++;
            applesEaten++;
            newApple();
            // Increase countdown timer by 10 seconds
            countdownTimer += 10;
            // Play eat sound
            playEatSound();
        }
    }

    /**
     * Generate a new apple.
     */
    private void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }
    /**
     * Play the eat sound effect.
     */
    private void playEatSound() {
        if (eatSoundClip != null) {
            eatSoundClip.setFramePosition(0); // Rewind to the beginning
            eatSoundClip.start();
        }
    }

    /**
     * Draw the game over screen.
     *
     * @param g Graphics object for drawing.
     */
    private void gameOver(Graphics g) {
        String msg = "Game Over";
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(msg, (SCREEN_WIDTH - metrics.stringWidth(msg)) / 2, SCREEN_HEIGHT / 2);
    }

    /**
     * Inner class for handling key events.
     */
    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
