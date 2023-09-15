package minigames.client.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class StartMenu {

    // Define constants for button dimensions and positions
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int START_BUTTON_Y = 300;
    private static final int HOW_TO_PLAY_BUTTON_Y = 360;


    // Getters for start menu width and height
    public static int getStartMenuWidth() {
        return getImage().getIconWidth();
    }

    public static int getStartMenuHeight() {
        return getImage().getIconHeight();
    }

    // Get image icon
    private static ImageIcon getImage() {
        return new ImageIcon(Objects.requireNonNull(StartMenu.class.getResource("/snake/snake_image.png")));
    }

    public static Component startMenu() {
        JPanel panel = new JPanel();

        ImageIcon backgroundIcon = getImage();

        // Because the image is being used as container to hold the buttons, it would be better to use a JPanel instead of a JLabel
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());


        JButton startButton = createButton("Start Game", START_BUTTON_Y);
        startButton.addActionListener(e -> {
            new GameFrame();
        });

        JButton howToPlayButton = createButton("How to Play", HOW_TO_PLAY_BUTTON_Y);
        howToPlayButton.addActionListener(e -> {
            showHowToPlayDialog(panel);
        });

        backgroundLabel.add(startButton);
        backgroundLabel.add(howToPlayButton);

        panel.add(backgroundLabel);

        return panel;
    }


    private static JButton createButton(String text, int yPosition) {
        int buttonX = (getStartMenuWidth() - BUTTON_WIDTH) / 2;

        JButton button = new JButton(text);
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

        // Set button size and position
        button.setBounds(buttonX, yPosition, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Add mouse listeners
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(174, 52, 255));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(36, 192, 196), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(36, 192, 196));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(174, 52, 255), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });

        return button;
    }

    private static void showHowToPlayDialog(Component parent) {
        JDialog howToPlayDialog = new JDialog();
        howToPlayDialog.setSize(600, 350);
        howToPlayDialog.setResizable(false);
        howToPlayDialog.setLocationRelativeTo(parent);

        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("How to Play");
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 0));
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(234, 108, 55)); // Set text color

        String[] instructions = {
                "1. Use the arrow keys to control the direction of the snake.",
                "2. Eat the fruit to grow longer and earn points.",
                "3. Avoid running into the walls or yourself.",
                "4. Press the spacebar to pause and unpause the game."
        };

        instructionsPanel.add(title);

        for (String instruction : instructions) {
            JLabel instructionLabel = new JLabel(instruction);
            instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
            instructionLabel.setForeground(new Color(40, 190, 92));
            instructionsPanel.add(instructionLabel);
        }

        instructionsPanel.setBackground(new Color(0, 0, 0)); // Set background color

        howToPlayDialog.add(instructionsPanel);
        howToPlayDialog.setVisible(true);
    }
}
