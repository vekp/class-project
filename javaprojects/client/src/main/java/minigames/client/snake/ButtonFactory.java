package minigames.client.snake;

import javax.swing.*;

public class ButtonFactory extends JButton {

// TODO Adapt the fancy button to its own class

//    /**
//     * Creates a styled JButton for the start menu with the specified properties.
//     * <p>
//     * This method generates a JButton with a set style and properties:
//     * 1. The button has a specific font and color.
//     * 2. The button has a predefined width and its height is passed as an argument.
//     * 3. The button's background changes when hovered over.
//     * 4. The button has a compound border, combining a line border and empty padding.
//     *
//     * @param text      The text to be displayed on the button.
//     * @param yPosition The vertical position where the button should be placed.
//     * @return A styled JButton ready to be added to a GUI component.
//     */
//    private static JButton createButton(String text, int yPosition) {
//        // Calculate the horizontal position for center alignment
//        int buttonX = (getStartMenuWidth() - BUTTON_WIDTH) / 2;
//
//        // Create the JButton and set its text
//        JButton button = new JButton(text);
//
//        // Set font, colors, and properties
//        button.setFont(new Font("Arial", Font.PLAIN, 24));
//        button.setForeground(Color.WHITE);
//        button.setContentAreaFilled(false);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(174, 52, 255), 2),
//                BorderFactory.createEmptyBorder(10, 20, 10, 20)
//        ));
//        button.setOpaque(true);
//        button.setBackground(new Color(36, 192, 196));
//
//        // Set the button's size and position
//        button.setBounds(buttonX, yPosition, BUTTON_WIDTH, BUTTON_HEIGHT);
//
//        // Add mouse listeners for hover effects
//        button.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                // Change button background and border when mouse hovers over it
//                button.setBackground(new Color(174, 52, 255));
//                button.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(new Color(36, 192, 196), 2),
//                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
//                ));
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                // Revert button background and border when mouse leaves the button
//                button.setBackground(new Color(36, 192, 196));
//                button.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(new Color(174, 52, 255), 2),
//                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
//                ));
//            }
//        });
//
//        return button;
//    }


}
