package minigames.client.gameshow;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameShowUI {

    private final static String hSBackgroundPath = "./GameShowImages/homescreen-background.jpg";
    private static final ImageIcon hScreenBackground = new ImageIcon(hSBackgroundPath);

    private final static String singleButtonPath = "./GameShowImages/single-button.png";
    private static final ImageIcon singleButton = new ImageIcon(singleButtonPath);

    private final static String multiButtonPath = "./GameShowImages/multi-button.png";
    private static final ImageIcon multiButton = new ImageIcon(multiButtonPath);

    private final static String invisableButtonPath = "./GameShowImages/invisable-button.png";
    private static final ImageIcon invisableButton = new ImageIcon(invisableButtonPath);

    public static JPanel generateHomeScreen() {

        JLabel background;
        JPanel homeScreenPanel;

        JButton singlePlayer;
        JButton multiPlayer;
        JButton singlePlayer1;
        JButton multiPlayer1;

        homeScreenPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // gbc.ipadx = 20;
        singlePlayer = new JButton(
                new ImageIcon(singleButton.getImage().getScaledInstance(300, 130, Image.SCALE_DEFAULT)));
        singlePlayer.setContentAreaFilled(false);
        singlePlayer.setFocusPainted(false);
        singlePlayer.setBorderPainted(false);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 3;
        // gbc.gridheight = 4;
        // gbc.gridwidth = 4;
        homeScreenPanel.add(singlePlayer, gbc);

        singlePlayer1 = new JButton(
                new ImageIcon(invisableButton.getImage().getScaledInstance(300, 130, Image.SCALE_DEFAULT)));
        // singlePlayer1.setVisible(false);
        singlePlayer1.setContentAreaFilled(false);
        singlePlayer1.setFocusPainted(false);
        singlePlayer1.setBorderPainted(false);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        // gbc.gridheight = 4;
        // gbc.gridwidth = 4;
        homeScreenPanel.add(singlePlayer1, gbc);

        multiPlayer = new JButton(
                new ImageIcon(multiButton.getImage().getScaledInstance(300, 130, Image.SCALE_DEFAULT)));
        multiPlayer.setContentAreaFilled(false);
        multiPlayer.setFocusPainted(false);
        multiPlayer.setBorderPainted(false);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.gridx = 3;
        gbc.gridy = 3;
        // gbc.gridheight = 4;
        // gbc.gridwidth = 4;
        homeScreenPanel.add(multiPlayer, gbc);

        multiPlayer1 = new JButton(
                new ImageIcon(invisableButton.getImage().getScaledInstance(300, 130, Image.SCALE_DEFAULT)));
        multiPlayer1.setContentAreaFilled(false);
        multiPlayer1.setFocusPainted(false);
        multiPlayer1.setBorderPainted(false);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.gridx = 4;
        gbc.gridy = 3;
        // gbc.gridheight = 4;
        // gbc.gridwidth = 4;
        homeScreenPanel.add(multiPlayer1, gbc);

        background = new JLabel(
                new ImageIcon(hScreenBackground.getImage()));// .getScaledInstance(1300,
                                                             // 667,Image.SCALE_DEFAULT)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 10;
        gbc.ipady = 20;
        gbc.gridheight = 4;
        gbc.gridwidth = 5;

        homeScreenPanel.add(background, gbc);

        singlePlayer.addActionListener(e -> {

        });

        multiPlayer.addActionListener(e -> {

        });

        return homeScreenPanel;

    }

}
