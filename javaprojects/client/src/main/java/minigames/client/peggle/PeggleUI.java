package minigames.client.peggle;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * PeggleUI class that provides a user interface for the Peggle mini-game
 */
public class PeggleUI {
    private static final String gameName = "Peggle MiniGame";
    // File paths for images
    private static final String iconFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/gameIcon.png";
    private static final String backgroundFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/menuBG.png";
    private static final String startButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/startBTN.png";
    private static final String exitButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/exitBTN.png";
    private static final String instructionsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/instructionsBTN.png";
    private static final String achievementsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/achievementsBTN.png";
    private static final String leaderboardButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/leaderboardBTN.png";
    private static final String settingsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/settingsBTN.png";
    private static JFrame mainWindow = null;

    // Main function initialises the UI and starts the game
    public static void main(String[] args) {
        mainWindow = new JFrame(gameName);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setPreferredSize(new Dimension(500, 850));
        mainWindow.setIconImage(new ImageIcon(iconFilePath).getImage());

        JPanel titleScreen = generateMainMenu();
        mainWindow.setContentPane(titleScreen);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }

    // Adds a panel to the background with an anchor
    private static void addPanelToBackground(JLabel background, JPanel panel, int anchor) {
        GridBagConstraints backgroundConstraints = new GridBagConstraints();
        backgroundConstraints.gridx = 0;
        backgroundConstraints.gridy = 0;
        backgroundConstraints.weightx = 1;
        backgroundConstraints.weighty = 1;
        backgroundConstraints.anchor = anchor;
        background.add(panel, backgroundConstraints);
    }

    // Generates the title screen main menu of the game
    private static JPanel generateMainMenu() {
        JPanel titleScreen = new JPanel(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon(backgroundFilePath));
        titleScreen.add(background, BorderLayout.CENTER);
        background.setLayout(new GridBagLayout());

        JPanel buttonsPanel = generateMainButtonsPanel();
        addPanelToBackground(background, buttonsPanel, GridBagConstraints.CENTER);

        JPanel topCenterButtonsPanel = generateTopCenterButtonsPanel();
        addPanelToBackground(background, topCenterButtonsPanel, GridBagConstraints.NORTH);

        return titleScreen;
    }

    // Generates the main menu buttons (start, exit)
    private static JPanel generateMainButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)); // Vertical alignment

        JButton startButton = createImageButton(startButtonFilePath, e -> startGame(), 0.5); //scaling
        JButton exitButton = createImageButton(exitButtonFilePath, e -> System.exit(0), 0.5); //scaling

        // Add buttons
        buttonsPanel.add(startButton);
        buttonsPanel.add(Box.createVerticalStrut(75)); // Vertical spacing between buttons
        buttonsPanel.add(exitButton);

        return buttonsPanel;
    }

    // Generate the top center buttons (achievements, leaderboard, instructions, settings)
    private static JPanel generateTopCenterButtonsPanel() {
        JPanel topRightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topRightButtonsPanel.setOpaque(false);

        topRightButtonsPanel.add(createImageButton(achievementsButtonFilePath, e -> checkAchievements(), 0.3));
        topRightButtonsPanel.add(createImageButton(leaderboardButtonFilePath, e -> checkLeaderboard(), 0.3));
        topRightButtonsPanel.add(createImageButton(instructionsButtonFilePath, e -> checkInstructions(), 0.3));
        topRightButtonsPanel.add(createImageButton(settingsButtonFilePath, e -> checkSettings(), 0.3));

        return topRightButtonsPanel;
    }

    // Creates a button with an image, associated action and scaling
    private static JButton createImageButton(String imagePath, ActionListener action, double scalingFactor) {
        ImageIcon icon = new ImageIcon(imagePath);
        int scaledWidth = (int) (icon.getIconWidth() * scalingFactor);
        int scaledHeight = (int) (icon.getIconHeight() * scalingFactor);
        Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(action);
        return button;
    }

    public static void showMainMenu() {
        JPanel titleScreen = generateMainMenu();
        mainWindow.setContentPane(titleScreen);
        mainWindow.setPreferredSize(new Dimension(500, 850));
        mainWindow.revalidate();
    }

    // Starts the game by switching to the InGameUI
    private static void startGame() {
        InGameUI gameSession = new InGameUI();
        mainWindow.setContentPane(gameSession);
        mainWindow.setPreferredSize(new Dimension(1000, 750));
        mainWindow.revalidate();
    }

    // Checks achievements and displays them to the player
    private static void checkAchievements() {
        System.out.println("Checking Achievements");
    }

    // Checks and displays the leaderboard
    private static void checkLeaderboard() {
        System.out.println("Checking Leaderboard");
    }

    // Checks and displays game settings
    private static void checkSettings() {
        System.out.println("Checking Settings");
    }

    // Display game instructions to the player
    private static void checkInstructions() {
        InstructionsUI instructionsUI = new InstructionsUI();
        mainWindow.setContentPane(instructionsUI);
        mainWindow.setPreferredSize(new Dimension(500, 850));
        mainWindow.revalidate();
    }

    // Pause the game and display the pause menu. TODO: To be implemented
    private void pauseMenu(){
        System.out.println("Pause Menu");
    }
}