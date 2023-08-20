package minigames.client.achievementui;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
import minigames.achievements.PlayerAchievementRecord;
import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class AchievementUI extends JPanel {
    public static final String TITLE = "Achievements";
    private final JScrollPane achievementScrollPane;

    /**
     * Creates a new JPanel containing a user interface for viewing achievements.
     *
     * @param mnClient the MinigameNetworkClient to be used for communicating with server.
     */
    public AchievementUI(MinigameNetworkClient mnClient) {
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());


        // Title
        JLabel title = new JLabel(TITLE);
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 36));
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        this.add(titlePanel, BorderLayout.NORTH);

        // Selection menu items on the left
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));

        //Getting usernames from server
        //todo obtain username from login / user system when able
        mnClient.getPlayerNames().onSuccess((resp) -> {
            String[] names = resp.replace("[","").replace("]","").split(",");
           JList<String> usernameJlist = new JList<>(names);
            usernameJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            selectorPanel.add(generateScrollPane("Username:", usernameJlist));
            usernameJlist.addListSelectionListener(e -> {
                String selectedUsername = usernameJlist.getSelectedValue();
                mnClient.getPlayerAchievements(this, selectedUsername);
            });
            mnClient.getMainWindow().pack();
        });


        selectorPanel.setBorder(new EmptyBorder(0, 0, 0, 15));
        this.add(selectorPanel, BorderLayout.WEST);

        // Achievement list on the right in a scroll pane
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        achievementPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel achievementPanelLabel = new JLabel("Achievements:");
        achievementPanelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        achievementPanel.add(achievementPanelLabel);
        achievementScrollPane = new JScrollPane();
        achievementScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        achievementScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        SwingUtilities.invokeLater(() -> achievementScrollPane.getViewport().setViewPosition(new Point(0, 0)));
        achievementScrollPane.setAlignmentX(0);
        achievementPanel.add(achievementScrollPane);
        this.add(achievementPanel);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());
        buttonPanel.add(backButton, BorderLayout.EAST);
        // Add demo popup button TODO: Remove when not required
        AchievementPresenter testAchievement = new AchievementPresenter(new Achievement(
                "Popup tester",
                "<html>Congratulations, you have opened a popup notification!<br>Click me to dismiss! It uses random alignment</html>",
                0, "",
                false
        ), true);
        JButton popup = new JButton("Demo achievement popup");
        popup.addActionListener(e -> {
            JPanel popupDemoPanel = testAchievement.mediumAchievementPanel(false);
            popupDemoPanel.setBorder(null);
            float x = new Random().nextFloat();
            mnClient.getNotificationManager().setAlignment(x);
            mnClient.getNotificationManager().showNotification(popupDemoPanel);
            mnClient.getNotificationManager().setAlignment(0.5f);
        });
        buttonPanel.add(popup, BorderLayout.WEST);
        // Another popup demo button TODO: Remove this
        JButton popup2 = new JButton("Demo text popup");
        popup2.addActionListener(e -> {
            JButton leftPopup = new JButton("<html>This is another popup example.<br>Click me for another popup.");
            leftPopup.addActionListener(e1 -> {
                mnClient.getNotificationManager().setAlignment(0f);
                mnClient.getNotificationManager().showNotification(new JLabel("This one is on the left, with Component.LEFT_ALIGNMENT"));
            });

            mnClient.getNotificationManager().showNotification(leftPopup, false);
        });
        mnClient.getNotificationManager().setAlignment(1.0f);
        buttonPanel.add(popup2);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * @param label       a label to place at the top of the pane
     * @param selectItems the JList containing the selectable items
     * @return a JScrollPane with selectable items
     */
    private JPanel generateScrollPane(String label, JList<String> selectItems) {
        JScrollPane scrollPane = new JScrollPane(selectItems);
        // Set preferred size so that scrolling is only needed if window is shrunk.
        scrollPane.setPreferredSize(new Dimension(150, selectItems.getModel().getSize() * 18));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));

        JLabel instructionLabel = new JLabel(label);
        instructionLabel.setAlignmentX(0);
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        itemsPanel.add(instructionLabel);
        scrollPane.setAlignmentX(0);
        itemsPanel.add(scrollPane);
        return itemsPanel;
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
        System.out.println("I have received some player data for " + data.playerID());
        for (GameAchievementState state : data.gameAchievements()) {
            String gameID = state.gameID();
            JLabel gameLabel = new JLabel(gameID);
            gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            gameLabel.setFont(new Font(gameLabel.getFont().getFontName(), Font.BOLD, 25));
            gameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
            achievementPanel.add(gameLabel);
            AchievementCollection gameAchievements = new AchievementCollection(state);
            achievementPanel.add(gameAchievements.achievementListPanel());
        }
        achievementScrollPane.setViewportView(achievementPanel);
    }
}
