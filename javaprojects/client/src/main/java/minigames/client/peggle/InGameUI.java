package minigames.client.peggle;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class InGameUI extends JPanel {
    final int columns = 1820;
    final int rows = 980;
    final Color background = Color.BLACK;
    private Timer gameLoopTimer;
    private int delay = 8; // approx 120 FPS (1000 / 60 = 8.3 milliseconds per frame, delay shown in milliseconds)

    // Constants for ball parameters
    private ArrayList<Ball> balls = new ArrayList<>();
    private static final int ballSize = 5; // Adjust as needed
    private static final double ballSpeed = 5.0; // Adjust as needed

    // Create a list of bricks
    ArrayList<Brick> bricks = new ArrayList<>();
    private final Cannon cannon;



    InGameUI(GameContainer gameContainer) {
        gameLoopTimer = new Timer(delay, e -> gameLoop(gameContainer));
        gameLoopTimer.start();

        setBackground(background);

        cannon = new Cannon(0, 0);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                launchBall(e);
            }
        });

        // Used for cannon to track mouse location if mouse moved or dragged
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleDrag(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e);
            }
        });
        // Initialize bricks array
        initBricks();
    }


    public void launchBall(MouseEvent e) {

        float angle = (float) Math.atan2(e.getY() - cannon.getY(), e.getX() - cannon.getX());

        Ball ball = new Ball(cannon.getX(), cannon.getY(), true, 10);
        ball.shoot(angle, 20);  // SHOOTING_SPEED is a constant that you can adjust

        // Now add the ball to the list of balls or wherever you're storing them
        balls.add(ball);
    }


    private void gameLoop(GameContainer gameContainer) {
        updateGame(gameContainer);
        repaint();
    }

    public void updateGame(GameContainer gameContainer) {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            Rectangle currentBallBounds = new Rectangle(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());

            for (Brick brick : bricks) {
                if (!brick.isHit() && brick.checkCollision(ball)) {
                    if (currentBallBounds.intersects(new Rectangle(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight()))) {
                        ball.bounceOffObject();
                        gameContainer.incrementScore(10);
                    }
                    brick.isHit = true;
                }
            }

            ball.updateBall(0, columns, 0, rows);

            if (!ball.active) {
                balls.remove(i);
                i--;
            }
        }
    }


    void handleHover(MouseEvent e) {
        // Update the angle of the cannon to point towards the mouse position
        int dx = e.getX() - cannon.getX();
        int dy = e.getY() - cannon.getY();
        double angle = Math.atan2(dy, dx);
        cannon.setAngle(angle);
        repaint();
    }

    void handleDrag(MouseEvent e) {
        // Tracks cannon while mouse key pressed+dragged
        handleHover(e);
        repaint();
    }

    private void initBricks() {
        int brickWidth = 50;
        int brickHeight = 20;
        int xOffset = 50;
        int yOffset = 100;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 20; col++) {
                int x = xOffset + (col * (brickWidth + 10));
                int y = yOffset + (row * (brickHeight + 10));
                bricks.add(new Brick(x, y, brickWidth, brickHeight));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Centers the cannon, even when window is resized
        cannon.setX(getWidth() / 2);

        // Draw the cannon
        cannon.draw(g);

        for (Ball ball : balls) {
            ball.drawBall(g);
        }
        // Drawing the bricks
        for (Brick brick : bricks) {
            brick.draw(g);
        }
    }




}