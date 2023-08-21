package minigames.client.achievementui;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
import minigames.achievements.PlayerAchievementRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class for presenting a group of achievements for a particular game
 */
public class AchievementCollection {
    private final List<AchievementPresenter> achievements = new ArrayList<>();
    private final String gameID;
    private final int unlockedQty;

    /**
     * Constructor for AchievementCollection using Achievement collection
     * @param gameID the name of the game
     * @param achievements a Collection of Achievements
     * @param isUnlocked whether the achievement has been unlocked by player
     */
    public AchievementCollection(String gameID, Collection<Achievement> achievements, boolean isUnlocked) {
        this.gameID = gameID;
        for (Achievement a : achievements) {
            AchievementPresenter ap = new AchievementPresenter(a, isUnlocked);
            this.achievements.add(ap);
        }
        unlockedQty = achievements.size();
    }

    public AchievementCollection(GameAchievementState gaState) {
        this.gameID = gaState.gameID();
        unlockedQty = gaState.unlocked().size();
        for (Achievement a : gaState.unlocked()) achievements.add(new AchievementPresenter(a, true));
        for (Achievement a : gaState.locked()) achievements.add(new AchievementPresenter(a, false));

    }

    /**
     * Constructor using PlayerAchievementRecord
     * @param gameID ID of the game
     * @param data the PlayerAchievementRecord
     */
    public AchievementCollection(String gameID, PlayerAchievementRecord data) {
        this.gameID = gameID;
        for (GameAchievementState gaState : data.gameAchievements()) {
            if (gaState.gameID().equals(gameID)) {
                unlockedQty = gaState.unlocked().size();
                for (Achievement a : gaState.unlocked()) achievements.add(new AchievementPresenter(a, true));
                for (Achievement a : gaState.locked()) achievements.add(new AchievementPresenter(a, false));
                return;
            }
        }
        unlockedQty = 0;
    }

    /**
     * Create a panel containing a list of achievements
     */
    public JPanel achievementListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < achievements.size(); i++) {
            AchievementPresenter ap = achievements.get(i);
            JPanel achievementPanel = ap.smallAchievementPanel(true);
            achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
            achievementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            int index = i;
            // If achievement is unlocked, add mouse click listener to view it in carousel
            if (ap.isUnlocked) {
                achievementPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(panel.getTopLevelAncestor(), achievementCarousel(index), gameID, JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
            panel.add(achievementPanel);
        }
        return panel;
    }

    /**
     * Create a carousel view with large images of unlocked achievements
     * @param index the position to start at
     * @return a JPanel containing the carousel
     */
    public JPanel achievementCarousel(int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JButton("<"), BorderLayout.WEST);
        panel.add(new JButton(">"), BorderLayout.EAST);
        JLabel indexLabel = new JLabel();
        indexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(indexLabel, BorderLayout.SOUTH);
        updateCarousel(index, panel);
        panel.setPreferredSize(new Dimension(700, 400));

        return panel;
    }

    /**
     * Update the elements in the carousel
     * @param index the position in the list
     * @param carouselPanel the JPanel containing the carousel
     */
    private void updateCarousel(int index, JPanel carouselPanel) {
        if (index < 0 || index >= unlockedQty) return;
        BorderLayout layout = (BorderLayout) carouselPanel.getLayout();

        carouselPanel.add(achievements.get(index).largeAchievementPanel());

        JLabel southLabel = (JLabel) layout.getLayoutComponent(BorderLayout.SOUTH);
        southLabel.setText("Unlocked achievement " + (index + 1) + " of " + unlockedQty);

        JButton leftButton = (JButton) layout.getLayoutComponent(BorderLayout.WEST);
        leftButton.setPreferredSize(new Dimension(50, leftButton.getHeight()));
        JButton rightButton = (JButton) layout.getLayoutComponent(BorderLayout.EAST);
        rightButton.setPreferredSize(new Dimension(50, rightButton.getHeight()));

        for (ActionListener al : leftButton.getActionListeners()) leftButton.removeActionListener(al);
        leftButton.addActionListener(e -> updateCarousel(index - 1, carouselPanel));
        for (ActionListener al : rightButton.getActionListeners()) rightButton.removeActionListener(al);
        rightButton.addActionListener(e -> updateCarousel(index + 1, carouselPanel));

        leftButton.setEnabled(!(index == 0));
        rightButton.setEnabled(!(index == unlockedQty - 1));
    }

}
