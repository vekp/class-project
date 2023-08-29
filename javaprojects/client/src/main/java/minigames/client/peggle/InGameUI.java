import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class InGameUI extends JPanel {
    final int columns = 1000;
    final int rows = 750;
    final Color background = Color.BLACK;
    private final boolean[][] grid = new boolean[rows][columns];
    private final Cannon cannon;
    private Timer gameLoopTimer;
    private int delay = 16; // approx 60 FPS (1000 / 60 = 16.67 milliseconds per frame, delay shown in milliseconds)

    // Constants for ball parameters
    private ArrayList<Ball> balls = new ArrayList<>();
    private static final int ballSize = 5; // Adjust as needed
    private static final double ballSpeed = 5.0; // Adjust as needed

    //MM added - Create a list of bricks
    ArrayList<Brick> bricks = new ArrayList<>();

    InGameUI() {

        gameLoopTimer = new Timer(delay, e -> gameLoop());
        gameLoopTimer.start();


        setBackground(background);

        JButton returnButton = new JButton("Return to Main Menu");
        ActionListener returnActionListener = e -> PeggleUI.showMainMenu();
        returnButton.addActionListener(returnActionListener);
        add(returnButton, BorderLayout.NORTH);

        // Creates the cannon at position (0, 0)
        cannon = new Cannon(0, 0);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                launchBall();
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
        //MM added - Initialize bricks array
        initBricks();

    }

    private void launchBall() {
        // Calculate velocity based on cannon's angle
        float xVelocity = (float) -(ballSpeed * Math.cos(cannon.angle));
        float yVelocity = (float) (ballSpeed * Math.sin(cannon.angle));

        // TODO fix ball parameters
        Ball newBall = new Ball(cannon.x, cannon.y, (float)cannon.angle, (float) cannon.angle, xVelocity, yVelocity,true,1,10 );
        balls.add(newBall);
    }

    private void gameLoop() {
        updateGame();
        repaint();

    }

    public void updateGame() {
        // Update each ball's position and check for collisions
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            ball.updateBall(0, getWidth(), getHeight());


            //TODO fix isActive
            if (!true) {
                balls.remove(i);
                i--; // Account for shifting elements
            }
        }

        // Repaint to show changes
        repaint();
    }

    void handleHover(MouseEvent e) {

        //Update the angle of the cannon to point towards the mouse position
        int dx = e.getX() - cannon.x;
        int dy = e.getY() - cannon.y;

        double angle = Math.atan2(dy, dx);
        cannon.setAngle(angle);
        repaint();
    }

    void handleDrag(MouseEvent e) {
        // Tracks cannon while mouse key pressed+dragged
        handleHover(e);

        repaint();
    }

    /* MM added -  initializes the bricks. Sets up a grid of bricks,
specifying their position X & Y, width, and height.
Then, adds these brick objects to an ArrayList called bricks, a field of this class*/
    private void initBricks() {
        int brickWidth = 50;
        int brickHeight = 20;
        int xOffset = 50;
        int yOffset = 100;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
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
        //MM Added - drawing the bricks
        for (Brick brick : bricks) {
            brick.draw(g);
    }

}
