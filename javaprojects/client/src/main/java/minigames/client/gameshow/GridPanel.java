package minigames.client.gameshow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The GridPanel class represents a custom JPanel for displaying an image grid
 * with fading cells. It is used to reveal portions of an image during gameplay.
 *
 * This class extends JPanel and provides functionality to mark and display
 * specific cells of the grid as faded, effectively reducing their opacity.
 *
 * The grid consists of rows and columns, and each cell can be marked as faded
 * or visible. When marked as faded, the cell's opacity is reduced to reveal
 * underlying image content.
 */
class GridPanel extends JPanel {
    private int[][] fadedCells;   // 2D array to store faded cell coordinates
    private ImageIcon imageIcon;  // The image icon to display

    int rows = 10;  // Number of rows in the grid
    int cols = 10;  // Number of columns in the grid

    /**
     * Constructs a new GridPanel instance with the provided image icon.
     *
     * @param imageIcon The ImageIcon to display within the grid.
     */
    public GridPanel(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        fadedCells = new int[rows][cols]; // Initialize the fadedCells array
    }

    /**
     * Marks the specified cell as faded, updating its opacity and triggering a repaint.
     *
     * @param row    The row index of the cell to fade.
     * @param column The column index of the cell to fade.
     */
    public void setFadeCell(int row, int column) {
        if (row >= 0 && row < rows && column >= 0 && column < cols) {
            fadedCells[row][column] = 1;  // Store the faded cell coordinates
            repaint();
        }
    }

    /**
     * Checks if the specified cell is visible (not faded).
     *
     * @param row    The row index of the cell to check.
     * @param column The column index of the cell to check.
     * @return True if the cell is visible; otherwise, false.
     */
    public boolean isCellVisible(int row, int column) {
        if (fadedCells[row][column] == 1) {
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the image
        g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);

        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        // Fill grid cells with grey background and reduced opacity
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellWidth;
                int y = i * cellHeight;
                float opacity = 1;
                if (fadedCells[i][j] == 1) {
                    opacity = 0;
                }
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g.fillRect(x, y, cellWidth, cellHeight);
            }
        }
    }
}
