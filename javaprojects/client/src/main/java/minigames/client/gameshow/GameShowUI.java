package minigames.client.gameshow;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.logging.Level;

import javax.swing.BorderFactory;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import io.vertx.core.json.JsonObject;
import minigames.client.Main;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameShowUI {

        private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";

        private final static String fontdir = "./src/main/java/minigames/client/gameshow/Fonts/";

        private static final ImageIcon hScreenBackground = new ImageIcon(dir + "homescreen-background.jpg");

        // make home button

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

        public JButton quit = GameShow.quit();

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
                JButton qiut = GameShow.quit();

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
                lobbyHeader.add(qiut, BorderLayout.NORTH);
                lobbyHeader.add(lobbyInstructions, BorderLayout.PAGE_END);

                return lobbyHeader;

        }

        public static JPanel generateMemoryHeader() {

                JPanel memoryHeader = new JPanel();

                JLabel memoryHeaderImage;
                JTextField memoryInstructions;
                JButton qiut = GameShow.quit();

                memoryHeader.setLayout(new BorderLayout());
                memoryHeader.setBackground(Color.ORANGE);

                memoryHeaderImage = new JLabel(
                                new ImageIcon(memoryIcon.getImage().getScaledInstance(600, 100,
                                                Image.SCALE_DEFAULT)));

                memoryInstructions = new JTextField("add memory instructions");
                memoryInstructions.setEditable(false);
                memoryInstructions.setHorizontalAlignment(JTextField.CENTER);
                memoryInstructions.setBackground(Color.ORANGE);
                memoryInstructions.setFont(pixelFont.deriveFont(15f));
                // TODO: add memory instructions

                memoryHeader.add(memoryHeaderImage);
                memoryHeader.add(qiut, BorderLayout.NORTH);
                memoryHeader.add(memoryInstructions, BorderLayout.PAGE_END);

                return memoryHeader;

        }

        public static JPanel generateScrambleHeader() {

                JPanel scrambleHeader = new JPanel();

                JLabel scrambleHeaderImage;
                JTextField scrambleInstructions;
                JButton qiut = GameShow.quit();

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

                scrambleHeader.add(scrambleHeaderImage);
                scrambleHeader.add(qiut, BorderLayout.NORTH);
                scrambleHeader.add(scrambleInstructions, BorderLayout.PAGE_END);

                return scrambleHeader;

        }

        public static JPanel generateRevealHeader() {

                JPanel revealHeader = new JPanel();

                JLabel revealHeaderImage;
                JTextField revealInstructions;
                JButton qiut = GameShow.quit();

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
                revealHeader.add(revealHeaderImage);
                revealHeader.add(qiut, BorderLayout.NORTH);
                revealHeader.add(revealInstructions, BorderLayout.PAGE_END);

                return revealHeader;

        }

        public static JPanel generateGameContainer(GameShow gameShow) {

                JPanel lobbyPanel = generateLobbyPanel(gameShow);

                gameContainer = new JPanel(new BorderLayout());

                gameContainer.add(lobbyPanel);
                gameContainer.setMinimumSize(new Dimension(600, 400));

                return gameContainer;
        }

        public static JPanel generateLobbyPanel(GameShow gameShow) {

                JPanel lobbyPanel = new JPanel();

                JPanel lobbyHeader = generateLobbyHeader();

                JTextArea players;
                JPanel miniMiniGame;
                JButton startGame;

                lobbyPanel = new JPanel(new BorderLayout());
                lobbyPanel.setBackground(Color.WHITE);

                miniMiniGame = new JPanel(new BorderLayout());
                miniMiniGame.setMinimumSize(new Dimension(400, 250));
                miniMiniGame.setBorder(new LineBorder(Color.WHITE, 10, true));
                miniMiniGame.setBackground(Color.WHITE);

                startGame = new JButton(
                                new ImageIcon(startButton.getImage().getScaledInstance(300, 50, Image.SCALE_DEFAULT)));
                startGame.setContentAreaFilled(false);
                startGame.setFocusPainted(false);
                startGame.setBorderPainted(false);
                // TODO: add action listener

                JButton memoryButton = new JButton("Memory Game");
                memoryButton.addActionListener(null);
                miniMiniGame.add(memoryButton, BorderLayout.LINE_START);

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

                players = new JTextArea("Players in lobby: " + "", 18, 20);// TODO: get names of players
                players.setEditable(false);
                players.setFont(pixelFont.deriveFont(15f));
                JScrollPane scrollableTextArea = new JScrollPane(players);
                scrollableTextArea.setMinimumSize(new Dimension(100, 100));
                scrollableTextArea.setBorder(new LineBorder(Color.WHITE, 10, true));

                lobbyPanel.add(lobbyHeader, BorderLayout.PAGE_START);
                lobbyPanel.add(scrollableTextArea, BorderLayout.LINE_START);
                lobbyPanel.add(miniMiniGame, BorderLayout.LINE_END);

                return lobbyPanel;
        }

        // public static playersNames(MinigameNetworkClient mg){

        // playersNames = mg.getPlayerNames();

        // }

        public static JPanel generateConsistentPanel(GameShow gameShow) {

                JPanel consistentPanel = new JPanel();
                JPanel gameContainer = generateGameContainer(gameShow);

                JPanel scorePanel;
                // JPanel playersPanel;
                // JPanel othersPanel;
                JPanel playersScorePanel;
                JPanel firstScorePanel;
                JPanel secondScorePanel;
                JPanel thirdScorePanel;
                // JTextArea yourScoreHeader;
                JTextField playersName;
                JTextField playersScore;
                // JTextArea otherPlayersHeader;
                JTextField firstPlayersScore;
                JTextField secondPlayersScore;
                JTextField thirdPlayersScore;
                JTextField firstPlayersName;
                JTextField secondPlayersName;
                JTextField thirdPlayersName;

                Border raisedbevel = BorderFactory.createRaisedBevelBorder();
                Border loweredbevel = BorderFactory.createLoweredBevelBorder();

                CompoundBorder compound = BorderFactory.createCompoundBorder(
                                raisedbevel, loweredbevel);

                consistentPanel = new JPanel(new BorderLayout());
                consistentPanel.add(gameContainer, BorderLayout.PAGE_START);

                playersName = new JTextField(gameShow.player);
                playersName.setFont(pixelFont.deriveFont(15f));
                playersName.setHorizontalAlignment(JTextField.CENTER);
                playersName.setColumns(10);
                playersName.setEditable(false);
                playersName.setBackground(Color.ORANGE);
                playersScore = new JTextField("10");// TODO get players score
                playersScore.setFont(pixelFont.deriveFont(40f));
                playersScore.setBorder(compound);
                playersScore.setHorizontalAlignment(JTextField.CENTER);
                playersScorePanel = new JPanel(new BorderLayout());
                playersScorePanel.setBackground(Color.ORANGE);
                playersScorePanel.setBorder(new EmptyBorder(0, 0, 0, 30));
                playersScorePanel.add(playersName, BorderLayout.PAGE_START);
                playersScorePanel.add(playersScore, BorderLayout.PAGE_END);

                // yourScoreHeader = new JTextArea("Your Score");
                // yourScoreHeader.setFont(pixelFont.deriveFont(15f));

                // playersPanel = new JPanel(new BorderLayout());
                // playersPanel.setPreferredSize(new Dimension(80, 80));
                // playersPanel.setBorder(compound);
                // playersPanel.add(yourScoreHeader, BorderLayout.PAGE_START);
                // playersPanel.add(playersScorePanel, BorderLayout.PAGE_END);
                // TODO get other players names and scores
                firstPlayersName = new JTextField("Name");
                firstPlayersName.setFont(pixelFont.deriveFont(15f));
                firstPlayersName.setHorizontalAlignment(JTextField.CENTER);
                firstPlayersName.setColumns(10);
                firstPlayersName.setBackground(Color.ORANGE);
                firstPlayersName.setEditable(false);
                firstPlayersScore = new JTextField("10");
                firstPlayersScore.setFont(pixelFont.deriveFont(40f));
                firstPlayersScore.setHorizontalAlignment(JTextField.CENTER);
                firstPlayersScore.setEditable(false);
                firstPlayersScore.setBorder(compound);

                firstScorePanel = new JPanel(new BorderLayout());
                firstScorePanel.setBackground(Color.ORANGE);
                firstScorePanel.setBorder(new EmptyBorder(0, 30, 0, 0));
                firstScorePanel.add(firstPlayersName, BorderLayout.PAGE_START);
                firstScorePanel.add(firstPlayersScore, BorderLayout.PAGE_END);

                secondPlayersName = new JTextField("Name");
                secondPlayersName.setFont(pixelFont.deriveFont(15f));
                secondPlayersName.setHorizontalAlignment(JTextField.CENTER);
                secondPlayersName.setColumns(10);
                secondPlayersName.setEditable(false);
                secondPlayersName.setBackground(Color.ORANGE);
                secondPlayersScore = new JTextField("10");
                secondPlayersScore.setFont(pixelFont.deriveFont(40f));
                secondPlayersScore.setHorizontalAlignment(JTextField.CENTER);
                secondPlayersScore.setEditable(false);
                secondPlayersScore.setBorder(compound);

                secondScorePanel = new JPanel(new BorderLayout());
                secondScorePanel.setBackground(Color.ORANGE);
                secondScorePanel.add(secondPlayersName, BorderLayout.PAGE_START);
                secondScorePanel.add(secondPlayersScore, BorderLayout.PAGE_END);

                thirdPlayersName = new JTextField("Name");
                thirdPlayersName.setFont(pixelFont.deriveFont(15f));
                thirdPlayersName.setHorizontalAlignment(JTextField.CENTER);
                thirdPlayersName.setEditable(false);
                thirdPlayersName.setColumns(10);
                thirdPlayersName.setBackground(Color.ORANGE);
                thirdPlayersScore = new JTextField("10");
                thirdPlayersScore.setFont(pixelFont.deriveFont(40f));
                thirdPlayersScore.setHorizontalAlignment(JTextField.CENTER);
                thirdPlayersScore.setEditable(false);
                thirdPlayersScore.setBorder(compound);

                thirdScorePanel = new JPanel(new BorderLayout());
                thirdScorePanel.setBackground(Color.ORANGE);
                thirdScorePanel.add(thirdPlayersName, BorderLayout.PAGE_START);
                thirdScorePanel.add(thirdPlayersScore, BorderLayout.PAGE_END);

                // otherPlayersHeader = new JTextArea("Other Players Scores");
                // otherPlayersHeader.setFont(pixelFont.deriveFont(15f));

                // othersPanel = new JPanel(new BorderLayout());
                // othersPanel.add(otherPlayersHeader, BorderLayout.PAGE_START);
                // othersPanel.add(firstScorePanel, BorderLayout.LINE_START);
                // othersPanel.add(secondScorePanel, BorderLayout.CENTER);
                // othersPanel.add(thirdScorePanel, BorderLayout.LINE_END);

                scorePanel = new JPanel(new FlowLayout());
                scorePanel.setBackground(Color.ORANGE);
                scorePanel.setMaximumSize(new Dimension(400, 150));
                scorePanel.add(playersScorePanel);
                scorePanel.add(firstScorePanel);
                scorePanel.add(secondScorePanel);
                scorePanel.add(thirdScorePanel);

                consistentPanel.add(scorePanel, BorderLayout.CENTER);

                return consistentPanel;
        }

}
