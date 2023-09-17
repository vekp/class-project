package minigames.client.snake;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
private final MultimediaManager multimediaManager;
    // Constants for button dimensions and positions
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_START_Y = 300;
    private static final int BUTTON_GAP = 60; // Vertical gap between buttons

    public MainMenuPanel() {
        this.multimediaManager = new MultimediaManager();
        // Setup layout, size, and background
        setLayout(null); // We'll set absolute positions for components
        setPreferredSize(new Dimension(MultimediaManager.getStartMenuWidth(), MultimediaManager.getStartMenuHeight())); // Set the preferred size based on the background image dimensions
        setupBackground();
        // Play background sound
        multimediaManager.playBackgroundSound("/snake/menu.wav");
    }

    private void setupBackground() {
        ImageIcon backgroundIcon = MultimediaManager.getImage(); // Assuming you'll provide the directory in this method
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight()); // Use the size of the image
        add(backgroundLabel); // This should be the first component added to ensure it's in the background
    }

    public void stopBackgroundSound(){
        multimediaManager.stopBackgroundSound();
    }

}
