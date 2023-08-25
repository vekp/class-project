package minigames.client.gameshow;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameShowUI {

        private final static String hSBackgroundPath = "./GameShowImages/homescreen-background.jpg";
        private static final ImageIcon hScreenBackground = new ImageIcon(hSBackgroundPath);

        private final static String lobbyButtonPath = "./GameShowImages/lobby-button.png";
        private static final ImageIcon lobbyButton = new ImageIcon(lobbyButtonPath);

        // private final static String multiButtonPath =
        // "./GameShowImages/multi-button.png";
        // private static final ImageIcon multiButton = new ImageIcon(multiButtonPath);

        // private final static String invisableButtonPath =
        // "./GameShowImages/invisable-button.png";
        // private static final ImageIcon invisableButton = new
        // ImageIcon(invisableButtonPath);

        /**
         * method to generate the home screen
         * 
         * @return a JPanel representing the home screen
         */

        public static JPanel generateHomeScreen() {

                JLabel background;
                JPanel homeScreenPanel;

                JButton lobby;

                homeScreenPanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();

                lobby = new JButton(
                                new ImageIcon(lobbyButton.getImage().getScaledInstance(300, 130, Image.SCALE_DEFAULT)));
                lobby.setContentAreaFilled(false);
                lobby.setFocusPainted(false);
                lobby.setBorderPainted(false);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.SOUTH;
                gbc.gridx = 0;
                gbc.gridy = 0;
                homeScreenPanel.add(lobby, gbc);

                background = new JLabel(
                                new ImageIcon(hScreenBackground.getImage()));
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.ipadx = 10;
                gbc.ipady = 20;
                gbc.gridheight = 1;
                gbc.gridwidth = 1;
                homeScreenPanel.add(background, gbc);

                lobby.addActionListener(e -> {

                });

                return homeScreenPanel;
        }

        public static JPanel generateConsistentPanel() {

                JPanel consistentPanel;
                consistentPanel = new JPanel(null, false);
                return consistentPanel;
        }

}
