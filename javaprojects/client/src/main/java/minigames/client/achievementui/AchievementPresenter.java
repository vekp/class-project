package minigames.client.achievementui;

import minigames.achievements.Achievement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOError;

public class AchievementPresenter {

    private final String rootImageFolderLocation = "src/main/resources/images/achievements/";
    private final String defaultFilepath = rootImageFolderLocation + "default/";
    private final String resourceFilepath;
    private final Achievement achievement;
    private final ImageIcon smallImage;
    private final ImageIcon largeImage;
    //TODO: Add a sound effect?

    public AchievementPresenter(Achievement achievement) {
        this.achievement = achievement;
        resourceFilepath = rootImageFolderLocation + achievement.type().toLowerCase().replace(" ", "") + "/";
        System.out.println(resourceFilepath);
        smallImage = makeImage("small.png");
        largeImage = makeImage("large.png");
    }

    private ImageIcon makeImage(String filename) {
        String path = resourceFilepath + filename;
        System.out.println(path);
        System.out.println((new File(path).exists())? "Found" : "Not found");
        ImageIcon image;
        try {
            image = new ImageIcon(resourceFilepath + filename);
        } catch (IOError e) {
            System.out.println("not found");
        }
        return new ImageIcon(resourceFilepath + filename);
    }

    public JPanel smallAchievementPanel() {
        JPanel panel = new JPanel();
        JLabel name = new JLabel(achievement.name());
        name.setBorder(new LineBorder(Color.BLACK));
        JTextArea description = new JTextArea(achievement.description());
        description.setBorder(new LineBorder(Color.BLACK));

        panel.add(new JLabel(smallImage));
        panel.add(name);
        panel.add(description);
        return panel;
    }

}
