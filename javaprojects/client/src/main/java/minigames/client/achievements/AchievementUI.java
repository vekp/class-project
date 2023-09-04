package minigames.client.achievements;

import minigames.achievements.GameAchievementState;
import minigames.achievements.PlayerAchievementRecord;
import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AchievementUI extends JPanel {
    private final JScrollPane achievementScrollPane;
    private final MinigameNetworkClient mnClient;
    private final JPanel buttonPanel;

    /**
     * Creates a new JPanel containing a user interface for viewing achievements.
     *
     * @param mnClient the MinigameNetworkClient to be used for communicating with server.
     */
    public AchievementUI(MinigameNetworkClient mnClient) {
        this.mnClient = mnClient;
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("<html><h1>Achievements</h1></html>");
        this.add(title, gbc);

        // Section headers
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 10);
        this.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        this.add(new JLabel("Achievements: "), gbc);

        // Username menu
        GridBagConstraints usernameGBC = new GridBagConstraints();
        usernameGBC.gridx = 0;
        usernameGBC.gridy = 2;
        usernameGBC.weighty = 1;
        usernameGBC.insets = new Insets(10, 10, 10, 10);
        usernameGBC.fill = GridBagConstraints.BOTH;
        usernameGBC.ipadx = 50;

        //todo obtain username from login / user system when able
        mnClient.getPlayerNames().onSuccess((resp) -> {
            String[] names = resp.replace("[","").replace("]","").split(",");
            JList<String> usernameJlist = new JList<>(names);
            usernameJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.add(generateScrollPane(usernameJlist), usernameGBC);
            usernameJlist.addListSelectionListener(e -> {
                String selectedUsername = usernameJlist.getSelectedValue();
                mnClient.getPlayerAchievements(this, selectedUsername);
            });
            mnClient.getMainWindow().pack();
        });

        // Achievement list scroll pane
        gbc = (GridBagConstraints) usernameGBC.clone();
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.ipadx = 0;
        achievementScrollPane = generateScrollPane(null);
        achievementScrollPane.getVerticalScrollBar().setUnitIncrement(5);
        this.add(achievementScrollPane, gbc);

        buttonPanel = new JPanel(new BorderLayout());
        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());
        buttonPanel.add(backButton, BorderLayout.EAST);
    }

    /**
     * Create a vertical scroll pane of the given content
     */
    public static JScrollPane generateScrollPane(Component content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    /**
     * Display the achievement viewer UI in the main window, with buttons below.
     */
    public void load() {
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(this);
        mnClient.getMainWindow().addSouth(buttonPanel);
        mnClient.getMainWindow().pack();

    }

    /**
     * This will be called when the server responds to an achievement data request. The player achievement record
     * contains the ID of the player that we requested info for, and the list of achievements they have unlocked/have
     * yet to unlock
     *
     * @param data the achievement data for a single player
     */
    public void populateAchievementPanel(PlayerAchievementRecord data) {
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
//        System.out.println("I have received some player data for " + data.playerID());
        for (GameAchievementState state : data.gameAchievements()) {
            String gameID = state.gameID();
            JLabel gameLabel = new JLabel("<html><h2>" + gameID + "</h2></html>");
            gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            gameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
            achievementPanel.add(gameLabel);
            AchievementPresenterRegistry gameAchievements = new AchievementPresenterRegistry(state, mnClient.getAnimator());
            achievementPanel.add(gameAchievements.achievementListPanel(mnClient.getAchievementDialogViewer()));
        }
        achievementScrollPane.setViewportView(achievementPanel);
    }
}
