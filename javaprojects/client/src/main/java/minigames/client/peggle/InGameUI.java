import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class InGameUI extends JPanel {
    final int COLUMNS = 500;
    final int ROWS = 500;
    final Color PAINT_COLOUR = Color.GRAY;
    final Color BACKGROUND = Color.BLACK;
    final Color HOVER_COLOUR = Color.YELLOW;
    int CELL_WIDTH = 1; // preferred
    int CELL_HEIGHT = 1; // preferred
    private boolean grid[][] = new boolean[ROWS][COLUMNS];
    int hoverX = -1;
    int hoverY = -1;

    //Instance of the Cannon class
    private Cannon cannon;

    InGameUI() {
        setBackground(BACKGROUND);
        // Creates the cannon at position (250, 50)
        cannon = new Cannon(250, 55);

        // A new MouseInputAdapter can replace default mouse actions
        addMouseListener(new MouseInputAdapter() {
            // There are other methods too, like click (press and release in the same spot)
            // Add mousePressed and mouseReleased methods for dragging
            @Override
            public void mousePressed(MouseEvent e) {
                // We might want to register what was "picked up"
                // In this case, we're just drawing
                handleDrag(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // In some cases, we might want to react to an item being "dropped"
            }
        });

        // A new MouseMotionListener can handle movements
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

    }

    void handleHover(MouseEvent e) {
        int x = COLUMNS * e.getX() / getWidth();
        int y = (ROWS * e.getY() / getHeight());
        if (x == hoverX && y == hoverY) {
            // Don't do repaints unless there was a change
            return;
        }
        hoverY = (ROWS - 1) - y; // Fix for upside down coordinates
        hoverX = x;
        repaint();

        // Ignore mouse movements above a certain Y position
        if (e.getY() < 80) {
            return;
        }

        // Update the angle of the cannon to point towards the mouse position
        double dx = e.getX() - cannon.x;
        double dy = e.getY() - cannon.y;
        double angle = Math.atan2(dy, dx);
        cannon.setAngle(angle);
        repaint();
    }

    // Handles clicks or drags
    void handleDrag(MouseEvent e) {
        // Be careful, it will keep registering dragging as we go off the window
        if (e.getX() < 0 || e.getX() > getWidth() || e.getY() < 0 || e.getY() > getHeight()) {
            return;
        }
        // Track and paint the yellow cursor bit while we're dragging
        handleHover(e);

        int x = COLUMNS * e.getX() / getWidth();
        int y = (ROWS * e.getY() / getHeight());
        y = (ROWS - 1) - y; // Fix for upside down coordinates
        // Update the model
        grid[y][x] = true;
        repaint();
    }

    private void drawGridSquare(int x, int y, Graphics g) {
        // Translate x and y to panel coordinates
        // Switch y coordinates that usually start from top, to start from bottom.
        x = (int) (getWidth() * x / COLUMNS);
        y = (int) (getHeight() * (ROWS - 1 - y) / ROWS);
        g.fillRect(x, y, getWidth() / COLUMNS, getHeight() / ROWS);
    }

    // This method can be used to paint a Component however you like
    // It will be called whenever you repaint(), or when the screen is resized etc
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Update the cannon's position to be the center of the window
        cannon.setX(getWidth() / 2);

        // There is already a background colour set in the constructor
        g.setColor(PAINT_COLOUR);
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (!grid[y][x])
                    continue;
                // Grey squares
                drawGridSquare(x, y, g);
            }
        }

        // Yellow square
        g.setColor(HOVER_COLOUR);
        drawGridSquare(hoverX, hoverY, g);

        // Draw the cannon
        cannon.draw(g2d);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COLUMNS * CELL_WIDTH, ROWS * CELL_HEIGHT);
    }

    // We only have a main method to display the panel on a new JFrame.
    // In the project, we would just put a panel directly on the client window
    public static void main(String[] args) {
        JFrame frame = new JFrame("Title");
        JPanel panel = new InGameUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        panel.repaint();
    }
}