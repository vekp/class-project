package minigames.client.achievementui;

import minigames.achievements.*;
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
     * @param returnAction an ActionListener for returning to the previous screen.
     */
    public AchievementUI(MinigameNetworkClient networkClient, ActionListener returnAction) {
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());

        //todo obtain username from login / user system when able
        List<String> usernames = AchievementTestData.getNames();

        // Title
        JLabel title = new JLabel(TITLE);
        title.setFont(new Font(title.getFont().getFontName(), Font.PLAIN, 36));
        JPanel titlePanel = new JPanel();
        titlePanel.add(title);
        this.add(titlePanel, BorderLayout.NORTH);

        // Selection menu items on the left
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        JList<String> usernameJList = new JList<>(usernames.toArray(new String[0]));
        selectorPanel.add(generateScrollPane("Username:", usernameJList));

        //todo games list no longer does anything, remove?
        List<String> games = new ArrayList<>();
        JList<String> gamesJList = new JList<>(games.toArray(new String[0]));
        selectorPanel.add(generateScrollPane("Game:", gamesJList));

        // Radio buttons under the menus
        JRadioButton unlockedButton = new JRadioButton("Unlocked", true);
        JRadioButton lockedButton = new JRadioButton("Locked", false);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(unlockedButton);
        buttonGroup.add(lockedButton);
        selectorPanel.add(unlockedButton);
        selectorPanel.add(lockedButton);

        selectorPanel.setBorder(new EmptyBorder(0, 0, 0, 15));
        this.add(selectorPanel, BorderLayout.WEST);

        // Achievement list on the right in a scroll pane
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        achievementPanel.add(Box.createRigidArea(new Dimension(0,20)));
        JLabel achievementPanelLabel = new JLabel("Please make your selections.");
        achievementPanelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        achievementPanel.add(achievementPanelLabel);
        achievementScrollPane = new JScrollPane();
        achievementScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        SwingUtilities.invokeLater(() -> achievementScrollPane.getViewport().setViewPosition(new Point(0, 0)));
        achievementScrollPane.setAlignmentX(0);
        achievementPanel.add(achievementScrollPane);
        this.add(achievementPanel);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        // Add submit button
        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            //TODO implement new data package - hook up to the ui panels
            String selectedUsername = usernameJList.getSelectedValue();
            networkClient.getPlayerAchievements(this, selectedUsername);
        });
        buttonPanel.add(submit, BorderLayout.CENTER);

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(returnAction);
        buttonPanel.add(backButton, BorderLayout.EAST);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     *  
     * @param label
     * @param selectItems
     * @return
     */
    private JPanel generateScrollPane(String label, JList<String> selectItems){
        JScrollPane scrollPane = new JScrollPane(selectItems);
        // Set preferred size so that scrolling is only needed if window is shrunk.
        scrollPane.setPreferredSize(new Dimension(200, selectItems.getModel().getSize()*18));
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

     /** This will be called when the server responds to an achevement data request. The player achievement record
     * contains the ID of the player that we requested info for, and the list of achievements they have unlocked/have
     * yet to unlock
     * @param data the achievement data for a single player
     */
    public void populateAchievementPanel(PlayerAchievementRecord data){
        //todo use the server-based data to populate panels
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
        System.out.println("I have received some player data for " + data.playerID());
        for (GameAchievementState state: data.gameAchievements()) {
            String gameID = state.gameID();
            JLabel gameLabel = new JLabel(gameID);
            gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            gameLabel.setFont(new Font(gameLabel.getFont().getFontName(), Font.BOLD, 25));
            achievementPanel.add(gameLabel);
            List<Achievement> unlockedAchievements = state.unlocked();
                if (!unlockedAchievements.isEmpty()) {
                    AchievementCollection presenterList = achievementCollection(unlockedAchievements, true);
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
