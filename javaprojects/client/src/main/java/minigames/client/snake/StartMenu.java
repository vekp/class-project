package minigames.client.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * The StartMenu class provides the initial interface for the Snake game.
 * It contains methods for creating and displaying the start menu with options
 * to either start the game or view instructions on how to play.
 */
public class StartMenu {

    // Constants defining dimensions and positions for GUI elements
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int START_BUTTON_Y = 300;
    private static final int HOW_TO_PLAY_BUTTON_Y = 360;

    /**
     * Returns the width of the start menu.
     *
     * @return width of the start menu image.
     */
    public static int getStartMenuWidth() {
        return getImage().getIconWidth();
    }

    /**
     * Returns the height of the start menu.
     *
     * @return height of the start menu image.
     */
    public static int getStartMenuHeight() {
        return getImage().getIconHeight();
    }

    /**
     * Helper method to get the image icon for the background.
     *
     * @return ImageIcon for the background.
     */
    private static ImageIcon getImage() {
        return new ImageIcon(Objects.requireNonNull(StartMenu.class.getResource("/snake/snake_image.png")));
    }

    /**
     * Creates and returns the start menu component for the Snake game.
     * <p>
     * This method sets up the main panel for the start menu, which includes:
     * 1. A background image.
     * 2. A "Start Game" button, which, when clicked, initializes a new game.
     * 3. A "How to Play" button, which, when clicked, displays instructions for playing the game.
     *
     * @return Component (JPanel) containing the start menu UI.
     */
    public static Component startMenu() {
        // Initialize the main panel
        JPanel panel = new JPanel();

        // Get the background image icon
        ImageIcon backgroundIcon = getImage();

        // Set up the background label with the image icon.
        // Using a JLabel to hold the image as it will also act as a container for the buttons.
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());

        // Create and set up the "Start Game" button
        JButton startButton = createButton("Start Game", START_BUTTON_Y);
        startButton.addActionListener(e -> {
            new GameFrame();  // Initialize a new game frame when clicked
        });

        // Create and set up the "How to Play" button
        JButton howToPlayButton = createButton("How to Play", HOW_TO_PLAY_BUTTON_Y);
        howToPlayButton.addActionListener(e -> {
            showHowToPlayDialog(panel);  // Show game instructions when clicked
        });

        // Add the buttons to the background label
        backgroundLabel.add(startButton);
        backgroundLabel.add(howToPlayButton);

        // Add the background label (with buttons) to the main panel
        panel.add(backgroundLabel);

        // Return the fully set up panel
        return panel;
    }


    /**
     * Creates a styled JButton for the start menu with the specified properties.
     * <p>
     * This method generates a JButton with a set style and properties:
     * 1. The button has a specific font and color.
     * 2. The button has a predefined width and its height is passed as an argument.
     * 3. The button's background changes when hovered over.
     * 4. The button has a compound border, combining a line border and empty padding.
     *
     * @param text      The text to be displayed on the button.
     * @param yPosition The vertical position where the button should be placed.
     * @return A styled JButton ready to be added to a GUI component.
     */
    private static JButton createButton(String text, int yPosition) {
        // Calculate the horizontal position for center alignment
        int buttonX = (getStartMenuWidth() - BUTTON_WIDTH) / 2;

        // Create the JButton and set its text
        JButton button = new JButton(text);

        // Set font, colors, and properties
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(174, 52, 255), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setOpaque(true);
        button.setBackground(new Color(36, 192, 196));

        // Set the button's size and position
        button.setBounds(buttonX, yPosition, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Add mouse listeners for hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button background and border when mouse hovers over it
                button.setBackground(new Color(174, 52, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(36, 192, 196), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert button background and border when mouse leaves the button
                button.setBackground(new Color(36, 192, 196));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(174, 52, 255), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }


    /**
     * Displays a modal dialog with instructions on how to play the Snake game.
     *
     * This method creates and shows a JDialog that contains a list of instructions
     * for playing the game. The instructions are:
     * 1. Using arrow keys to control the snake's direction.
     * 2. Eating fruits to grow longer and earn points.
     * 3. Avoiding collisions with walls or the snake's own body.
     * 4. Using the spacebar to toggle pause.
     *
     * The dialog is centered relative to the provided parent component and
     * has a fixed size. It uses a combination of JPanel and JLabel to present
     * the instructions in a formatted manner.
     *
     * @param parent The parent component relative to which the dialog is displayed.
     */
    private static void showHowToPlayDialog(Component parent) {
        // Create the dialog and set properties
        JDialog howToPlayDialog = new JDialog();
        howToPlayDialog.setSize(600, 350);
        howToPlayDialog.setResizable(false);
        howToPlayDialog.setLocationRelativeTo(parent);

        // Create and set up the instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create and set up the title label
        JLabel title = new JLabel("How to Play");
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 0));
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(234, 108, 55));

        // List of instructions
        String[] instructions = {
                "1. Use the arrow keys to control the direction of the snake.",
                "2. Eat the fruit to grow longer and earn points.",
                "3. Avoid running into the walls or yourself.",
                "4. Press the spacebar to pause and unpause the game."
        };

        // Add the title to the instructions panel
        instructionsPanel.add(title);

        // Add each instruction to the instructions panel
        for (String instruction : instructions) {
            JLabel instructionLabel = new JLabel(instruction);
            instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
            instructionLabel.setForeground(new Color(40, 190, 92));
            instructionsPanel.add(instructionLabel);
        }

        // Set the background color of the instructions panel
        instructionsPanel.setBackground(new Color(0, 0, 0));

        // Add the instructions panel to the dialog
        howToPlayDialog.add(instructionsPanel);

        // Display the dialog
        howToPlayDialog.setVisible(true);
    }

}
