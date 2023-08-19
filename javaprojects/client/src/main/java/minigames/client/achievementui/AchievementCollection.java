package minigames.client.achievementui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AchievementCollection {
    private final List<AchievementPresenter> achievements;
    private final String gameID;
    public AchievementCollection (String gameID, Collection<AchievementPresenter> achievements) {
        this.achievements = new ArrayList<>(achievements);
        this.gameID = gameID;
    }

    public JPanel achievementListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < achievements.size(); i++) {
            AchievementPresenter ap = achievements.get(i);
            JPanel achievementPanel = ap.smallAchievementPanel(true);
            achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.X_AXIS));
            achievementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            int index = i;
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

    public JPanel achievementCarousel(int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JButton("<"), BorderLayout.WEST);
        panel.add(new JButton(">"), BorderLayout.EAST);
        JLabel indexLabel = new JLabel("");
        indexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(indexLabel, BorderLayout.SOUTH);
        updateCarousel(index, panel);
        panel.setPreferredSize(new Dimension(700, 400));

        return panel;
    }

    private void updateCarousel(int index, JPanel carouselPanel) {
        if (index < 0 || index >= achievements.size()) return;
        BorderLayout layout = (BorderLayout) carouselPanel.getLayout();

        carouselPanel.add(achievements.get(index).largeAchievementPanel());

        JLabel southLabel = (JLabel) layout.getLayoutComponent(BorderLayout.SOUTH);
        southLabel.setText("Unlocked achievement " + (index + 1) + " of " + achievements.size());

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
