package minigames.client.achievements;

import minigames.client.Animator;
import minigames.client.Tickable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Class for presenting players with an animated carousel of their unlocked achievements.
 */

public class AchievementCarousel implements Tickable {
    /**
     * Enum Direction indicates the current status of the panel, whether at rest, moving left, or right.
     */
    private enum Direction {
        NONE(0), LEFT(carouselPanelWidth), RIGHT(-carouselPanelWidth);
        final private int initialX;
        Direction(int initialX) {
            this.initialX = initialX;
        }

    }
    private Direction direction = Direction.NONE;
    private final AchievementPresenterRegistry apRegistry;
    private final int unlockedQty;
    private JPanel centrePanel;
    private JButton leftButton;
    private JButton rightButton;
    private JLabel southLabel;
    private int position;
    private JPanel currentAchievement;
    private JPanel nextAchievement;
    private float currentX;

    // Size of central carousel panel
    static final int carouselPanelWidth = 600;
    static final int carouselPanelHeight = 400;
    private final Animator animator;


    /**
     * Constructor for AchievementCarousel
     */
    public AchievementCarousel(AchievementPresenterRegistry apRegistry, Animator animator, int unlockedQty) {
        this.apRegistry = apRegistry;
        this.unlockedQty = unlockedQty;
        this.animator = animator;
    }

    /**
     * Create a carousel view with large images of unlocked achievements
     * @param index the position to start at
     * @return a JPanel containing the carousel
     */
    public JPanel achievementCarouselPanel(int index) {
        // No carousel required if only 1 achievement unlocked
        if (unlockedQty == 1) return apRegistry.achievements.get(0).largeAchievementPanel();

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
        centrePanel.setPreferredSize(new Dimension(carouselPanelWidth, carouselPanelHeight));
        leftButton.setPreferredSize(new Dimension(50, carouselPanelHeight));
        rightButton.setPreferredSize(new Dimension(50, carouselPanelHeight));
        updateCarousel(index);
        return panel;
    }

    /**
     * Update the elements in the carousel
     * @param index the current position in the list
     */
    private void updateCarousel(int index) {
        if (index < 0 || index >= unlockedQty) return;
        position = index;

        // Update current position label
        southLabel.setText("Unlocked achievement " + (index + 1) + " of " + unlockedQty);

        // Update actions for buttons
        for (ActionListener al : leftButton.getActionListeners()) leftButton.removeActionListener(al);
        leftButton.addActionListener(e -> {
            direction = Direction.RIGHT;
            updateCarousel(position - 1);
        });
        for (ActionListener al : rightButton.getActionListeners()) rightButton.removeActionListener(al);
        rightButton.addActionListener(e -> {
            direction = Direction.LEFT;
            updateCarousel(position + 1);
        });

        // Enable/disable buttons at either end of carousel
        setButtons();

        // Update currently displayed achievement
        nextAchievement = apRegistry.achievements.get(index).largeAchievementPanel();
        apRegistry.dialogManager.applyStyling(nextAchievement);

        // When first opened, show achievement panel immediately.
        if (direction.equals(Direction.NONE)) {
            nextAchievement.setBounds(0, 0, carouselPanelWidth, carouselPanelHeight);
            centrePanel.add(nextAchievement);
            currentAchievement = nextAchievement;
        } else {
            //Otherwise, animate
            animator.requestTick(this);
        }
    }

    /**
     * Set enabled state of carousel buttons based on position to maintain valid position
     */
    private void setButtons() {
        leftButton.setEnabled(position > 0);
        rightButton.setEnabled(position < unlockedQty - 1);
    }

    /**
     * Animate the outgoing and incoming achievement panels
     */
    @Override
    public void tick(Animator al, long now, long delta) {
        // Position and add the incoming achievement panel
        if (nextAchievement.getBounds().equals(new Rectangle(0, 0, 0, 0))) {
            currentX = direction.initialX;
            nextAchievement.setBounds(direction.initialX, 0, carouselPanelWidth, carouselPanelHeight);
            centrePanel.add(nextAchievement);
            // Disable buttons for animation duration
            leftButton.setEnabled(false);
            rightButton.setEnabled(false);
            animator.requestTick(this);
            return;
        }

        // Calculate and set new positions
        currentX *= 0.8;
        int newX = Math.round(currentX);
        currentAchievement.setLocation(newX - direction.initialX, 0);
        nextAchievement.setLocation(newX, 0);

        // Stop if final position reached
        if (newX == 0) {
            direction = Direction.NONE;
            centrePanel.remove(currentAchievement);
            currentAchievement = nextAchievement;
            nextAchievement = null;
            setButtons();
            return;
        }
        animator.requestTick(this);
    }
}
