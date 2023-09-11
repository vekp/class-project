package minigames.client.gameshow;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;

import io.vertx.core.json.JsonObject;
import minigames.client.Main;
import minigames.commands.CommandPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameShowUI {

        private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";

        private final static String fontdir = "./src/main/java/minigames/client/gameshow/Fonts/";

        private static final ImageIcon hScreenBackground = new ImageIcon(dir + "homescreen-background.jpg");

        private static final ImageIcon lobbyButton = new ImageIcon(dir + "lobby-button.png");// make home button

        private static final ImageIcon lobbyIcon = new ImageIcon(dir + "lobby.png");

        private static final ImageIcon memoryIcon = new ImageIcon(dir + "memory.png");

        private static final ImageIcon scrambleIcon = new ImageIcon(dir + "scramble.png");

        private static final ImageIcon revealIcon = new ImageIcon(dir + "reveal.png");

        private static final ImageIcon startButton = new ImageIcon(dir + "start-button.png");

        private static final ImageIcon submitButton = new ImageIcon(dir + "submit-button.png");

        public static JPanel lobbyHeader;
        public static JPanel memoryHeader;
        public static JPanel scrambleHeader;
        public static JPanel revealHeader;
        public static JPanel gameContainer;
        public static JPanel lobbyPanel;
        public static JPanel consistantPanel = null;
        public static Font pixelFont;
        public JPanel homeScreenPanel;

        static JButton wordScramble;
        static JButton imageGuesserStart;

        private int alpha = 255;
        private int increment = -5;
        private FadePanel background;

        public static Font pixelFont() {
                try {
                        pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontdir + "Minecraft.ttf"));
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontdir + "Minecraft.ttf")));

                } catch (IOException | FontFormatException e) {
                        System.out.println(e);
                }
                return pixelFont;
        }

        /**
         * method to add pixelFont to Graphics Environment
         */

        /**
         * method to generate the home screen
         * 
         * @return a JPanel representing the home screen
         */

        public static JPanel generateHomeScreen() {

                JLabel background;
                FadePanel homeScreenPanel;

                homeScreenPanel = new FadePanel();

                background = new JLabel(
                                new ImageIcon(hScreenBackground.getImage().getScaledInstance(800, 600,
                                                Image.SCALE_DEFAULT)));

                homeScreenPanel.add(background);

                homeScreenPanel.setAlpha(0.5);

                SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                                var main = new GameShowUI();
                                main.background = homeScreenPanel;
                                main.makeUI();
                        }
                });

                return homeScreenPanel;
        }

        public void makeUI() {

                new javax.swing.Timer(40, new ActionListener() {

                        public void actionPerformed(ActionEvent e) {

                                if (alpha <= 0) {
                                        return;
                                }
                                alpha += increment;

                                background.setAlpha(alpha / 255f);
                                // System.out.println(background.getAlpha());

                        }
                }).start();
        }

        public static JPanel generateIntroPanel(GameShow gameShow) {
                pixelFont();
                JPanel introPanel = new JPanel();
                introPanel.setLayout(new OverlayLayout(introPanel));

                JPanel topPanel = generateHomeScreen();
                JPanel bottomPanel = generateConsistentPanel(gameShow);

                introPanel.add(topPanel);
                introPanel.add(bottomPanel);

                return introPanel;
        }

        public static JPanel generateLobbyHeader() {

                JPanel lobbyHeader = new JPanel();

                JLabel lobbyHeaderImage;
                JTextField lobbyInstructions;

                lobbyHeader.setLayout(new BorderLayout());
                lobbyHeader.setBackground(Color.ORANGE);

                lobbyHeaderImage = new JLabel(
                                new ImageIcon(lobbyIcon.getImage().getScaledInstance(600, 100,
                                                Image.SCALE_DEFAULT)));

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
                                new ImageIcon(memoryIcon.getImage().getScaledInstance(800, 100,
                                                Image.SCALE_DEFAULT)));

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
                                new ImageIcon(scrambleIcon.getImage().getScaledInstance(600, 100,
                                                Image.SCALE_DEFAULT)));

                scrambleInstructions = new JTextField("add scramble instructions");
                scrambleInstructions.setEditable(false);
                scrambleInstructions.setHorizontalAlignment(JTextField.CENTER);
                scrambleInstructions.setBackground(Color.ORANGE);
                scrambleInstructions.setFont(pixelFont.deriveFont(15f));
                // TODO: add scramble instructions

                scrambleHeader.add(scrambleHeaderImage, BorderLayout.NORTH);
                scrambleHeader.add(scrambleInstructions, BorderLayout.SOUTH);

                return scrambleHeader;

        }

        public static JPanel generateRevealHeader() {

                JPanel revealHeader = new JPanel();

                JLabel revealHeaderImage;
                JTextField revealInstructions;

                revealHeader.setLayout(new BorderLayout());
                revealHeader.setBackground(Color.ORANGE);
                revealHeaderImage = new JLabel(
                                new ImageIcon(revealIcon.getImage().getScaledInstance(600, 100,
                                                Image.SCALE_DEFAULT)));

                revealInstructions = new JTextField("add reveal instructions");
                revealInstructions.setEditable(false);
                revealInstructions.setHorizontalAlignment(JTextField.CENTER);
                revealInstructions.setBackground(Color.ORANGE);
                revealInstructions.setFont(pixelFont.deriveFont(15f));
                // TODO: add reveal instructions

                revealHeader.add(revealHeaderImage, BorderLayout.NORTH);
                revealHeader.add(revealInstructions, BorderLayout.SOUTH);

                return revealHeader;

        }

        public static JPanel generateGameContainer(GameShow gameShow) {

                JPanel lobbyPanel = generateLobbyPanel(gameShow);

                gameContainer = new JPanel(new BorderLayout());

                gameContainer.add(lobbyPanel);
                gameContainer.setMinimumSize(new Dimension(800, 600));

                return gameContainer;
        }

        public static JPanel generateLobbyPanel(GameShow gameShow) {

                JPanel lobbyPanel = new JPanel();

                JPanel lobbyHeader = generateLobbyHeader();

                JTextArea players;
                JPanel miniMiniGame;
                JButton startGame;

                lobbyPanel = new JPanel(new BorderLayout());

                miniMiniGame = new JPanel(new BorderLayout());
                miniMiniGame.setMinimumSize(new Dimension(400, 250));

                startGame = new JButton(
                                new ImageIcon(startButton.getImage().getScaledInstance(300, 50, Image.SCALE_DEFAULT)));
                startGame.setContentAreaFilled(false);
                startGame.setFocusPainted(false);
                startGame.setBorderPainted(false);

                wordScramble = new JButton("Word Scramble");
                // wordScramble.setAlignmentX(Component.CENTER_ALIGNMENT);
                wordScramble.addActionListener((evt) -> WordScramble.welcome(GameShow.Main));
                miniMiniGame.add(wordScramble, BorderLayout.PAGE_START);

                imageGuesserStart = new JButton("Image Guesser");
                // imageGuesserStart.setAlignmentX(Component.CENTER_ALIGNMENT);
                imageGuesserStart.addActionListener(
                                (evt) -> gameShow.sendCommand(new JsonObject().put("command", "imageGuesser")));
                miniMiniGame.add(imageGuesserStart, BorderLayout.CENTER);

                miniMiniGame.add(startGame, BorderLayout.PAGE_END);

                players = new JTextArea("Players in lobby: " + "", 20, 30);// TODO: get names of players
                players.setFont(pixelFont.deriveFont(15f));
                JScrollPane scrollableTextArea = new JScrollPane(players);
                scrollableTextArea.setMinimumSize(new Dimension(400, 250));

                lobbyPanel.add(lobbyHeader, BorderLayout.PAGE_START);
                lobbyPanel.add(scrollableTextArea, BorderLayout.LINE_START);
                lobbyPanel.add(miniMiniGame, BorderLayout.LINE_END);

                return lobbyPanel;
        }

        public static JPanel generateConsistentPanel(GameShow gameShow) {

                JPanel consistentPanel = new JPanel();
                JPanel gameContainer = generateGameContainer(gameShow);

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
