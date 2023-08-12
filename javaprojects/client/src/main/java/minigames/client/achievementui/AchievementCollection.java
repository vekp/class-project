package minigames.client.achievementui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public JPanel achievementCarousel() {
        JPanel panel = new JPanel(new BorderLayout());
        String title = "New Achievement" + (achievements.size() > 1 ? "s" : "") + " unlocked";
        panel.add(new JLabel(title), BorderLayout.NORTH);
        panel.add(new JButton("<"), BorderLayout.WEST);
        panel.add(new JButton(">"), BorderLayout.EAST);
        panel.add(new JLabel(""), BorderLayout.SOUTH);
        updateCarousel(0, panel);

        return panel;
    }

    private void updateCarousel(int index, JPanel carouselPanel) {
        System.out.println(index+" of "+ achievements.size());
        if (index < 0 || index >= achievements.size()) return;
        BorderLayout layout = (BorderLayout) carouselPanel.getLayout();

        Component centreComponent = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centreComponent != null) carouselPanel.remove(centreComponent);
        carouselPanel.add(achievements.get(index).largeAchievementPanel());

        JLabel southLabel = (JLabel) layout.getLayoutComponent(BorderLayout.SOUTH);
        southLabel.setText("Achievement " + (index + 1) + " of " + achievements.size());

        JButton leftButton = (JButton) layout.getLayoutComponent(BorderLayout.WEST);
        JButton rightButton = (JButton) layout.getLayoutComponent(BorderLayout.EAST);

        for (ActionListener al : leftButton.getActionListeners()) leftButton.removeActionListener(al);
        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCarousel(index - 1, carouselPanel);
            }
        });
        for (ActionListener al : rightButton.getActionListeners()) rightButton.removeActionListener(al);
        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCarousel(index + 1, carouselPanel);
            }
        });

        leftButton.setEnabled(!(index == 0));
        rightButton.setEnabled(!(index == achievements.size() - 1));
    }

}
