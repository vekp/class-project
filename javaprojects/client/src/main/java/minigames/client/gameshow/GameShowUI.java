package minigames.client.gameshow;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GameShowUI {

        private final static String hSBackgroundPath = "./GameShowImages/homescreen-background.jpg";
        private static final ImageIcon hScreenBackground = new ImageIcon(hSBackgroundPath);

        private final static String lobbyButtonPath = "./GameShowImages/lobby-button.png";
        private static final ImageIcon lobbyButton = new ImageIcon(lobbyButtonPath);

        private final static String lobbyIconPath = "./GameShowImages/lobby.png";
        private static final ImageIcon lobbyIcon = new ImageIcon(lobbyIconPath);

        private final static String memoryIconPath = "./GameShowImages/memory.png";
        private static final ImageIcon memoryIcon = new ImageIcon(memoryIconPath);

        private final static String scrambleIconPath = "./GameShowImages/scramble.png";
        private static final ImageIcon scrambleIcon = new ImageIcon(scrambleIconPath);

        private final static String revealIconPath = "./GameShowImages/reveal.png";
        private static final ImageIcon revealIcon = new ImageIcon(revealIconPath);

        private final static String startButtonPath = "./GameShowImages/start-button.png";
        private static final ImageIcon startButton = new ImageIcon(startButtonPath);

        public static JPanel lobbyHeader;
        public static JPanel memoryHeader;
        public static JPanel scrambleHeader;
        public static JPanel revealHeader;
        public static JPanel gameContainer;
        public static JPanel lobbyPanel;
        public static JPanel consistantPanel = null;
        public static Font pixelFont;
        public JPanel homeScreenPanel;

        public static Font pixelFont() {
                return pixelFont;
        }

        public void gameShowUI() {
                try {
                        pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                                        getClass().getResourceAsStream("/Fonts/Minecraft.ttf"));
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                                        getClass().getResourceAsStream("/Fonts/Minecraft.ttf")));
                } catch (IOException | FontFormatException e) {
                        System.out.println(e);
                }

        }

        /**
         * method to generate the home screen
         * 
         * @return a JPanel representing the home screen
         */

        public static JPanel generateHomeScreen() {

                JLabel background;
                JPanel homeScreenPanel;

                JButton lobby;
                System.out.println("check1");

                pixelFont();
                System.out.println("check2");

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
                System.out.println("check3");

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

                        // mainWindow.setContentPane(generateConsistentPanel());
                        // mainWindow.pack();
                        // mainWindow.setLocationRelativeTo(null);
                        // mainWindow.setVisible(true);

                });
                System.out.println("check4");
                return homeScreenPanel;

        }

        public static JPanel generateLobbyHeader() {

                JPanel lobbyHeader = new JPanel();

                JLabel lobbyHeaderImage;
                JTextField lobbyInstructions;

                lobbyHeader.setLayout(new BorderLayout());
                lobbyHeader.setBackground(Color.ORANGE);

                lobbyHeaderImage = new JLabel(
                                new ImageIcon(lobbyIcon.getImage()));

                lobbyInstructions = new JTextField("Start a solo game now or wait for other players to join");
                lobbyInstructions.setEditable(false);
                lobbyInstructions.setHorizontalAlignment(JTextField.CENTER);
                lobbyInstructions.setBackground(Color.ORANGE);
                lobbyInstructions.setFont(pixelFont.deriveFont(15f));

                lobbyHeader.add(lobbyHeaderImage);
                lobbyHeader.add(lobbyInstructions, BorderLayout.PAGE_END);

                return lobbyHeader;

        }

        public static JPanel generateMemoryHeader() {

                JPanel memoryHeader = new JPanel();

                JLabel memoryHeaderImage;
                JTextField memoryInstructions;

                memoryHeader.setLayout(new BorderLayout());
                memoryHeader.setBackground(Color.ORANGE);

                memoryHeaderImage = new JLabel(
                                new ImageIcon(memoryIcon.getImage()));

                memoryInstructions = new JTextField("add memory instructions");
                memoryInstructions.setEditable(false);
                memoryInstructions.setHorizontalAlignment(JTextField.CENTER);
                memoryInstructions.setBackground(Color.ORANGE);
                memoryInstructions.setFont(pixelFont.deriveFont(15f));
                // TODO: add memory instructions

                memoryHeader.add(memoryHeaderImage);
                memoryHeader.add(memoryInstructions);

                return memoryHeader;

        }

        public static JPanel generateScrambleHeader() {

                JPanel scrambleHeader = new JPanel();

                JLabel scrambleHeaderImage;
                JTextField scrambleInstructions;

                scrambleHeader.setLayout(new BorderLayout());
                scrambleHeader.setBackground(Color.ORANGE);

                scrambleHeaderImage = new JLabel(
                                new ImageIcon(scrambleIcon.getImage()));

                scrambleInstructions = new JTextField("add scramble instructions");
                scrambleInstructions.setEditable(false);
                scrambleInstructions.setHorizontalAlignment(JTextField.CENTER);
                scrambleInstructions.setBackground(Color.ORANGE);
                scrambleInstructions.setFont(pixelFont.deriveFont(15f));
                // TODO: add scramble instructions

                scrambleHeader.add(scrambleHeaderImage);
                scrambleHeader.add(scrambleInstructions);

                return scrambleHeader;

        }

        public static JPanel generateRevealHeader() {

                JPanel revealHeader = new JPanel();

                JLabel revealHeaderImage;
                JTextField revealInstructions;

                revealHeader.setLayout(new BorderLayout());
                revealHeader.setBackground(Color.ORANGE);
                revealHeaderImage = new JLabel(
                                new ImageIcon(revealIcon.getImage()));

                revealInstructions = new JTextField("add reveal instructions");
                revealInstructions.setEditable(false);
                revealInstructions.setHorizontalAlignment(JTextField.CENTER);
                revealInstructions.setBackground(Color.ORANGE);
                revealInstructions.setFont(pixelFont.deriveFont(15f));
                // TODO: add reveal instructions

                revealHeader.add(revealHeaderImage);
                revealHeader.add(revealInstructions);

                return revealHeader;

        }

        public static JPanel generateGameContainer() {

                JPanel gameContainer;
                JPanel lobbyPanel = generateLobbyPanel();

                gameContainer = new JPanel(new BorderLayout());

                gameContainer.add(lobbyPanel);

                return gameContainer;
        }

        public static JPanel generateLobbyPanel() {

                JPanel lobbyPanel = new JPanel();

                JPanel lobbyHeader = generateLobbyHeader();

                JTextArea players;
                JPanel miniMiniGame;
                JButton startGame;

                lobbyPanel = new JPanel(new BorderLayout());

                miniMiniGame = new JPanel();
                miniMiniGame.setMinimumSize(new Dimension(600, 800));

                startGame = new JButton(
                                new ImageIcon(startButton.getImage().getScaledInstance(600, 100, Image.SCALE_DEFAULT)));// TODO:
                                                                                                                        // change
                                                                                                                        // button
                                                                                                                        // image
                startGame.setContentAreaFilled(false);
                startGame.setFocusPainted(false);
                startGame.setBorderPainted(false);

                players = new JTextArea("Players in lobby: " + "", 20, 30);// TODO: get names of players
                players.setFont(pixelFont.deriveFont(15f));
                JScrollPane scrollableTextArea = new JScrollPane(players);
                scrollableTextArea.setMinimumSize(new Dimension(600, 800));

                lobbyPanel.add(lobbyHeader, BorderLayout.PAGE_START);
                lobbyPanel.add(scrollableTextArea, BorderLayout.LINE_START);
                lobbyPanel.add(miniMiniGame, BorderLayout.LINE_END);
                lobbyPanel.add(startGame, BorderLayout.PAGE_END);

                return lobbyPanel;
        }

        public static JPanel generateConsistentPanel() {

                JPanel consistentPanel = new JPanel();
                JPanel gameContainer = generateGameContainer();

                JPanel scoreHeadingsHolder;
                JPanel scoreHolder;
                JTextArea yourScore;
                JTextArea topScores;
                JTextArea yourScoreHolder;
                JTextArea firstPlaceHolder;
                JTextArea secondPlaceHolder;
                JTextArea thirdPlaceHolder;

                consistentPanel = new JPanel();

                consistentPanel.add(gameContainer);

                return consistentPanel;
        }

}
