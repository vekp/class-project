package minigames.client.achievementui;

import minigames.achievements.Achievement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;

public class AchievementPresenter {

    private final String achievementImageFolderLocation = "src/main/resources/images/achievements/";
    private final Achievement achievement;
    //TODO: Add a sound effect?
    final boolean isUnlocked;

    public AchievementPresenter(Achievement achievement, boolean isUnlocked) {
        this.achievement = achievement;
        this.isUnlocked = isUnlocked;
    }

    private ImageIcon makeScaledImage(int size) {
        // Attempt to set path of image
        String path = (achievement.hidden() && !isUnlocked) ? achievementImageFolderLocation + "mystery.png" :
                achievementImageFolderLocation + achievement.mediaFileName().toLowerCase().replace(" ", "") + ".png";
        System.out.println(path);
        if (!new File(path).exists()) {
            path = path.replace(".png", ".jpg");
            if (!new File(path).exists()) {
                path = path.replace(".jpg", ".gif");
                // Use default if not found
                if (!new File(path).exists()) {
                    path = achievementImageFolderLocation + "goldbadge.png";
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
//        name.setBorder(new LineBorder(Color.BLACK));
        Font currentFont = name.getFont();
        Font boldFont = new Font(currentFont.getFontName(), Font.BOLD, currentFont.getSize());
        name.setFont(boldFont);

        // Todo: make description a jtextpane.  Jtextarea can't be clicked on.
        JLabel description = new JLabel(achievement.description());
        description.setVerticalAlignment(JLabel.TOP);
//        description.setBorder(new LineBorder(Color.BLACK));

        JPanel panel = new JPanel();
//        panel.setBorder(new LineBorder(Color.BLACK));
        JLabel image = new JLabel(makeScaledImage(60));
        panel.add(image);
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(name, BorderLayout.NORTH);
        textPanel.add(description);
        panel.add(textPanel);
        if (!isUnlocked) {
            name.setForeground(Color.LIGHT_GRAY);
            description.setForeground(Color.LIGHT_GRAY);
            if (achievement.hidden()) {
                description.setText(makeRandomText(achievement.description()));
            }

        } else if (achievement.hidden()) panel.setBackground(Color.YELLOW);

        // Show large panel if clicked on.
        if (isUnlocked) {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(panel), largeAchievementPanel(),
                            "Your achievement", JOptionPane.PLAIN_MESSAGE);
                }
            });
        }
        return panel;
    }

    public JPanel largeAchievementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel image = new JLabel(makeScaledImage(200));
        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(image);

        JLabel name = new JLabel(achievement.name());
        Font nameFont = new Font(name.getFont().getFontName(), Font.BOLD, 36);
        name.setFont(nameFont);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(name);

        JLabel description = new JLabel(achievement.description());
        Font descriptionFont = new Font(description.getFont().getFontName(), Font.PLAIN, 20);
        description.setFont(descriptionFont);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(description);

        return panel;
    }
    private String makeRandomText(String text) {
        StringBuilder randomText = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < text.length(); i++) {
            char c;
            if (Character.isUpperCase(text.charAt(i))) c = (char)(random.nextInt(26) + 'A');
            else if (Character.isLowerCase(text.charAt(i))) c = (char)(random.nextInt(26) + 'a');
            else if (Character.isDigit(text.charAt(i))) c = (char) (random.nextInt(10) + '0');
            else c = text.charAt(i);
            randomText.append(c);
        }
        return randomText.toString();
    }

}
