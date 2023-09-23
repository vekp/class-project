package minigames.client.snake;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The ButtonFactory class provides utility methods for creating styled JButtons for the Snake
 * game UI.
 * It offers a centralized way to maintain the consistent look and feel of buttons throughout the
 * game interface.
 */
public class ButtonFactory {

    /**
     * Creates a styled JButton with the specified properties.
     *
     * @param text      The text to be displayed on the button.
     * @param xPosition The x-coordinate for the top-left corner of the button.
     * @param yPosition The y-coordinate for the top-left corner of the button.
     * @param width     The width of the button.
     * @param height    The height of the button.
     * @param action    The action to be executed when the button is clicked.
     * @return A styled JButton with the provided properties and added visual effects for mouse
     * hover actions.
     */
    public static JButton createButton(String text, int xPosition, int yPosition, int width,
                                       int height, ActionListener action) {
        JButton button = new JButton(text);

        // Set button properties for appearance
        button.setFont(GameConstants.BUTTON_FONT);
        button.setForeground(GameConstants.BUTTON_TEXT_COLOR);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        GameConstants.DEFAULT_BORDER_COLOR, GameConstants.BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(
                        GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING,
                        GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING
                                               )
                                                           ));
        button.setOpaque(true);
        button.setBackground(GameConstants.DEFAULT_BACKGROUND_COLOR);

        // Position and size the button based on provided parameters
        button.setBounds(xPosition, yPosition, width, height);

        // Add mouse listeners to change button appearance on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(GameConstants.HOVER_BACKGROUND_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                GameConstants.HOVER_BORDER_COLOR, GameConstants.BORDER_THICKNESS),
                        BorderFactory.createEmptyBorder(
                                GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING,
                                GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING
                                                       )
                                                                   ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(GameConstants.DEFAULT_BACKGROUND_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                GameConstants.DEFAULT_BORDER_COLOR, GameConstants.BORDER_THICKNESS),
                        BorderFactory.createEmptyBorder(
                                GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING,
                                GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING
                                                       )
                                                                   ));
            }
        });

        // Attach the provided action listener to handle button clicks
        button.addActionListener(action);

        return button;
    }

    /**
     * Sets up a "Return to Main Menu" button with default position (centered) and name.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param container     The container (like a JPanel) where the button will be added.
     * @return JButton The created button.
     */
    public static JButton setupReturnButton(MainMenuPanel.PanelSwitcher panelSwitcher,
                                            JComponent container) {
        int buttonX = (MultimediaManager.getPhoneBackground()
                                        .getImageResourceWidth() - GameConstants.BUTTON_WIDTH) / 2;
        return setupReturnButton(
                panelSwitcher, container, buttonX, GameConstants.RETURN_BUTTON_Y,
                GameConstants.MAIN_MENU_PANEL
                                );
    }

    /**
     * Sets up a button with custom position and name.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param container     The container (like a JPanel) where the button will be added.
     * @param x             The x-coordinate of the button.
     * @param y             The y-coordinate of the button.
     * @param buttonName    The custom name for the button.
     * @return JButton The created button.
     */
    public static JButton setupReturnButton(MainMenuPanel.PanelSwitcher panelSwitcher,
                                            JComponent container, int x, int y, String buttonName) {
        ActionListener action = e -> panelSwitcher.switchToPanel(GameConstants.MAIN_MENU_PANEL);
        JButton returnButton = createButton(
                buttonName, x, y, GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT, action);
        container.add(returnButton);
        return returnButton;
    }

    /**
     * Sets up a button with custom position, name, and background sound.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param container     The container (like a JPanel) where the button will be added.
     * @param x             The x-coordinate of the button.
     * @param y             The y-coordinate of the button.
     * @param buttonName    The custom name for the button.
     * @param soundName     The name of the background sound to play when the button is clicked.
     */
    public static void setupReturnButton(
            MainMenuPanel.PanelSwitcher panelSwitcher,
            JComponent container, int x, int y, String buttonName, MusicChoice soundName
                                        ) {
        ActionListener action = e -> {
            panelSwitcher.switchToPanel(GameConstants.MAIN_MENU_PANEL);
            MultimediaManager.playBackgroundSound(soundName);
        };

        JButton returnButton = createButton(
                buttonName, x, y, GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT, action);
        container.add(returnButton);
    }
}
