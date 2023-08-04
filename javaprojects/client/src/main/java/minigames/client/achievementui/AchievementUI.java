package minigames.client.achievementui;

import javax.swing.*;
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

        // Create some dummy values. TODO: Get real values from server.
        List<String> usernames = new ArrayList<>();
        List<String> games = new ArrayList<>();
        List<String> achievements = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            usernames.add("DummyUsername");
            games.add("DummyGame");
            achievements.add("DummyAchievement");
        }

        // Selection menu items
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        selectorPanel.add(new JList<>(usernames.toArray()));
        selectorPanel.add(new JList<>(games.toArray()));
        this.add(selectorPanel, BorderLayout.WEST);

        // Achievement list
        this.add(new JList<>(achievements.toArray()));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        // Add submit button TODO: Make it functional
        JButton submit = new JButton("Submit");
        buttonPanel.add(submit, BorderLayout.CENTER);

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(returnAction);
        buttonPanel.add(backButton, BorderLayout.EAST);

        this.add(buttonPanel, BorderLayout.SOUTH);

    }
}
