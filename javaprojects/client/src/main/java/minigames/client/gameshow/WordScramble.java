package minigames.client.gameshow;

import java.awt.*;
import javax.swing.*;

import io.vertx.core.json.JsonObject;

public class WordScramble {
    private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";
    private static final ImageIcon submitButtonButton = new ImageIcon(dir + "submit-button.png");

    /**
     * Creates a welcome screen, currently prompting user for difficulty
     */
    public static void welcome(GameShow gs) {
        // Clear the current game container
        GameShowUI.gameContainer.removeAll();

        // The base panel for the screen
        JPanel welcomeScreen = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Top half of the screen
        JPanel upperPanel = new JPanel(new GridBagLayout());

        // The name of the game being played
        JLabel gameName = new JLabel("Word Scramble");
        gameName.setFont(new Font("Arial", Font.BOLD, 24));

        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        upperPanel.add(gameName, gbc);

        // Instructions on how the game works
        JLabel instructions[] = new JLabel[4];

        instructions[0] = new JLabel(
                "- You will be presented with letters of a word but they will be "
                        + "in the wrong order!");

        instructions[1] = new JLabel(
                "- Your task is to rearrange the letters to form the correct word.");

        instructions[2] = new JLabel(
                "- There may be more than one possible word from the given letters "
                        + "but only one word is correct.");

        instructions[3] = new JLabel(
                "- The faster you guess the correct word, the higher your score "
                        + "will be. Good luck!");

        gbc.insets = new Insets(5, 0, 5, 0);

        for (JLabel instruction : instructions) {
            instruction.setFont(new Font("Arial", Font.PLAIN, 16));
            gbc.gridy++;
            upperPanel.add(instruction, gbc);
        }

        // Add the instructions to the welcome screen panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        welcomeScreen.add(upperPanel, gbc);

        // A panel to hold to difficulty buttons
        JPanel difficultyPanel = new JPanel(new GridBagLayout());

        // Create and add instructions for selecting a difficulty option
        JLabel difficultyInstructions = new JLabel("Choose your difficulty:");
        difficultyInstructions.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyPanel.add(difficultyInstructions, gbc);

        // Create and add a button to select easy difficulty
        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener((evt) -> gs.sendCommand(new JsonObject()
                .put("command", "startGame")
                .put("game", "wordScramble")
                .put("difficulty", "easy")));
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        difficultyPanel.add(easyButton, gbc);

        // Create and add a button to select medium difficulty
        JButton mediumButton = new JButton("Medium");
        mediumButton.addActionListener((evt) -> gs.sendCommand(new JsonObject()
                .put("command", "startGame")
                .put("game", "wordScramble")
                .put("difficulty", "medium")));
        gbc.gridx = 1;
        gbc.gridy = 1;
        difficultyPanel.add(mediumButton, gbc);

        // Create and add a button to select hard difficulty
        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener((evt) -> gs.sendCommand(new JsonObject()
                .put("command", "startGame")
                .put("game", "wordScramble")
                .put("difficulty", "hard")));
        gbc.gridx = 2;
        gbc.gridy = 1;
        difficultyPanel.add(hardButton, gbc);

        // Add the difficulty buttons to the welcome screen panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 20, 0);
        welcomeScreen.add(difficultyPanel, gbc);

        // Add the welcome screen to the game container and update the window
        GameShowUI.gameContainer.add(welcomeScreen);
        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }

    /**
     * Starts a game
     */
    public static void startGame(GameShow gs, String letters, int gameId) {
        GameShowUI.gameContainer.removeAll();

        // The base panel for the screen
        gs.gamePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Display the scrambled letters
        JLabel scrambledLetters = new JLabel(letters);
        Font pixelFont = GameShowUI.pixelFont;
        scrambledLetters.setFont(pixelFont.deriveFont(30f));

        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gs.gamePanel.add(scrambledLetters, gbc);

        // Add a panel for making guesses
        JPanel guessPanel = new JPanel(new GridBagLayout());

        // A text box in which to type a guess (and optionally submit on Enter)
        JTextField guessBox = new JTextField(7);
        guessBox.setFont(pixelFont.deriveFont(18f));
        guessBox.setHorizontalAlignment(SwingConstants.CENTER);
        guessBox.addActionListener((evt) -> sendGuess(gs, guessBox.getText(), gameId));

        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        guessPanel.add(guessBox, gbc);

        // A submit button - alternative to submitting on Enter from guessBox
        JButton submitButton;
        submitButton = new JButton(
                new ImageIcon(submitButtonButton.getImage().getScaledInstance(150, 40, Image.SCALE_DEFAULT)));
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener((evt) -> sendGuess(gs, guessBox.getText(), gameId));

        gbc.gridx = 0;
        gbc.gridy = 1;
        guessPanel.add(submitButton, gbc);

        // Add the guessing panel to the game panel
        gbc.insets = new Insets(20, 0, 20, 0);
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
     * Processes the result of a guess from the server
     */
    public static void processGuess(GameShow gs, boolean correct) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);

        if (!correct) {
            JLabel incorrectGuess = new JLabel("Incorrect :( Try again!");
            Font pixelFont = GameShowUI.pixelFont;
            incorrectGuess.setFont(pixelFont.deriveFont(15f));
            incorrectGuess.setForeground(Color.RED);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gs.gamePanel.add(incorrectGuess, gbc);
        } else {
            gs.gamePanel.removeAll();
            JLabel correctGuess = new JLabel("Congratulations! You Win :)");
            Font pixelFont = GameShowUI.pixelFont;
            correctGuess.setFont(pixelFont.deriveFont(15f));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gs.gamePanel.add(correctGuess, gbc);
        }

        gs.gamePanel.validate();
        gs.gamePanel.repaint();
    }

    /**
     * Sends a guess to the server
     */
    private static void sendGuess(GameShow gs, String guess, int gameId) {
        gs.sendCommand(new JsonObject()
                .put("command", "guess")
                .put("game", "wordScramble")
                .put("guess", guess)
                .put("gameId", gameId));
    }
}
