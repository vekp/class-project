package minigames.client.snake;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private int applesEaten;

    ScorePanel() {
        this.setPreferredSize(new Dimension(GameDisplay.SCREEN_WIDTH, 50)); // Set preferred size
        this.setBackground(Color.black);
        this.applesEaten = 0;
    }

    public void updateScore(int applesEaten) {
        this.applesEaten = applesEaten;
        repaint(); // Redraw the panel to update the score
    }

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
