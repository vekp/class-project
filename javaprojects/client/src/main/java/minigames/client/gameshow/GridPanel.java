package minigames.client.gameshow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


class GridPanel extends JPanel {
    private int[][] fadedCells;   // 2D array to store faded cell coordinates
    private ImageIcon imageIcon;  // The image icon to display

    int rows = 10;  // Number of rows in the grid
    int cols = 10;  // Number of columns in the grid

    public GridPanel(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        fadedCells = new int[rows][cols]; // Initialize the fadedCells array
    }

    public void setFadeCell(int row, int column) {
        if (row >= 0 && row < rows && column >= 0 && column < cols) {
            fadedCells[row][column] = 1;  // Store the faded cell coordinates
            repaint();
        }
    }

    public boolean isCellVisible(int row, int column) {
        if (fadedCells[row][column] == 1) {
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
