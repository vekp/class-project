package minigames.client.achievementui;

import minigames.achievements.Achievement;
import minigames.achievements.AchievementHandler;
import minigames.achievements.AchievementRegister;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AchievementUI extends JPanel {
    public static final String TITLE = "Achievements";
    private final JScrollPane achievementScrollPane;

    /**
     * Creates a new JPanel containing a user interface for viewing achievements.
     * @param returnAction an ActionListener for returning to the previous screen.
     */
    public AchievementUI(ActionListener returnAction) {
        System.out.println(new File("src/main/resources").exists());
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());

        // Get test values. TODO: Get real values from server.
        AchievementRegister register = AchievementUITestData.getTestData();
        List<String> usernames = register.getPlayerList();
        List<String> games = new ArrayList<>();
        for (AchievementHandler h : register.getAllHandlers()) games.add(h.getGameID());

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
        achievementPanelLabel.setAlignmentX(0);
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
            String selectedUsername = usernameJList.getSelectedValue();
            String selectedGame = gamesJList.getSelectedValue();
            System.out.println("Selected: " + selectedUsername + ", " + selectedGame +", "+unlockedButton.isSelected());
            if (selectedUsername == null || selectedGame == null) return;
            achievementPanelLabel.setText((unlockedButton.isSelected()? "Unl" : "L") + "ocked achievements");
            JPanel scrollPaneContents = populateAchievementPanel(selectedUsername, selectedGame, register, unlockedButton.isSelected());
            achievementScrollPane.setViewportView(scrollPaneContents);
            // This code might be required if components need to be redrawn
//            // Update the frame
//            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
//            frame.invalidate();
//            frame.validate();
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

    /**
     * Fills the panel with the obtained achievements for the specified user
     * @param username The username of the specified user
     * @param gameID The ID of the game that is being queried
     * @param register The AchievementRegister Object containing all achievement information
     * @param unlocked Whether the achievement has been unlocked or not
     * @return A panel with this information displayed
     */
    private JPanel populateAchievementPanel(String username, String gameID, AchievementRegister register, boolean unlocked) {
        ArrayList<Achievement> userAchievements = register.getUserAchievements(username, gameID, unlocked);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        for (Achievement achievement : userAchievements) {
            AchievementPresenter presenter = new AchievementPresenter(achievement);
//            System.out.println(achievement.name() + ", " + achievement.description());
//            JLabel label = new JLabel(achievement.name());
//            label.setToolTipText(achievement.description());
            panel.add(presenter.smallAchievementPanel());
        }
        return panel;
    }
}
