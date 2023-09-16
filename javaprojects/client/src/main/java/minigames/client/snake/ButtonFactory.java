package minigames.client.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonFactory {

    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(36, 192, 196);
    private static final Color HOVER_BACKGROUND_COLOR = new Color(174, 52, 255);
    private static final Color DEFAULT_BORDER_COLOR = new Color(174, 52, 255);
    private static final Color HOVER_BORDER_COLOR = new Color(36, 192, 196);

    /**
     * Creates a styled JButton with the specified properties.
     *
     * @param text      The text to be displayed on the button.
     * @param xPosition The horizontal position where the button should be placed.
     * @param yPosition The vertical position where the button should be placed.
     * @param width     The width of the button.
     * @param height    The height of the button.
     * @param action    The action to be executed when the button is clicked.
     * @return A styled JButton ready to be added to a GUI component.
     */
    public static JButton createButton(String text, int xPosition, int yPosition, int width, int height, ActionListener action) {
        JButton button = new JButton(text);

        // Set font, colors, and properties
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

        // Set the button's size and position
        button.setBounds(xPosition, yPosition, width, height);

        // Add mouse listeners for hover effects
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

        // Attach the provided action listener to the button
        button.addActionListener(action);

        return button;
    }
}
