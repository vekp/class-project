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
import minigames.client.snake.*;
public class GameDisplay extends JPanel implements ActionListener {

    // Constants for screen size and unit size
    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 800;
    static final int UNIT_SQUARES = 40;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SQUARES * UNIT_SQUARES);

    private static final String[] FOODS_IMAGE = new String[]{"/snake/orange.png", "/snake/apple.png", "/snake/cherry.png",
            "/snake/watermelon.png", "/snake/orange.png"};
    private boolean paused = false; // New variable to track pause stat

    // Arrays to store snake's coordinates
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int snakeParts = 4; // Initial number of snake parts
    int appleX; // X-coordinate of apple
    int appleY; // Y-coordinate of apple
    int applesEaten; // Number of apples eaten
    int currentDelay = DELAY;
    Image foodImage;
    char direction = 'R'; // Initial direction (Right)
    boolean running = false; // Game running status
    static final int DELAY = 175; // Delay for the timer
    Timer timer; // Timer to control game loop
    Random random; // Random number generator

    private final ScorePanel scorePanel;

    // Constructor
    GameDisplay(ScorePanel scorePanel) {
        random = new Random();
        this.scorePanel = scorePanel;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame(); // Start the game
    }

    // Start the game
    public void startGame() {
        newApple(); // Place the first apple
        running = true; // Set game to running
        timer = new Timer(currentDelay, this); // Create timer with initial currentDelay
        timer.start(); // Start the timer
        foodImage = loadImage(FOODS_IMAGE[0]); // Load the first food image
    }

    // Paint the game components
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); // Call the draw method
    }
    private void drawPaused(Graphics g) {
        if (paused) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String pausedText = "PAUSED";
            int pausedX = (SCREEN_WIDTH - metrics.stringWidth(pausedText)) / 2;
            int pausedY = SCREEN_HEIGHT / 2 + metrics.getAscent(); // Adjust vertical position
            g.drawString(pausedText, pausedX, pausedY);
        }
    }

    // Draw game components
    public void draw(Graphics g) {
        if (running) {
        /* Draw grid lines
        for (int i = 0; i <= SCREEN_WIDTH / UNIT_SQUARES; i++) {
            g.drawLine(i * UNIT_SQUARES, 0, i * UNIT_SQUARES, SCREEN_HEIGHT);
        }
        for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SQUARES; i++) {
            g.drawLine(0, i * UNIT_SQUARES, SCREEN_WIDTH, i * UNIT_SQUARES);
        }*/

            // Draw apple
            g.drawImage(foodImage, appleX, appleY, this);

            // Draw snake parts
            for (int i = 0; i < snakeParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(107, 224, 81));
                    g.fillRoundRect(x[i], y[i], UNIT_SQUARES, UNIT_SQUARES, 20, 20); // Snake head
                } else {
                    g.setColor(new Color(12, 204, 117));
                    g.fillRect(x[i], y[i], UNIT_SQUARES, UNIT_SQUARES); // Snake body parts
                }
            }


        } else {
            if (!paused) { // Only draw game over screen when not paused
                gameOver(g);
            }
        }
        drawPaused(g); // Draw "PAUSED" if game is paused
    }

    // Add this method to your class
    private Image loadImage(String imagePath) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(imagePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Place a new apple randomly
    public void newApple() {
        int randomFoodIndex = random.nextInt(FOODS_IMAGE.length);
        foodImage = loadImage(FOODS_IMAGE[randomFoodIndex]);

        // Generate random coordinates for the apple
        do {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SQUARES - 1)) * UNIT_SQUARES;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SQUARES - 1)) * UNIT_SQUARES;
        } while (snakeOccupies(appleX, appleY)); // Check if snake occupies the generated coordinates
    }

    // Check if the snake occupies the given coordinates
    private boolean snakeOccupies(int x, int y) {
        for (int i = 0; i < snakeParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }

    // Move the snake
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

    // Check if snake has eaten an apple
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

    // Add this method to your GameDisplay class
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

    // Check for collisions
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

    // Draw the game over screen
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

    // Action performed by the timer
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint(); // Redraw the panel
    }

    // Key adapter to handle user input
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