package minigames.client.achievementui;

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

        List<String> achievements = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            achievements.add("DummyAchievement " + i);
        }

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
        selectorPanel.add(generateScrollJPanel("Username:", new JList<>(usernames.toArray(new String[0]))));
        selectorPanel.add(generateScrollJPanel("Game:", new JList<>(games.toArray(new String[0]))));
        this.add(selectorPanel, BorderLayout.WEST);

        // Achievement list on the right
        JPanel achievementPanel = generateScrollJPanel("Achievements:", new JList<>(achievements.toArray(new String[0])));
        achievementPanel.setBorder(new EmptyBorder(0, 15, 0, 0));
        this.add(achievementPanel);

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

    private JPanel generateScrollJPanel(String label, JList<String> selectItems){
        JScrollPane scrollPane = new JScrollPane(selectItems);
        selectItems.setLayoutOrientation(JList.VERTICAL);
        // Set preferred size so that scrolling is only needed if window is shrunk.
        scrollPane.setPreferredSize(new Dimension(250, selectItems.getModel().getSize()*18));
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
}
