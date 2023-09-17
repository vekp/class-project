package minigames.client.snake;

import javax.swing.*;
import java.awt.*;

/**
 * The MainMenuPanel class represents the main menu screen for the Snake game.
 * It is responsible for displaying the main menu background, initializing the multimedia manager
 * for sound playback, and providing methods to manage the background sound.
 */
public class MainMenuPanel extends JPanel {
    // The multimedia manager to manage sounds and images for the game
    private final MultimediaManager multimediaManager;

    /**
     * Constructs the MainMenuPanel. Initializes the layout, size, background,
     * and starts the background sound for the main menu.
     */
    public MainMenuPanel() {
        this.multimediaManager = new MultimediaManager();

        // Setup layout, size, and background
        setLayout(null); // Using null layout to set absolute positions for components
        // Set the preferred size based on the background image dimensions
        setPreferredSize(new Dimension(MultimediaManager.getPhoneBackground().getWidth(), MultimediaManager.getPhoneBackground().getHeight()));
        setupBackground();

        // Play the background sound for the main menu
        multimediaManager.playBackgroundSound("Menu");
    }

    /**
     * Sets up the background image for the main menu.
     */
    private void setupBackground() {
        ImageIcon backgroundIcon = MultimediaManager.getPhoneBackground().getImage();
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight()); // Set the size of the label to the size of the image
        add(backgroundLabel); // This should be the first component added to ensure it's rendered in the background

        // Adding the Snake Logo to the top of the MainMenuPanel
        ImageIcon logoIcon = MultimediaManager.getSnakeLogoResource().getImage();
        JLabel logoLabel = new JLabel(logoIcon);

        // Calculate the x position to center the logo
        int logoX = (backgroundIcon.getIconWidth() - logoIcon.getIconWidth()) / 2;
        logoLabel.setBounds(logoX, 30, logoIcon.getIconWidth(), logoIcon.getIconHeight()); // 10 pixels from the top

        backgroundLabel.add(logoLabel); // Add the logo to the background label so it appears on top of the background
    }

    /**
     * Stops the background sound that is currently playing.
     */
    public void stopBackgroundSound() {
        multimediaManager.stopBackgroundSound();
    }
}
