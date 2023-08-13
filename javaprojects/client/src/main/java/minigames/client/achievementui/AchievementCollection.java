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
        String title = "<html><b>New achievement" + (achievements.size() > 1 ? "s" : "") + " unlocked!</b></html>";
        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JButton("<"), BorderLayout.WEST);
        panel.add(new JButton(">"), BorderLayout.EAST);
        JLabel indexLabel = new JLabel("");
        indexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(indexLabel, BorderLayout.SOUTH);
        updateCarousel(0, panel);

        return panel;
    }

    private void updateCarousel(int index, JPanel carouselPanel) {
        if (index < 0 || index >= achievements.size()) return;
        BorderLayout layout = (BorderLayout) carouselPanel.getLayout();

        Component centreComponent = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centreComponent != null) carouselPanel.remove(centreComponent);
        carouselPanel.add(achievements.get(index).largeAchievementPanel());

        JLabel southLabel = (JLabel) layout.getLayoutComponent(BorderLayout.SOUTH);
        southLabel.setText("Achievement " + (index + 1) + " of " + achievements.size());

        JButton leftButton = (JButton) layout.getLayoutComponent(BorderLayout.WEST);
        leftButton.setPreferredSize(new Dimension(50, leftButton.getHeight()));
        JButton rightButton = (JButton) layout.getLayoutComponent(BorderLayout.EAST);
        rightButton.setPreferredSize(new Dimension(50, rightButton.getHeight()));

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
