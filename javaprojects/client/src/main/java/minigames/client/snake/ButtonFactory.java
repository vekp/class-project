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

    // Default background color for the button.
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(18, 96, 98);

    // Background color for the button when the mouse hovers over it.
    private static final Color HOVER_BACKGROUND_COLOR = new Color(87, 26, 128);

    // Default border color for the button.
    private static final Color DEFAULT_BORDER_COLOR = new Color(18, 144, 147);

    // Border color for the button when the mouse hovers over it.
    private static final Color HOVER_BORDER_COLOR = new Color(87, 26, 128);

    /**
     * Creates a styled JButton with the specified properties.
     *
     * @param text      The text to be displayed on the button.
     * @param xPosition The x-coordinate for the top-left corner of the button.
     * @param yPosition The y-coordinate for the top-left corner of the button.
     * @param width     The width of the button.
     * @param height    The height of the button.
     * @param action    The action to be executed when the button is clicked.
     *
     * @return A styled JButton with the provided properties and added visual effects for mouse hover actions.
     */
    public static JButton createButton(String text, int xPosition, int yPosition, int width, int height, ActionListener action) {
        JButton button = new JButton(text);

        // Set button properties for appearance
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DEFAULT_BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setOpaque(true);
        button.setBackground(DEFAULT_BACKGROUND_COLOR);

        // Position and size the button based on provided parameters
        button.setBounds(xPosition, yPosition, width, height);

        // Add mouse listeners to change button appearance on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_BACKGROUND_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HOVER_BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DEFAULT_BACKGROUND_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DEFAULT_BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        // Attach the provided action listener to handle button clicks
        button.addActionListener(action);

        return button;
    }
}
