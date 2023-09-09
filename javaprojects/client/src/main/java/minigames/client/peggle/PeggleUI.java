package minigames.client.peggle;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.logging.Logger;

public class PeggleUI implements GameClient {
    private static final String iconFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/gameIcon.png";
    private static final String backgroundFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/menuBG.png";
    private static final String startButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/startBTN.png";
    private static final String exitButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/exitBTN.png";
    private static final String instructionsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/instructionsBTN.png";
    private static final String achievementsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/achievementsBTN.png";
    private static final String leaderboardButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/leaderboardBTN.png";
    private static final String settingsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/settingsBTN.png";
    private MinigameNetworkClient mnClient;
    private GameMetadata gm;
    private String player;
    private static final Logger logger = Logger.getLogger(PeggleUI.class.getName());

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        mnClient.getMainWindow().addCenter(generateMainMenu(mnClient));
        mnClient.getMainWindow().getFrame().setPreferredSize(new Dimension(1920,1080));
        mnClient.getMainWindow().pack();

    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.info("my command: " + command.encode());
        switch(command.getString("command")) {
//            case "startGame" -> sendCommand("test");
        }
    }

    @Override
    public void closeGame() {
        sendCommand("exitGame");

    }

    private void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));

    }

    private static JPanel generateMainMenu(MinigameNetworkClient mnClient) {
        JPanel titleScreen = new JPanel(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon(backgroundFilePath));
        titleScreen.add(background, BorderLayout.CENTER);
        background.setLayout(new GridBagLayout());
        JPanel buttonsPanel = generateMainButtonsPanel(mnClient);
        addPanelToBackground(background, buttonsPanel, GridBagConstraints.CENTER);
        JPanel topCenterButtonsPanel = generateTopCenterButtonsPanel(mnClient);
        addPanelToBackground(background, topCenterButtonsPanel, GridBagConstraints.NORTH);
        return titleScreen;
    }

    private static void addPanelToBackground(JLabel background, JPanel panel, int anchor) {
        GridBagConstraints backgroundConstraints = new GridBagConstraints();
        backgroundConstraints.gridx = 0;
        backgroundConstraints.gridy = 0;
        backgroundConstraints.weightx = 1;
        backgroundConstraints.weighty = 1;
        backgroundConstraints.anchor = anchor;
        background.add(panel, backgroundConstraints);
    }


    private static JPanel generateMainButtonsPanel(MinigameNetworkClient mnClient) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)); // Vertical alignment
        JButton startButton = createImageButton(startButtonFilePath, e -> startGame(mnClient), 0.5); //scaling
        JButton exitButton = createImageButton(exitButtonFilePath, e -> System.exit(0), 0.5); //scaling
        buttonsPanel.add(startButton);
        buttonsPanel.add(Box.createVerticalStrut(75)); // Vertical spacing between buttons
        buttonsPanel.add(exitButton);
        return buttonsPanel;
    }


    private static JPanel generateTopCenterButtonsPanel(MinigameNetworkClient mnClient) {
        JPanel topRightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topRightButtonsPanel.setOpaque(false);
        topRightButtonsPanel.add(createImageButton(achievementsButtonFilePath, e -> checkAchievements(mnClient), 0.3));
        topRightButtonsPanel.add(createImageButton(leaderboardButtonFilePath, e -> checkLeaderboard(mnClient), 0.3));
        topRightButtonsPanel.add(createImageButton(instructionsButtonFilePath, e -> checkInstructions(mnClient), 0.3));
        topRightButtonsPanel.add(createImageButton(settingsButtonFilePath, e -> checkSettings(mnClient), 0.3));
        return topRightButtonsPanel;
    }


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

    public static void showMainMenu(MinigameNetworkClient client) {

        JFrame mainWindow = client.getMainWindow().getFrame();
        JPanel titleScreen = generateMainMenu(client);
        mainWindow.setContentPane(titleScreen);
        mainWindow.setPreferredSize(new Dimension(1920, 1080));
        mainWindow.pack();
        mainWindow.revalidate();
    }

    private static void startGame(MinigameNetworkClient client) {
        InGameUI gameSession = new InGameUI(client);

        JFrame mainWindow = client.getMainWindow().getFrame();
        mainWindow.setContentPane(gameSession);
        mainWindow.setPreferredSize(new Dimension(1920, 1080));
        mainWindow.pack();
        mainWindow.revalidate();
    }

    private static void checkInstructions(MinigameNetworkClient client) {
        InstructionsUI instructionsUI = new InstructionsUI(client);
        JFrame mainWindow = client.getMainWindow().getFrame();
        mainWindow.setContentPane(instructionsUI);
        mainWindow.setPreferredSize(new Dimension(1920, 1080));
        mainWindow.pack();
        mainWindow.revalidate();
    }

    private static void checkAchievements(MinigameNetworkClient client) {
        System.out.println("Checking Achievements");
    }

    private static void checkLeaderboard(MinigameNetworkClient client) {
        System.out.println("Checking Leaderboard");
    }

    private static void checkSettings(MinigameNetworkClient client) {
        System.out.println("Checking Settings");
    }


}
