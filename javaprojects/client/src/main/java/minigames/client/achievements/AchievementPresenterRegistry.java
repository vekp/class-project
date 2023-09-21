package minigames.client.achievements;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
import minigames.client.Animator;
import minigames.client.notifications.DialogManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static minigames.utilities.MinigameUtils.generateScrollPane;

/**
 * A class for presenting a group of achievements for a particular game
 */
public class AchievementPresenterRegistry {
    final List<AchievementPresenter> achievements = new ArrayList<>();
    private final String gameID;
    DialogManager dialogManager;
    AchievementCarousel achievementCarousel;

    /**
     * Construct an AchievementPresenterRegistry from a GameAchievementState
     * @param gaState GameAchievementState received from server
     */
    public AchievementPresenterRegistry(GameAchievementState gaState, Animator animator) {
        this.gameID = gaState.gameID();
        this.achievementCarousel = new AchievementCarousel(this, animator, gaState.unlocked().size());
        for (Achievement a : gaState.unlocked()) achievements.add(new AchievementPresenter(a, true));
        for (Achievement a : gaState.locked()) achievements.add(new AchievementPresenter(a, false));
    }

    /**
     * Create a panel containing a list of achievements
     */
    public JPanel achievementListPanel(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < achievements.size(); i++) {
            AchievementPresenter ap = achievements.get(i);
            JPanel achievementPanel = ap.mediumAchievementPanel();
            achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
            achievementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            // If achievement is unlocked, add mouse click listener to view it in carousel
            if (ap.isUnlocked) {
                makeClickable(achievementPanel, i);
            }
            panel.add(achievementPanel);
        }
        return panel;
    }

    /**
     * Make an achievement panel clickable with border effects and click action to display carousel
     * @param panel the achievement panel
     * @param index the position in the carousel
     */
    private void makeClickable(JPanel panel, int index) {
        Border smallEmptyBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        Border mouseOverBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(smallEmptyBorder,
                BorderFactory.createBevelBorder(BevelBorder.RAISED)), smallEmptyBorder);
        Border mouseDownBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(smallEmptyBorder,
                BorderFactory.createBevelBorder(BevelBorder.LOWERED)), smallEmptyBorder);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dialogManager != null) {
                    dialogManager.showMessageDialog(gameID + " achievements", achievementCarousel.achievementCarouselPanel(index));
                } else {
                    JOptionPane.showMessageDialog(panel.getTopLevelAncestor(),achievementCarousel.achievementCarouselPanel(index),
                            gameID + " achievements", JOptionPane.PLAIN_MESSAGE);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                panel.setBorder(mouseDownBorder);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(mouseOverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
        });
    }

    /**
     * Display the player's achievements for the current game in a scroll pane message dialog using the
     * given NotificationManager
     */
    public void showGameAchievements(DialogManager dialogManager) {
        JScrollPane scrollPane = generateScrollPane(achievementListPanel(dialogManager));
        // Set pane size to fit inside frame
        scrollPane.setPreferredSize(new Dimension(
            Math.min(700, scrollPane.getPreferredSize().width + 20), // add some padding on the right
            Math.min(400, scrollPane.getPreferredSize().height)
            )
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        scrollPane.setBorder(null);
        dialogManager.showMessageDialog(gameID + " achievements", scrollPane);
    }
}
