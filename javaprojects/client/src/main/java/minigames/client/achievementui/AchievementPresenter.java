package minigames.client.achievementui;

import minigames.achievements.Achievement;

import javax.swing.*;
import javax.swing.border.*;
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
    private final Color hiddenUnlockedColour = new Color(0, 51, 204);
    private final Color hiddenLockedColour = new Color(204, 216, 255);
    private final Color lockedColour = Color.LIGHT_GRAY;

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

    public JPanel smallAchievementPanel(boolean isClickable) {
        Border smallEmptyBorder = new EmptyBorder(4, 4, 4, 4);
        Border largeEmptyBorder = new EmptyBorder(10, 10, 10, 10);

        JLabel name = new JLabel(achievement.name());
//        name.setBorder(new LineBorder(Color.BLACK));
        Font currentFont = name.getFont();
        Font boldFont = new Font(currentFont.getFontName(), Font.BOLD, currentFont.getSize());
        name.setFont(boldFont);

        // Todo: make description a jtextpane so it wraps.  Jtextarea can't be clicked on.
        JLabel description = new JLabel(achievement.description());
        description.setVerticalAlignment(JLabel.TOP);
//        description.setBorder(new LineBorder(Color.BLACK));

        JPanel panel = new JPanel();
//        panel.setBorder(new LineBorder(Color.BLACK));
        JLabel image = new JLabel(makeScaledImage(50));
        if (achievement.hidden() && isUnlocked) {
            image.setBackground(Color.YELLOW);
            image.setOpaque(true);
            name.setForeground(hiddenUnlockedColour);
            description.setForeground(hiddenUnlockedColour);
        }
        image.setBorder(smallEmptyBorder);
        panel.add(image);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(name, BorderLayout.NORTH);
        textPanel.add(description);
        panel.add(textPanel);
        if (!isUnlocked) {
            name.setForeground(achievement.hidden()? hiddenLockedColour : lockedColour);
            description.setForeground(achievement.hidden()? hiddenLockedColour : lockedColour);
            if (achievement.hidden()) {
                description.setText(makeRandomText(achievement.description()));
            }

        } //else if (achievement.hidden()) panel.setBackground(Color.YELLOW);

        panel.setBorder(largeEmptyBorder);
        Border mouseOverBorder = new CompoundBorder(new CompoundBorder(smallEmptyBorder,
                new BevelBorder(BevelBorder.RAISED)), smallEmptyBorder);
        Border mouseDownBorder = new CompoundBorder(new CompoundBorder(smallEmptyBorder,
                new BevelBorder(BevelBorder.LOWERED)), smallEmptyBorder);
        if (isUnlocked && isClickable) {
            panel.addMouseListener(new MouseAdapter() {
                // Show large panel if clicked on, if unlocked.
                @Override
                public void mousePressed(MouseEvent e) {
                    panel.setBorder(mouseDownBorder);
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(panel), largeAchievementPanel(),
                            "Your achievement", JOptionPane.PLAIN_MESSAGE);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBorder(mouseOverBorder);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBorder(largeEmptyBorder);
                }
            });
        }

        return panel;
    }

    public JPanel largeAchievementPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel image = new JLabel(makeScaledImage(200));
        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(image);

        JLabel name = new JLabel(achievement.name());
        Font nameFont = new Font(name.getFont().getFontName(), Font.BOLD, 36);
        name.setFont(nameFont);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setBorder(new EmptyBorder(15, 0, 15, 0));
        panel.add(name);

        JLabel description = new JLabel(achievement.description());
        Font descriptionFont = new Font(description.getFont().getFontName(), Font.PLAIN, 20);
        description.setFont(descriptionFont);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(description);

        if (achievement.hidden()) {
            name.setForeground(hiddenUnlockedColour);
            description.setForeground(hiddenUnlockedColour);
        }

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
