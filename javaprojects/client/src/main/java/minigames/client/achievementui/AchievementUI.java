package minigames.client.achievementui;

import minigames.achievements.Achievement;
import minigames.achievements.AchievementTestData;
import minigames.achievements.GameAchievementState;
import minigames.achievements.PlayerAchievementRecord;
import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AchievementUI extends JPanel {
    public static final String TITLE = "Achievements";
    private final JScrollPane achievementScrollPane;

    /**
     * Creates a new JPanel containing a user interface for viewing achievements.
     * @param networkClient the MinigameNetworkClient to be used for communicating with server.
     * @param returnAction an ActionListener for returning to the previous screen.
     */
    public AchievementUI(MinigameNetworkClient networkClient, ActionListener returnAction) {
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());

        //todo obtain username from login / user system when able
        List<String> usernames = AchievementTestData.getNames();

        // Title
        JLabel title = new JLabel(TITLE);
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 36));
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        this.add(titlePanel, BorderLayout.NORTH);

        // Selection menu items on the left
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        JList<String> usernameJList = new JList<>(usernames.toArray(new String[0]));
        usernameJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectorPanel.add(generateScrollPane("Username:", usernameJList));
        usernameJList.addListSelectionListener(e -> {
            String selectedUsername = usernameJList.getSelectedValue();
            networkClient.getPlayerAchievements(this, selectedUsername);
        });

        selectorPanel.setBorder(new EmptyBorder(0, 0, 0, 15));
        this.add(selectorPanel, BorderLayout.WEST);

        // Achievement list on the right in a scroll pane
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        achievementPanel.add(Box.createRigidArea(new Dimension(0,20)));
        JLabel achievementPanelLabel = new JLabel("Achievements:");
        achievementPanelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        achievementPanel.add(achievementPanelLabel);
        achievementScrollPane = new JScrollPane();
        achievementScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> achievementScrollPane.getViewport().setViewPosition(new Point(0, 0)));
        achievementScrollPane.setAlignmentX(0);
        achievementPanel.add(achievementScrollPane);
        this.add(achievementPanel);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(returnAction);
        buttonPanel.add(backButton, BorderLayout.EAST);
        // Add demo popup button TODO: Remove when not required
        AchievementPresenter testAchievement = new AchievementPresenter(new Achievement(
                "Popup tester",
                "Congratulations, you have opened a popup notification!",
                0, "",
                false
        ), true);
        JButton popup = new JButton("Demo achievement popup");
        popup.addActionListener(e -> {
            JPanel popupDemoPanel = testAchievement.smallAchievementPanel(false);
            popupDemoPanel.setBorder(null);
            networkClient.getMainWindow().showNotification(popupDemoPanel);
        });
        buttonPanel.add(popup, BorderLayout.WEST);
        // Another popup demo button TODO: Remove this
        JButton popup2 = new JButton("Demo text popup");
        popup2.addActionListener(e -> {
            JButton leftPopup = new JButton("<html>This is another popup example.<br>It uses Component.RIGHT_ALIGNMENT (1.0f)<br>Click me for another popup.");
            leftPopup.addActionListener(e1 -> networkClient.getMainWindow().showNotification(new JLabel("This one is on the left, with Component.LEFT_ALIGNMENT"), Component.LEFT_ALIGNMENT));

            networkClient.getMainWindow().showNotification(leftPopup, Component.RIGHT_ALIGNMENT);
        });
        buttonPanel.add(popup2);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     *
     * @param label a label to place at the top of the pane
     * @param selectItems the JList containing the selectable items
     * @return a JScrollPane with selectable items
     */
    private JPanel generateScrollPane(String label, JList<String> selectItems){
        JScrollPane scrollPane = new JScrollPane(selectItems);
        // Set preferred size so that scrolling is only needed if window is shrunk.
        scrollPane.setPreferredSize(new Dimension(150, selectItems.getModel().getSize()*18));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));

        JLabel instructionLabel = new JLabel(label);
        instructionLabel.setAlignmentX(0);
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel,BoxLayout.Y_AXIS));
        itemsPanel.add(Box.createRigidArea(new Dimension(0,20)));
        itemsPanel.add(instructionLabel);
        scrollPane.setAlignmentX(0);
        itemsPanel.add(scrollPane);
        return itemsPanel;
    }

     /** This will be called when the server responds to an achievement data request. The player achievement record
     * contains the ID of the player that we requested info for, and the list of achievements they have unlocked/have
     * yet to unlock
     * @param data the achievement data for a single player
     */
    public void populateAchievementPanel(PlayerAchievementRecord data){
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        System.out.println("I have received some player data for " + data.playerID());
        for (GameAchievementState state: data.gameAchievements()) {
            String gameID = state.gameID();
            JLabel gameLabel = new JLabel(gameID);
            gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            gameLabel.setFont(new Font(gameLabel.getFont().getFontName(), Font.BOLD, 25));
            gameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
            achievementPanel.add(gameLabel);
            List<Achievement> unlockedAchievements = state.unlocked();
            if (!unlockedAchievements.isEmpty()) {
                AchievementCollection presenterList = achievementCollection(unlockedAchievements, true);
                //Demo for carousel. todo: remove later when not needed.
                JButton carousel = new JButton("Demo carousel");
                carousel.addActionListener(e -> JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(achievementPanel), presenterList.achievementCarousel(),
                        gameID, JOptionPane.PLAIN_MESSAGE));
                achievementPanel.add(carousel);

                achievementPanel.add(presenterList.achievementListPanel());
            }
            List<Achievement> lockedAchievements = state.locked();
            if (!lockedAchievements.isEmpty()) {
                AchievementCollection presenterList = achievementCollection(lockedAchievements, false);
                achievementPanel.add(presenterList.achievementListPanel());
            }
        }
        achievementScrollPane.setViewportView(achievementPanel);
    }

    private AchievementCollection achievementCollection(List<Achievement> achievementList, boolean isUnlocked) {
        List<AchievementPresenter> presenterList = new ArrayList<>();
        for (Achievement achievement : achievementList) {
            presenterList.add(new AchievementPresenter(achievement, isUnlocked));
        }
        return new AchievementCollection(presenterList);
    }
}
