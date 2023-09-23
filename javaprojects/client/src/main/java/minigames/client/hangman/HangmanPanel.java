package minigames.client.hangman;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.hangman.Tools;

public class HangmanPanel extends JPanel {

    /**
     * Returns a random line from a text file that will be used as the string to be guessed in the
     * game.
     */
    static List<String> lines = null;
    static {
        try {
            Path path = Paths.get(Tools.getFileURI("hangManWords/words.txt"));
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    final boolean[] letterUsed = new boolean[26];
    private final Random random = new Random();
    ImagePanel imagePanel = new ImagePanel();
    KeywordPanel keywordPanel = new KeywordPanel();
    RiddlePanel riddlePanel = new RiddlePanel();
    int errors = 0;
    String solution = "Default text";
    boolean gameLost = false;
    boolean gameWon = false;
    Color borderColour = new Color(146, 106, 61);
    int score = 0;
    public static final Logger logger = LogManager.getLogger(HangmanPanel.class);
    public HangmanPanel() {
        addKeyListener(
            new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e){
                    keyboard(e);
                }
        });
        // Initialise or reset game
        reset(getRandomWord());
        setPreferredSize(new Dimension(800, 800));
        imagePanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, borderColour));
        setupLayout();
        setFocusable(true);
    }
    /**
     * The setupLayout function sets up the layout of the puzzle. It sets up a GridBagLayout for
     * this JPanel\
     */
    void setupLayout() {
        setLayout((new GridBagLayout()));
        setPreferredSize(new Dimension(500, 600));

        GridBagConstraints hangmanConstraints = new GridBagConstraints();
        hangmanConstraints.fill = GridBagConstraints.BOTH;
        hangmanConstraints.weightx = 1.5;
        hangmanConstraints.weighty = 1.5;
        hangmanConstraints.gridx = 0;
        hangmanConstraints.gridy = 0;
        hangmanConstraints.gridwidth = 1;
        hangmanConstraints.gridheight = 1;

        GridBagConstraints keyboardConstraints = new GridBagConstraints();
        keyboardConstraints.fill = GridBagConstraints.BOTH;
        keyboardConstraints.weightx = 0.4;
        keyboardConstraints.weighty = 0.5;
        keyboardConstraints.gridx = 1;
        keyboardConstraints.gridy = 0;
        keyboardConstraints.gridwidth = 1;
        keyboardConstraints.gridheight = 1;

        GridBagConstraints riddleConstraints = new GridBagConstraints();
        riddleConstraints.fill = GridBagConstraints.BOTH;
        riddleConstraints.weightx = 0.5;
        riddleConstraints.weighty = 0.2;
        riddleConstraints.gridx = 0;
        riddleConstraints.gridy = 1;
        riddleConstraints.gridwidth = 2;
        riddleConstraints.gridheight = 1;

        add(imagePanel, hangmanConstraints);
        add(keywordPanel, keyboardConstraints);
        add(riddlePanel, riddleConstraints);
    }
    /**
     * Create a new game with initialised values. Clears old guesses, error count, and win/lose
     * status.
     *
     * @param puzzle The string that a player must guess in the new game.
     * Author: Sushil Kandel
     */
    public void reset(String puzzle) {
        errors = 0;
        this.solution = puzzle;
        gameLost = false;
        gameWon = false;
        Arrays.fill(letterUsed, false);

        imagePanel.setErrors(0);
        imagePanel.repaint();
        riddlePanel.setLabelText(censorSolution());
        riddlePanel.setLabelColour(Color.BLACK);
        riddlePanel.repaint();
        keywordPanel.resetLetters();
        keywordPanel.repaint();
    }

    /**
     * The censorSolution function takes a String as an argument and returns a String. .
     *
     * @return A string containing the censored version of the solution
     */
    String censorSolution() {
        char[] chars = solution.toCharArray();
        boolean blanksRemaining = false;
        for (int i = 0; i < chars.length; i++) {
            char letter = (Character.toUpperCase(chars[i]));
            if ((letter >= 'A') && (letter <= 'Z') && !letterUsed[letter - 'A']) {
                blanksRemaining = true;
                chars[i] = '_';
            }
        }
        if (!blanksRemaining) {
            gameWon = true;
            riddlePanel.setLabelColour(Color.GREEN);
        }
        return new String(chars).replace("", "\u2009");
    }

    /**
     * Take a letter and update the game state. Mark that letter as being guessed, and add an error
     * to the tally if it's not there. 
     *
     * @param letter letter that was guessed. It must already be converted to upper case
     */
    void handleLetterPress(char letter) {
        if (gameLost || gameWon || letterUsed[letter - 'A']) {
            return;
        }
        letterUsed[letter - 'A'] = true;
        keywordPanel.useLetter(letter);
        if (solution.toUpperCase().indexOf(letter) == -1) errors++;
        imagePanel.setErrors(errors);
        if (errors >= 8) {
            gameLost = true;
            riddlePanel.setLabelText(solution);
            riddlePanel.setLabelColour(Color.RED);
        } else {
            riddlePanel.setLabelText(censorSolution());
        }
        repaint();
    }
    public void setScore(int score) {
        this.score = score;
    }
    private void playAgain() {
        JFrame playAgainFrame = new JFrame();
        if (gameLost) {
            score = 0;
            HangmanClient.updatePoint(score);
            JOptionPane.showMessageDialog(
                    playAgainFrame,
                    "Opps! The word was " + solution + ".\n Would you like to play again?");
        } else {
            score = score + 50;
            HangmanClient.updatePoint(score);
            JOptionPane.showMessageDialog(
                    playAgainFrame,
                    "Congratulations! You made it" + "\nWould you like to play again?");
        }
    }
    /**
     * Register a pressed key, delegate the response if it's a valid key Pressing a letter is
     * treated as a guess in this game
     *
     * @param e the keyboard event
     */
    void keyboard(KeyEvent e) {
        char key = e.getKeyChar();
        if (key >= 'a' && key <= 'z' || key >= 'A' && key <= 'Z') {
            key = Character.toUpperCase(key);
            handleLetterPress(key);
        }
        if ((gameLost || gameWon)) {
            playAgain();
            reset(getRandomWord());
        }
        repaint();
    }
    /**
     * The getRandomWord function returns a random word from the list of words.
     *
     * @return A random word from the list of words
     */
    public String getRandomWord() {
        return Tools.toTitleCase(lines.get(random.nextInt(lines.size())));
    }
    public void setFocusOnPanel(){
        this.setFocusable(true);
        this.requestFocusInWindow();
    }
}