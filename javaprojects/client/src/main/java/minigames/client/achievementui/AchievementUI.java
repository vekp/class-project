package minigames.client.achievementui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AchievementUI extends JPanel {
    public static final String TITLE = "Achievements";

    /**
     * Creates a new JPanel containing a user interface for viewing achievements.
     * @param returnAction an ActionListener for returning to the previous screen.
     */
    public AchievementUI(ActionListener returnAction) {
        this.setPreferredSize(new Dimension(800, 600));
        this.add(new JLabel("Is this working? I think it is!"));

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(returnAction);
        this.add(backButton);
    }
}
