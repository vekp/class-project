package minigames.client;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.event.ActionListener;

import minigames.client.achievements.AchievementNotificationHandler;
import minigames.client.achievements.AchievementUI;
import minigames.client.survey.Survey;
import minigames.client.backgrounds.Starfield;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;

import java.awt.*;
import java.util.List;

/**
 * The main window that appears.
 * <p>
 * For simplicity, we give it a BorderLayout with panels for north, south, east, west, and center.
 * <p>
 * This makes it simpler for games to load up the UI however they wish, though the default expectation
 * is that the centre just has an 800x600 canvas.
 */
public class MinigameNetworkClientWindow {

    MinigameNetworkClient networkClient;
    private final AchievementNotificationHandler achievementPopups;

    JFrame frame;

    JPanel parent;
    JPanel north;
    JPanel center;
    JPanel south;
    JPanel west;
    JPanel east;

    JLabel messageLabel;

    // We hang on to this one for registering in servers
    JTextField nameField;

    public MinigameNetworkClientWindow(MinigameNetworkClient networkClient) {
        // Use LookandFeel to change the SwingUI theme
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("nimbusBase", Color.BLACK); // Set base color
                    UIManager.put("nimbusBlueGrey", Color.BLACK); // Set blue/grey color
                    UIManager.put("control", Color.BLACK); // Set control background color
                    UIManager.put("text", Color.WHITE); // Set text color
                    UIManager.put("List.background", Color.BLACK); // Set list background
                    UIManager.put("TextField.textForeground", Color.BLACK); // Set text field text color
                    UIManager.put("List.foreground", Color.BLACK); // Set JList text color
                    UIManager.put("ComboBox.foreground", Color.BLACK); // Set JComboBox text color
                    break;
                }
            }
        } catch (Exception e) {

        }

        this.networkClient = networkClient;

        frame = new JFrame();
        //frame.setUndecorated(true); // removes the frame around the window.
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.achievementPopups = new AchievementNotificationHandler(networkClient);

        parent = new JPanel(new BorderLayout());

        north = new JPanel();
        parent.add(north, BorderLayout.NORTH);
        center = new JPanel();
        center.setPreferredSize(new Dimension(800, 600));
        parent.add(center, BorderLayout.CENTER);
        south = new JPanel();
        parent.add(south, BorderLayout.SOUTH);
        east = new JPanel();
        parent.add(east, BorderLayout.EAST);
        west = new JPanel();
        parent.add(west, BorderLayout.WEST);

        frame.add(parent);

        nameField = new JTextField(20);
        nameField.setText("Algernon");

        frame.setLocationRelativeTo(null);

    }

    /**
     * Removes all components from the south panel
     */
    public void clearSouth() {
        south.removeAll();
    }

    /**
     * Clears all sections of the UI
     */
    public void clearAll() {
        for (JPanel p : new JPanel[]{north, south, east, west, center}) {
            p.removeAll();
        }
    }

    /**
     * Adds a component to the north part of the main window
     */
    public void addNorth(java.awt.Component c) {
        north.add(c);
    }

    /**
     * Adds a component to the south part of the main window
     */
    public void addSouth(java.awt.Component c) {
        south.add(c);
    }

    /**
     * Adds a component to the east part of the main window
     */
    public void addEast(java.awt.Component c) {
        east.add(c);
    }

    /**
     * Adds a component to the west part of the main window
     */
    public void addWest(java.awt.Component c) {
        west.add(c);
    }

    /**
     * Adds a component to the center of the main window
     */
    public void addCenter(java.awt.Component c) {
        center.add(c);
    }

    /**
     * "Packs" the frame, setting its size to match the preferred layout sizes of its component
     */
    public void pack() {
        frame.pack();
        parent.repaint();
    }

    /**
     * Makes the main window visible
     */
    public void show() {
        pack();
        frame.setVisible(true);
    }

    /**
     * Shows a simple message layered over a retro-looking starfield.
     * Terrible placeholder art.
     */
    public void showStarfieldMessage(String s) {
        clearAll();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(new Starfield(networkClient.animator), JLayeredPane.DEFAULT_LAYER);
        layeredPane.setBackground(new Color(0, 0, 0, 0));
        layeredPane.setPreferredSize(new Dimension(800, 600));

        JLabel label = new JLabel(s);
        label.setOpaque(true);
        label.setForeground(Color.CYAN);
        label.setBackground(Color.BLACK);
        label.setFont(new Font("Monospaced", Font.PLAIN, 36));
        Dimension labelSize = label.getPreferredSize();
        label.setSize(labelSize);
        label.setLocation((int) (400 - labelSize.getWidth() / 2), (int) (300 - labelSize.getHeight() / 2));
        layeredPane.add(label, JLayeredPane.MODAL_LAYER);

        center.add(layeredPane);
        pack();
    }

    /**
     * Shows a list of GameServers to pick from
     * <p>
     * TODO: Prettify!
     *
     * @param servers
     */
    /**
     * Shows a list of GameServers to pick from
     * <p>
     * TODO: Prettify!
     *
     * @param servers
     */
    public void showGameServers(List<GameServerDetails> servers) {
        frame.setTitle("COSC220 2023 Minigame Collection");
        clearAll();
        networkClient.getNotificationManager().resetToDefaultSettings();

        // Add the nameField to the north panel
        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Your name");
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        north.add(namePanel);

        // Create a panel for the buttons and arrow buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        JPanel serverButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout with spacing
        serverButtonPanel.setBackground(Color.BLACK); // Set background color to black

        for (GameServerDetails gsd : servers) {
            JButton newG = new JButton(
                    String.format("<html><h1>%s</h1><p>%s</p></html>", gsd.name(), gsd.description())
            );

            // Set button styles
            newG.setPreferredSize(new Dimension(150, 250)); // Adjust the preferred size as needed
            newG.setBorderPainted(false); // Remove button borders
            newG.setFocusPainted(false); // Remove focus border

            newG.addActionListener((evt) -> {
                networkClient.getGameMetadata(gsd.name())
                        .onSuccess((list) -> showGames(gsd.name(), list));
            });

            serverButtonPanel.add(newG);
        }

        buttonPanel.add(serverButtonPanel, BorderLayout.CENTER);

        center.setLayout(new BorderLayout());
        center.add(buttonPanel, BorderLayout.CENTER);

        JButton achievementsButton = new JButton("Achievements");
        achievementsButton.addActionListener(e -> {
            AchievementUI achievements = new AchievementUI(networkClient);
            achievements.load();
        });
        south.add(achievementsButton);

        JButton surveyButton = new JButton("Survey");
        surveyButton.addActionListener(e -> {
            clearAll();
            String gameId = "64fec6296849f97cdc19f017";
            JPanel survey = new Survey(networkClient, gameId);
            frame.setTitle(Survey.FRAME_TITLE);
            center.add(survey);
            pack();
        });
        south.add(surveyButton);

        pack();

    }

    /**
     * Shows a list of games to pick from
     * <p>
     * TODO: Prettify!
     *
     * @param gameServer
     * @param inProgress
     */
    public void showGames(String gameServer, List<GameMetadata> inProgress) {
        clearAll();

        // Remove the nameField from the north panel
        north.removeAll();
        north.revalidate(); // This is needed to update the UI

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<JPanel> gamePanels = inProgress.stream().map((g) -> {
            JPanel p = new JPanel();
            JLabel l = new JLabel(String.format("<html><h1>%s</h1><p>%s</p></html>", g.name(), String.join(",", g.players())));
            JButton join = new JButton("Join game");
            join.addActionListener((evt) -> {
                networkClient.joinGame(gameServer, g.name(), nameField.getText());
            });
            join.setEnabled(g.joinable());
            p.add(l);
            p.add(join);
            return p;
        }).toList();

        for (JPanel gamePanel : gamePanels) {
            panel.add(gamePanel);
        }

        JButton newG = new JButton("New game");
        newG.addActionListener((evt) -> {
            // FIXME: We've got a hardcoded player name here
            networkClient.newGame(gameServer, nameField.getText());
        });

        JButton returnToMainMenu = new JButton("Return to Main Menu");
        // TODO

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(newG);
        buttonPanel.add(returnToMainMenu);

        // Create a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        // Add the scroll pane to the center of the main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the button panel to the west of the main panel
        mainPanel.add(buttonPanel, BorderLayout.WEST);

        center.add(mainPanel);
        pack();
        parent.repaint();
    }


    /**
     * Return a reference to this window's frame
     */
    public JFrame getFrame() {
        return frame;
    }

}
