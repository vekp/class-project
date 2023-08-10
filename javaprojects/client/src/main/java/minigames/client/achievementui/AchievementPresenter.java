package minigames.client.achievementui;

import minigames.achievements.Achievement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

public class AchievementPresenter {

    private final String achievementImageFolderLocation = "src/main/resources/images/achievements/";
    private final Achievement achievement;
    //TODO: Add a sound effect?
    final boolean isUnlocked;

    public AchievementPresenter(Achievement achievement, boolean isUnlocked) {
        this.achievement = achievement;
        this.isUnlocked = isUnlocked;
    }

    private ImageIcon makeImage(int size) {
        // Attempt to set path of image
        String path = achievementImageFolderLocation + achievement.mediaFileName().toLowerCase().replace(" ", "") + ".png";
        System.out.println(path);
        if (!new File(path).exists()) {
            path = path.replace(".png", ".jpg");
            if (!new File(path).exists()) {
                path = path.replace(".jpg", ".gif");
                // Use default if not found
                if (!new File(path).exists()) {
                    path = achievementImageFolderLocation + "default.png";
                }
            }
        }
        // Scale to desired size
        ImageIcon imageIcon = new ImageIcon(path);
        Image scaledImage = imageIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
        imageIcon.setImage(scaledImage);
        return imageIcon;
    }

    public JPanel smallAchievementPanel() {
        JLabel name = new JLabel(achievement.name());
        name.setBorder(new LineBorder(Color.BLACK));
        Font currentFont = name.getFont();
        Font boldFont = new Font(currentFont.getFontName(), Font.BOLD, currentFont.getSize());
        name.setFont(boldFont);

        JTextArea description = new JTextArea(achievement.description());
        description.setBorder(new LineBorder(Color.BLACK));

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(Color.BLACK));
        panel.add(new JLabel(makeImage(60)));
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(name, BorderLayout.NORTH);
        textPanel.add(description);
        panel.add(textPanel);
        if (!isUnlocked) {
            name.setForeground(Color.GRAY);
            description.setForeground(Color.GRAY);

            //todo Can we blur this out instead?
            if (achievement.hidden()) {
                panel.setBackground(Color.ORANGE);
                description.setText("This is a hidden achievement. What do you think you need to do to earn it?");
            }
        } else if (achievement.hidden()) panel.setBackground(Color.YELLOW);
        return panel;
    }

}
