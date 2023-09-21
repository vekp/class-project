package minigames.client.snake;

import javax.swing.*;

/**
 * The BackgroundContainer class provides a standardized background with a logo for the Snake game UI.
 */
public class BackgroundContainer extends JLabel {

    /**
     * Constructs the BackgroundContainer and sets up the background with a logo.
     */
    public BackgroundContainer() {
        setupBackgroundWithLogo();
    }

    /**
     * Configures the background image and positions the logo centrally.
     */
    private void setupBackgroundWithLogo() {
        // Set up the background image
        ImageIcon backgroundIcon = MultimediaManager.getPhoneBackground().getImageResource();
        this.setIcon(backgroundIcon);
        this.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        this.setLayout(null);

        // Create and position the logo label
        ImageIcon logoIcon = MultimediaManager.getSnakeLogoResource().getImageResource();
        int logoWidth = logoIcon.getIconWidth();
        int logoHeight = logoIcon.getIconHeight();

        // Calculate the position of the logo
        int logoX = (this.getWidth() - logoWidth) / 2;
        int logoY = GameConstants.LOGO_Y;

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(logoX, logoY, logoWidth, logoHeight);
        this.add(logoLabel);
    }
}
