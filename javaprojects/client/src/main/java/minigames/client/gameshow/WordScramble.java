package minigames.client.gameshow;

import java.awt.*;
import javax.swing.*;

import io.vertx.core.json.JsonObject;

/**
 * Contains methods for displaying a Word Scramble puzzle.
 */
public class WordScramble {
        private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";
        private static final ImageIcon submitButtonButton = new ImageIcon(dir + "submit-button.png");
        private static final ImageIcon paperBackground = new ImageIcon(dir + "paper.png");
        private static final ImageIcon backBackground = new ImageIcon(dir + "back.png");

        public static JLabel backGround;

        /**
         * Draw a Word Scramble puzzle to the window.
         * @param gs an instance of GameShow.
         * @param letters the scrambled letters to be displayed.
         */
        public static void startGame(GameShow gs, String letters) {
                GameShowUI.gameContainer.removeAll();

                ImageIcon imageIcon = new ImageIcon(backBackground.getImage().getScaledInstance(600, 290,
                                Image.SCALE_DEFAULT));
                backGround = new JLabel(imageIcon);

                // Set up the scrambled letters display
                JButton scrambledLetters = new JButton(letters);
                Font pixelFont = GameShowUI.pixelFont;
                scrambledLetters.setFont(pixelFont.deriveFont(60f));
                scrambledLetters.setSize(600, 220);
                scrambledLetters.setVerticalAlignment(SwingConstants.BOTTOM);
                scrambledLetters.setContentAreaFilled(false);
                scrambledLetters.setFocusPainted(false);
                scrambledLetters.setBorderPainted(false);
                backGround.add(scrambledLetters);

                // The base panel for the screen
                gs.gamePanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();

                // Display the scrambled letters
                gbc.gridx = 0;
                gbc.gridy = 0;
                gs.gamePanel.add(backGround, gbc);

                // Add a panel for making guesses
                JPanel guessPanel = new JPanel(new GridBagLayout());
                JTextField guessBox = new JTextField();
                guessBox.setFont(pixelFont.deriveFont(18f));
                guessBox.setColumns(20);
                guessBox.addActionListener((evt) -> sendGuess(gs, guessBox.getText()));

                gbc.anchor = GridBagConstraints.LINE_START;
                gbc.gridx = 0;
                gbc.gridy = 0;
                guessPanel.add(guessBox, gbc);

                // A submit button - alternative to submitting on Enter from guessBox
                JButton submitButton;
                submitButton = new JButton(new ImageIcon(submitButtonButton
                  .getImage()
                  .getScaledInstance(150, 40,Image.SCALE_DEFAULT)));
                submitButton.setContentAreaFilled(false);
                submitButton.setFocusPainted(false);
                submitButton.setBorderPainted(false);
                submitButton.addActionListener((evt) -> sendGuess(gs, guessBox.getText()));

                gbc.insets = new Insets(0, 80, 0, 30);
                gbc.anchor = GridBagConstraints.LINE_END;
                gbc.gridx = 1;
                gbc.gridy = 0;
                guessPanel.add(submitButton, gbc);

                // Add the guessing panel to the game panel
                gbc.gridx = 0;
                gbc.gridy = 1;
                gs.gamePanel.add(guessPanel, gbc);

                JPanel scrambleHeader = GameShowUI.generateScrambleHeader();

                GameShowUI.gameContainer.add(scrambleHeader, BorderLayout.NORTH);
                GameShowUI.gameContainer.add(gs.gamePanel, BorderLayout.CENTER);
                GameShowUI.gameContainer.validate();
                GameShowUI.gameContainer.repaint();
        }

        /**
         * Processes the result of a guess from the server.
         * @param gs an instance of GameShow.
         * @param correct Boolean value representing if a submitted guess was correct.
         */
        public static void processGuess(GameShow gs, boolean correct) {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(20, 0, 20, 0);

                if (!correct) {
                        JButton incorrectGuess = new JButton("Incorrect :( Try again!");
                        Font pixelFont = GameShowUI.pixelFont;
                        incorrectGuess.setFont(pixelFont.deriveFont(15f));
                        incorrectGuess.setForeground(Color.RED);
                        incorrectGuess.setSize(600, 290);
                        incorrectGuess.setVerticalAlignment(SwingConstants.BOTTOM);
                        incorrectGuess.setContentAreaFilled(false);
                        incorrectGuess.setFocusPainted(false);
                        incorrectGuess.setBorderPainted(false);
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        backGround.add(incorrectGuess, gbc);
                } else {
                        gs.gamePanel.removeAll();
                        JLabel correctGuess = new JLabel("Congratulations! You Win :)");
                        Font pixelFont = GameShowUI.pixelFont;
                        correctGuess.setFont(pixelFont.deriveFont(15f));
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gs.gamePanel.add(correctGuess, gbc);

                        JButton nextRoundButton = new JButton("Next round ->");
                        nextRoundButton.addActionListener((evt) -> {
                                gs.sendCommand(new JsonObject()
                                  .put("command", "nextRound")
                                  .put("round", gs.round + 1));
                        });
                        gbc.gridx = 0;
                        gbc.gridy = 1;
                        gs.gamePanel.add(nextRoundButton, gbc);
                }

                backGround.validate();
                backGround.repaint();
                gs.gamePanel.validate();
                gs.gamePanel.repaint();
        }

        /**
         * Sends a guess to the server.
         * @param gs an instance of GameShow.
         * @param guess the guess made.
         */
        private static void sendGuess(GameShow gs, String guess) {
                gs.sendCommand(new JsonObject()
                                .put("command", "guess")
                                .put("game", "WordScramble")
                                .put("guess", guess)
                                .put("round", gs.round)
                                .put("score", gs.gameTimer.calculateScore()));
        }
}
