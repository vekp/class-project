package minigames.client.snake;

import javax.swing.*;
import java.awt.*;

/**
 * ScorePanel displays the player's score in the Snake game.
 *
 * This panel is intended to be placed at the top of the game display and shows
 * the number of apples the player's snake has eaten. The panel provides methods
 * for updating and rendering the score.
 */
public class ScorePanel extends JPanel {

    // The number of apples the player's snake has eaten.
    private int applesEaten;

    /**
     * Constructor for ScorePanel.
     *
     * Initializes the panel with a preferred size and background color. Sets the
     * initial score (apples eaten) to 0.
     */
    ScorePanel() {
        this.setPreferredSize(new Dimension(GameDisplay.SCREEN_WIDTH, 50)); // Set preferred size
        this.setBackground(Color.black);
        this.applesEaten = 0;
    }

    /**
     * Updates the score displayed on the panel.
     *
     * @param applesEaten Number of apples the player's snake has eaten.
     */
    public void updateScore(int applesEaten) {
        this.applesEaten = applesEaten;
        repaint(); // Redraw the panel to update the score
    }

    /**
     * Paints the score onto the panel.
     *
     * This method is called whenever the panel needs to be redrawn, such as when
     * the score changes. It writes the score in the format "Score: X" where X is
     * the number of apples eaten.
     *
     * @param g The Graphics object to protect.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(36, 192, 196));
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreText = "Score: " + applesEaten;
        int scoreX = 540; // Adjust horizontal position
        int scoreY = 33; // Adjust vertical position
        g.drawString(scoreText, scoreX, scoreY);
    }
}
