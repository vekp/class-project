package minigames.client.achievements;

import minigames.achievements.Achievement;
import minigames.achievements.GameAchievementState;
import minigames.client.Animator;
import minigames.client.Tickable;
import minigames.client.notifications.NotificationManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for presenting a group of achievements for a particular game
 */
public class AchievementPresenterRegistry implements Tickable {
    private final List<AchievementPresenter> achievements = new ArrayList<>();
    private final String gameID;
    private final int unlockedQty;
    private JPanel centrePanel;
    // Size of central carousel panel
    static final int panelWidth = 600;
    static final int panelHeight = 400;
    private JButton leftButton;
    private JButton rightButton;
    private JLabel southLabel;
    private JPanel currentAchievement;
    private JPanel nextAchievement;
    private int position;
    private enum Direction {
        LEFT(panelWidth),
        RIGHT(-panelWidth);
        final private int initialX;
        float currentX;

        Direction(int initialX) {
            this.initialX = initialX;
        }

    }
    // Direction of animation movement of the panels
    private Direction direction;
    private final Animator animator;


    /**
     * Construct an AchievementPresenterRegistry from a GameAchievementState
     * @param gaState GameAchievementState received from server
     */
    public AchievementPresenterRegistry(GameAchievementState gaState, Animator animator) {
        this.animator = animator;
        this.gameID = gaState.gameID();
        unlockedQty = gaState.unlocked().size();
        for (Achievement a : gaState.unlocked()) achievements.add(new AchievementPresenter(a, true));
        for (Achievement a : gaState.locked()) achievements.add(new AchievementPresenter(a, false));
    }

    /**
     * Create a panel containing a list of achievements
     */
    public JPanel achievementListPanel(NotificationManager notificationManager) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < achievements.size(); i++) {
            AchievementPresenter ap = achievements.get(i);
            JPanel achievementPanel = ap.mediumAchievementPanel();
            achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
            achievementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            // If achievement is unlocked, add mouse click listener to view it in carousel
            if (ap.isUnlocked) {
                makeClickable(achievementPanel, i, notificationManager);
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
    private void makeClickable(JPanel panel, int index, NotificationManager notificationManager) {
        Border smallEmptyBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        Border mouseOverBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(smallEmptyBorder,
                BorderFactory.createBevelBorder(BevelBorder.RAISED)), smallEmptyBorder);
        Border mouseDownBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(smallEmptyBorder,
                BorderFactory.createBevelBorder(BevelBorder.LOWERED)), smallEmptyBorder);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (notificationManager != null) {
                    notificationManager.showMessageDialog(gameID + " achievements", achievementCarousel(index));
                } else {
                    JOptionPane.showMessageDialog(panel.getTopLevelAncestor(), achievementCarousel(index),
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
    public void showGameAchievements(NotificationManager notificationManager) {
        JScrollPane scrollPane = AchievementUI.generateScrollPane(achievementListPanel(notificationManager));
        // Set pane size to fit inside frame
        scrollPane.setPreferredSize(new Dimension(
            Math.min(700, scrollPane.getPreferredSize().width + 20), // add some padding on the right
            Math.min(400, scrollPane.getPreferredSize().height)
            )
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        scrollPane.setBorder(null);
        notificationManager.showMessageDialog(gameID + " achievements", scrollPane);
    }

    /**
     * Create a carousel view with large images of unlocked achievements
     * @param index the position to start at
     * @return a JPanel containing the carousel
     */
    public JPanel achievementCarousel(int index) {
        // No carousel required if only 1 achievement unlocked
        if (unlockedQty == 1) return achievements.get(0).largeAchievementPanel();

        JPanel panel = new JPanel(new BorderLayout());
        centrePanel = new JPanel(null);
        panel.add(centrePanel, BorderLayout.CENTER);

        // Add prev/next buttons
        leftButton = new JButton("<");
        panel.add((leftButton), BorderLayout.WEST);
        rightButton = new JButton(">");
        panel.add(rightButton, BorderLayout.EAST);

        // JLabel to show current position
        southLabel = new JLabel();
        southLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(southLabel, BorderLayout.SOUTH);
        centrePanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        leftButton.setPreferredSize(new Dimension(50, panelHeight));
        rightButton.setPreferredSize(new Dimension(50, panelHeight));

        updateCarousel(index);
        return panel;
    }

    /**
     * Update the elements in the carousel
     * @param index the current position in the list
     */
    private void updateCarousel(int index) {
        position = index;
        if (index < 0 || index >= unlockedQty) return;

        // Update currently displayed achievement
        nextAchievement = achievements.get(index).largeAchievementPanel();
        animator.requestTick(this);

        // Update current position label
        southLabel.setText("Unlocked achievement " + (index + 1) + " of " + unlockedQty);

        // Update actions for buttons
        for (ActionListener al : leftButton.getActionListeners()) leftButton.removeActionListener(al);
        leftButton.addActionListener(e -> {
            direction = Direction.RIGHT;
            nextAchievement = achievements.get(index - 1).largeAchievementPanel();
            position = index - 1;
            updateCarousel(position);
        });
        for (ActionListener al : rightButton.getActionListeners()) rightButton.removeActionListener(al);
        rightButton.addActionListener(e -> {
            direction = Direction.LEFT;
            nextAchievement = achievements.get(index + 1).largeAchievementPanel();
            position = index + 1;
            updateCarousel(position);
        });

        // Enable/disable buttons at either end of carousel
        setButtons();
    }

    /**
     * Animate the outgoing and incoming achievement panels
     */
    @Override
    public void tick(Animator al, long now, long delta) {
        // No animation required when first displayed
        if (direction == null) {
            nextAchievement.setBounds(0, 0, panelWidth, panelHeight);
            centrePanel.add(nextAchievement);
            currentAchievement = nextAchievement;
            return;
        }
        // Position and add the incoming achievement panel
        if (nextAchievement.getBounds().equals(new Rectangle(0, 0, 0, 0))) {
            direction.currentX = direction.initialX;
            nextAchievement.setBounds(direction.initialX, 0, panelWidth, panelHeight);
            centrePanel.add(nextAchievement);
        }
        // Disable buttons for animation duration
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        // Calculate and set new positions
        direction.currentX *= 0.8;
        int newX = Math.round(direction.currentX);
        currentAchievement.setLocation(newX - direction.initialX, 0);
        nextAchievement.setLocation(newX, 0);
        // Stop if final position reached
        if (newX == 0) {
            direction = null;
            centrePanel.remove(currentAchievement);
            currentAchievement = nextAchievement;
            nextAchievement = null;
            setButtons();
            return;
        }
        animator.requestTick(this);
    }

    /**
     * Set enabled state of carousel buttons based on position to maintain valid position
     */
    private void setButtons() {
        leftButton.setEnabled(position > 0);
        rightButton.setEnabled(position < unlockedQty - 1);
    }
}
