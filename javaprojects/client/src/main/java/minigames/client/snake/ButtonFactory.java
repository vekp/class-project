package minigames.client.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The ButtonFactory class provides utility methods for creating styled JButtons for the Snake game UI.
 * It offers a centralized way to maintain the consistent look and feel of buttons throughout the game interface.
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
     * @return A styled JButton with the provided properties and added visual effects for mouse hover actions.
     */
    public static JButton createButton(String text, int xPosition, int yPosition, int width, int height, ActionListener action) {
        JButton button = new JButton(text);

        // Set button properties for appearance
        button.setFont(GameConstants.BUTTON_FONT);
        button.setForeground(GameConstants.BUTTON_TEXT_COLOR);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.DEFAULT_BORDER_COLOR, GameConstants.BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING)
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
                        BorderFactory.createLineBorder(GameConstants.HOVER_BORDER_COLOR, GameConstants.BORDER_THICKNESS),
                        BorderFactory.createEmptyBorder(GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(GameConstants.DEFAULT_BACKGROUND_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GameConstants.DEFAULT_BORDER_COLOR, GameConstants.BORDER_THICKNESS),
                        BorderFactory.createEmptyBorder(GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING, GameConstants.BORDER_PADDING)
                ));
            }
        });

        // Attach the provided action listener to handle button clicks
        button.addActionListener(action);

        return button;
    }
}
