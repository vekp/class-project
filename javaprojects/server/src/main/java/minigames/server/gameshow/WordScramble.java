package minigames.server.gameshow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the logic for the Word Scramble puzzle.
 */
public class WordScramble implements GameShowMiniGame {

    // The correct word for a given instance of WordScramble.
    private String chosenWord;

    /**
     * Creates an instance of the WordScramble game.
     * @param wordsFileName the filename containing the list of words from which to choose.
     */
    public WordScramble(String wordsFileName) {
        chooseWord(getWords(wordsFileName));
    }

    // Logger for the WordScramble class.
    private static final Logger logger = LogManager.getLogger(WordScramble.class);
    Random rand = new Random();

    /**
     * Reads the contents from a given file.
     * @param fileName the filename from which to read.
     * @return an ArrayList<String> of words.
     */
    public List<String> getWords(String fileName) {
        List<String> wordsList = new ArrayList<>();

        try {
            Path path = Paths.get("src/main/java/minigames/server/gameshow/assets/" + fileName);
            wordsList = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Unable to read words from {}", fileName);
            logger.error(e.getMessage());
        }

        logger.info("{} words loaded from file!", wordsList.size());

        return wordsList;
    }

    /**
     * Randomly chooses a word from a List.
     * @param wordsList the List of words from which to choose.
     */
    public void chooseWord(List<String> wordsList) {
        this.chosenWord = wordsList.get(rand.nextInt(wordsList.size()));

        logger.info("The chosen word is {}", chosenWord);
    }

    /**
     * Scrambles the letters of the target word.
     * @return a String containing the scrambled letters.
     */
    public String getScrambledWord() {
        List<String> letters = Arrays.asList(this.chosenWord.split(""));
        Collections.shuffle(letters);
        String scrambledWord = String.join("", letters);

        logger.info("The scrambled word is {}", scrambledWord);

        return scrambledWord;
    }

    /**
     * Checks if a guess is correct.
     * @param guess the guess made.
     * @return Boolean representation of guess correctness.
     */
    public boolean guessIsCorrect(String guess) {
        return guess.toUpperCase().equals(this.chosenWord);
    }

}
