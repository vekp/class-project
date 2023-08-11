package minigames.client.achievementui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AchievementCollection {
    private final List<AchievementPresenter> achievements;
    public AchievementCollection (Collection<AchievementPresenter> achievements) {
        this.achievements = new ArrayList<>(achievements);
    }

    public JPanel achievementListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (AchievementPresenter ap : achievements) {
            JPanel achievementPanel = ap.smallAchievementPanel(true);
            achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
            achievementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(achievementPanel);
        }
        return panel;
    }

}
