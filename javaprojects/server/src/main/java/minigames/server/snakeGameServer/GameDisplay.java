import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.Random;

public class GameDisplay extends JPanel implements ActionListener {

    // Constants for screen size and unit size
    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 800;
    static final int UNIT_SQUARES = 40;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SQUARES * UNIT_SQUARES);

    // Arrays to store snake's coordinates
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int snakeParts = 4; // Initial number of snake parts
    int appleX; // X-coordinate of apple
    int appleY; // Y-coordinate of apple
    int applesEaten; // Number of apples eaten
    char direction = 'R'; // Initial direction (Right)
    boolean running = false; // Game running status
    static final int DELAY = 175; // Delay for the timer
    Timer timer; // Timer to control game loop
    Random random; // Random number generator

    // Constructor
    GameDisplay() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        // setBorder(new LineBorder(new Color(30, 121, 2), 5)); // Border (optional)
        startGame(); // Start the game
    }

    // Start the game
    public void startGame() {
        newApple(); // Place the first apple
        running = true; // Set game to running
        timer = new Timer(DELAY, this); // Create timer
        timer.start(); // Start the timer
    }

    // Paint the game components
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); // Call the draw method
    }

    // Draw game components
    public void draw(Graphics g) {
        if (running) {
            // Draw grid lines
            for (int i = 0; i <= SCREEN_WIDTH / UNIT_SQUARES; i++) {
                g.drawLine(i * UNIT_SQUARES, 0, i * UNIT_SQUARES, SCREEN_HEIGHT);
            }
            for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SQUARES; i++) {
                g.drawLine(0, i * UNIT_SQUARES, SCREEN_WIDTH, i * UNIT_SQUARES);
            }

            // Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SQUARES, UNIT_SQUARES);

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

            // Draw score
            g.setColor(new Color(36, 192, 196));
            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String scoreText = "Score: " + applesEaten;
            int scoreX = (SCREEN_WIDTH - metrics.stringWidth(scoreText)) / 2;
            int scoreY = g.getFont().getSize();
            g.drawString(scoreText, scoreX, scoreY);
        } else {
            gameOver(g); // Draw game over screen
        }
    }

    // Place a new apple randomly
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SQUARES)) * UNIT_SQUARES;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SQUARES)) * UNIT_SQUARES;
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
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String gameOverText = "Game Over";
        int gameOverX = (SCREEN_WIDTH - metrics2.stringWidth(gameOverText)) / 2;
        int gameOverY = SCREEN_HEIGHT / 2;
        g.drawString(gameOverText, gameOverX, gameOverY);

        // Score text
        g.setColor(new Color(36, 192, 196));
        g.setFont(new Font("Arial", Font.BOLD, 20));
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
                case KeyEvent.VK_LEFT:
                    direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    direction = 'D';
                    break;
            }
        }
    }
}
