package minigames.client.achievements;

import minigames.achievements.Achievement;
import minigames.utilities.MinigameUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Class for presenting individual achievement information to user
 */
public class AchievementPresenter {

    private final String achievementImageFolderLocation = "src/main/resources/images/achievements/";
    private final Achievement achievement;
    final boolean isUnlocked;
    private final Color hiddenUnlockedColour = new Color(0, 51, 204);
    private final Color hiddenLockedColour = new Color(179, 195, 255);
    private final Color lockedColour = Color.LIGHT_GRAY;

    /**
     * Constructor for this class
     * @param achievement the Achievement
     * @param isUnlocked whether it has been unlocked
     */
    public AchievementPresenter(Achievement achievement, boolean isUnlocked) {
        this.achievement = achievement;
        this.isUnlocked = isUnlocked;
    }

    /**
     * Create image of achievement of given height
     * @param targetSize the desired size of image
     * @return an ImageIcon
     */
    private ImageIcon achievementImage(int targetSize) {

        // Set path of image
        String path;
        if (achievement.hidden() && !isUnlocked) path = achievementImageFolderLocation + "mystery.png";
        else {
            path = achievementImageFolderLocation + "goldbadge.png";
            for (String extension : new String[] {".png", ".jpg", ".gif"}) {
                String currentPath = achievementImageFolderLocation + achievement.mediaFileName().toLowerCase().
                        replace(" ", "") + extension;
                if (new File(currentPath).exists()) {
                    path = currentPath;
                    break;
                }
            }
        }

        ImageIcon scaledImage =  MinigameUtils.scaledImage(path, targetSize);
        if (isUnlocked) return scaledImage;
        // Apply grey filter to locked items
        Image grayedImage = GrayFilter.createDisabledImage(scaledImage.getImage());
        return new ImageIcon(grayedImage);
    }

    /**
     * Create a small panel to present to user, for use in popup notifications.
     */
    public JPanel smallAchievementPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        int imageSize = 32;
        JLabel image = new JLabel(achievementImage(imageSize));
        image.setPreferredSize(new Dimension(imageSize, imageSize));

        JLabel name = new JLabel("<html><p style='font-size: 1.2em'>Achievement unlocked</p>"
                 + achievement.name() + "</html>");
        if (achievement.hidden()) name.setForeground(hiddenUnlockedColour);

        panel.add(image);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(name);

        return panel;
    }

    /**
     * Create a medium-sized panel with achievement details to be shown in a list
     * @return A JPanel
     */
    public JPanel mediumAchievementPanel() {
        int imageSize = 50;
        JLabel text = new JLabel("<html><h3 style='margin: 0'>" + achievement.name() +
                "</h3>" + achievement.description() + "</html>");

        JPanel panel = new JPanel();
        JLabel image = new JLabel(achievementImage(imageSize));
        image.setPreferredSize(new Dimension(imageSize, imageSize));
        image.setBorder(new EmptyBorder(4, 4, 4, 4));

        panel.add(image);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(text);

        if (!isUnlocked) {
            text.setForeground(achievement.hidden() ? hiddenLockedColour : lockedColour);
            text.setName("Locked achievement text"); // Tag the text for applyStyling
        }
        else if (achievement.hidden()) text.setForeground(hiddenUnlockedColour);

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        return panel;
    }

    /**
     * Create a large JPanel with achievement image, name and description
     */
    public JPanel largeAchievementPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (!isUnlocked) {
            panel.add(new JLabel("<html><body style='text-align: center'><h1>403 Forbidden</h1>" +
                    "You must unlock this achievement to have access.</body></html>"));
            return panel;
        }

        JLabel image = new JLabel(achievementImage(192));
        JLabel name = new JLabel("<html><h1 style='text-align:center; margin-top: 20px'>" + achievement.name() + "</h1></html>", SwingConstants.CENTER);
        JLabel description = new JLabel("<html><h2 style='text-align:center; font-weight:normal;'>" + achievement.description() + "</h2></html>", SwingConstants.CENTER);

        if (achievement.hidden()) {
            name.setForeground(hiddenUnlockedColour);
            description.setForeground(hiddenUnlockedColour);
        }

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        panel.add(image, gbc);
        panel.add(name, gbc);
        panel.add(description, gbc);

        panel.setPreferredSize(new Dimension(AchievementCarousel.carouselPanelWidth, AchievementCarousel.carouselPanelHeight));

        return panel;
    }


}
