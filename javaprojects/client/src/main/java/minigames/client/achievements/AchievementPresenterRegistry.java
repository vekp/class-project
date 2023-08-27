package minigames.client.achievements;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for presenting a group of achievements for a particular game
 */
public class AchievementPresenterRegistry {
    private final List<AchievementPresenter> achievements = new ArrayList<>();
    private final String gameID;
    private final int unlockedQty;


    /**
     * Construct an AchievementPresenterRegistry from a GameAchievementState
     * @param gaState GameAchievementState received from server
     */
    public AchievementPresenterRegistry(GameAchievementState gaState) {
        this.gameID = gaState.gameID();
        unlockedQty = gaState.unlocked().size();
        for (Achievement a : gaState.unlocked()) achievements.add(new AchievementPresenter(a, true));
        for (Achievement a : gaState.locked()) achievements.add(new AchievementPresenter(a, false));

    }

    /**
     * Create a panel containing a list of achievements
     */
    public JPanel achievementListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < achievements.size(); i++) {
            AchievementPresenter ap = achievements.get(i);
            JPanel achievementPanel = ap.mediumAchievementPanel(true);
            achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
            achievementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            int index = i;
            // If achievement is unlocked, add mouse click listener to view it in carousel
            if (ap.isUnlocked) {
                achievementPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(panel.getTopLevelAncestor(), achievementCarousel(index),
                                gameID + " achievements", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
            panel.add(achievementPanel);
        }
        return panel;
    }

    /**
     * Display the player's achievements for the current game in a scroll pane message dialog
     * @param frame the parent frame
     */
    public void showGameAchievements(JFrame frame) {
        JScrollPane scrollPane = AchievementUI.generateScrollPane(achievementListPanel());
        // Set pane size to fit inside frame
        scrollPane.setPreferredSize(new Dimension(
            Math.min(700, scrollPane.getPreferredSize().width + 20), // add some padding on the right
            Math.min(500, scrollPane.getPreferredSize().height)
            )
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        scrollPane.setBorder(null);
        JOptionPane.showMessageDialog(frame, scrollPane,
                gameID + " achievements", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Create a carousel view with large images of unlocked achievements
     * @param index the position to start at
     * @return a JPanel containing the carousel
     */
    public JPanel achievementCarousel(int index) {
        // No carousel required if only 1 unlocked
        if (unlockedQty == 1) return achievements.get(0).largeAchievementPanel();

        JPanel panel = new JPanel(new BorderLayout());
        // Add prev/next buttons
        panel.add(new JButton("<"), BorderLayout.WEST);
        panel.add(new JButton(">"), BorderLayout.EAST);
        // JLabel to show current position
        JLabel positionLabel = new JLabel();
        positionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(positionLabel, BorderLayout.SOUTH);
        updateCarousel(index, panel);
        panel.setPreferredSize(new Dimension(700, 400));

        return panel;
    }

    /**
     * Update the elements in the carousel
     * @param index the current position in the list
     * @param carouselPanel the JPanel containing the carousel
     */
    private void updateCarousel(int index, JPanel carouselPanel) {
        if (index < 0 || index >= unlockedQty) return;
        BorderLayout layout = (BorderLayout) carouselPanel.getLayout();

        // Update currently displayed achievement
        carouselPanel.add(achievements.get(index).largeAchievementPanel());

        // Update current position label
        JLabel southLabel = (JLabel) layout.getLayoutComponent(BorderLayout.SOUTH);
        southLabel.setText("Unlocked achievement " + (index + 1) + " of " + unlockedQty);

        JButton leftButton = (JButton) layout.getLayoutComponent(BorderLayout.WEST);
        leftButton.setPreferredSize(new Dimension(50, leftButton.getHeight()));
        JButton rightButton = (JButton) layout.getLayoutComponent(BorderLayout.EAST);
        rightButton.setPreferredSize(new Dimension(50, rightButton.getHeight()));

        // Update actions for buttons
        for (ActionListener al : leftButton.getActionListeners()) leftButton.removeActionListener(al);
        leftButton.addActionListener(e -> updateCarousel(index - 1, carouselPanel));
        for (ActionListener al : rightButton.getActionListeners()) rightButton.removeActionListener(al);
        rightButton.addActionListener(e -> updateCarousel(index + 1, carouselPanel));

        // Enable/disable buttons at either end of carousel
        leftButton.setEnabled(!(index == 0));
        rightButton.setEnabled(!(index == unlockedQty - 1));
    }

}
