package minigames.client.achievementui;

import minigames.achievements.Achievement;
import minigames.achievements.AchievementHandler;
import minigames.achievements.AchievementRegister;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AchievementUI extends JPanel {
    public static final String TITLE = "Achievements";

    /**
     * Creates a new JPanel containing a user interface for viewing achievements.
     * @param returnAction an ActionListener for returning to the previous screen.
     */
    public AchievementUI(ActionListener returnAction) {
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());

        // Get test values. TODO: Get real values from server.
        AchievementRegister register = AchievementUITestData.getTestData();
        List<String> usernames = register.getPlayerList();
        List<String> games = new ArrayList<>();
        for (AchievementHandler h : register.getAllHandlers()) games.add(h.getGameID());

        // Title
        JLabel title = new JLabel(TITLE);
        title.setFont(new Font(title.getFont().getFontName(), Font.PLAIN, 25));
        title.setBackground(Color.LIGHT_GRAY);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(title, BorderLayout.CENTER);
        this.add(titlePanel, BorderLayout.NORTH);


        // Selection menu items on the left
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        JList usernameJList = new JList<>(usernames.toArray(new String[0]));
        selectorPanel.add(generateScrollPane("Username:", usernameJList));
        JList gamesJList = new JList<>(games.toArray(new String[0]));
        selectorPanel.add(generateScrollPane("Game:", gamesJList));
        // Radio buttons the menus
        JRadioButton unlockedButton = new JRadioButton("Unlocked", true);
        JRadioButton lockedButton = new JRadioButton("Locked", false);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(unlockedButton);
        buttonGroup.add(lockedButton);
        selectorPanel.add(unlockedButton);
        selectorPanel.add(lockedButton);

        selectorPanel.setBorder(new EmptyBorder(0, 0, 0, 15));
        this.add(selectorPanel, BorderLayout.WEST);

        // Achievement list on the right
        JPanel emptyAchievementPanel = generateScrollPane("Achievements:", new JList<>(new String[0]));
        this.add(emptyAchievementPanel);

        //TODO: Add checkboxes to select unlocked/locked achievements

        JPanel buttonPanel = new JPanel(new BorderLayout());
        // Add submit button
        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            String selectedUsername = (String) usernameJList.getSelectedValue();
            String selectedGame = (String) gamesJList.getSelectedValue();
            System.out.println("Selected: " + selectedUsername + ", " + selectedGame +", "+unlockedButton.isSelected());
            if (selectedUsername == null || selectedGame == null) return;
            JPanel achievementPanel = populateAchievementPanel(selectedUsername, selectedGame, register, unlockedButton.isSelected());
            this.add(achievementPanel);
            // Update the frame
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.invalidate();
            frame.validate();
        });
        buttonPanel.add(submit, BorderLayout.CENTER);

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(returnAction);
        buttonPanel.add(backButton, BorderLayout.EAST);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

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

    private JPanel populateAchievementPanel(String username, String gameID, AchievementRegister register, boolean unlocked) {
        ArrayList<Achievement> userAchievements = register.getUserAchievements(username, gameID, unlocked);
        StringBuilder achievementStringBuilder = new StringBuilder((unlocked? "Unlocked" : "Locked") + " achievements:\n");
        for (Achievement achievement : userAchievements) {
            System.out.println(achievement.name() + ", " + achievement.description());
            achievementStringBuilder.append(achievement.name()).append("\n");
        }
        String results = achievementStringBuilder.toString();
        if (results.length() == 0) results = "Nothing found.";
        JPanel panel = new JPanel();
        //TODO: Put in a scroll pane and make less ugly
        panel.add(new JTextArea(results));
        return panel;
    }
}
