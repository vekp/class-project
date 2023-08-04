package minigames.client.achievementui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AchievementUI extends JPanel {
    public static final String TITLE = "Achievements";

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
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
