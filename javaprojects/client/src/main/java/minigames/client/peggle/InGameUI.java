package minigames.client.peggle;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
//import javax.swing.event.MouseInputAdapter;

public class InGameUI extends JPanel {
    final int columns = 1000;
    final int rows = 750;
    final Color background = Color.BLACK;

    // int cell_width = 1; // preferred -NOT CURRENTLY USED
    // int cell_height = 1; // preferred -NOT CURRENTLY USED

    private final boolean[][] grid = new boolean[rows][columns];
    int hoverX = -1;
    int hoverY = -1;

    //Instance of the Cannon class
    private final Cannon cannon;

    InGameUI() {
        setBackground(background);

        JButton returnButton = new JButton("Return to Main Menu");
        ActionListener returnActionListener = e -> PeggleUI.showMainMenu();
        returnButton.addActionListener(returnActionListener);
        add(returnButton, BorderLayout.NORTH);

        // Creates the cannon at position (0, 0)
        cannon = new Cannon(0, 0);

        /* NOT CURRENTLY USED
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
        */

        // A MouseMotionListener can handle movements
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
    }

    void handleHover(MouseEvent e) {
        /* NOT CURRENTLY USED
        int x = columns * e.getX() / getWidth();
        int y = (rows * e.getY() / getHeight());
        if (x == hoverX && y == hoverY) {
            // Don't do repaints unless there was a change
            return;
        }
        hoverY = (rows - 1) - y; // Fix for upside down coordinates
        hoverX = x;
        repaint();
         */

        //Update the angle of the cannon to point towards the mouse position
        int dx = e.getX() - cannon.x;
        int dy = e.getY() - cannon.y;

        double angle = Math.atan2(dy, dx);
        cannon.setAngle(angle);
        repaint();
    }

    // Handles clicks or drags
    void handleDrag(MouseEvent e) {
        /* NOT CURRENTLY USED
        // Be careful, it will keep registering dragging as we go off the window
        if (e.getX() < 0 || e.getX() > getWidth() || e.getY() < 0 || e.getY() > getHeight()) {
            return;
        }
        */

        // Tracks cannon while mouse key pressed+dragged
        handleHover(e);

        /* NOT CURRENTLY USED
        //int x = columns * e.getX() / getWidth();
        //int y = (rows * e.getY() / getHeight());
        //y = (rows - 1) - y; // Fix for upside down coordinates

        // Update the model
        //grid[y][x] = true;
        */

        repaint();
    }

    /* NOT CURRENTLY USED
    private void drawGridSquare(int x, int y, Graphics g) {
        // Translate x and y to panel coordinates
        // Switch y coordinates that usually start from top, to start from bottom.
        x = (int) (getWidth() * x / columns);
        y = (int) (getHeight() * (rows - 1 - y) / rows);
        g.fillRect(x, y, getWidth() / columns, getHeight() / rows);
    }
     */

    // This method can be used to paint a Component however you like
    // It will be called whenever you repaint(), or when the screen is resized etc
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Centers the cannon, even when window is resized
        cannon.setX(getWidth() / 2);

        // Draw the cannon
        cannon.draw(g);
    }
}
