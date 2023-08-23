package minigames.server.gameshow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 *  A class to implement the server for the Word Scramble game
 */
public class WordScramble {

    private String chosenWord;
    
    public WordScramble(String wordFileName) {
        chooseWord(getWords(wordFileName));
    }

    private static final Logger logger = LogManager.getLogger(WordScramble.class);
    Random rand = new Random();

    // Reads all lines from `fileName`
    public List<String> getWords(String fileName) {
        List<String> wordsList = new ArrayList<>();
        String wordsFileName = fileName;

        try {
            Path path = Paths.get("src/main/java/minigames/server/gameshow/" + wordsFileName);
            wordsList = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Unable to read words from {}", wordsFileName);
            e.printStackTrace();
        }

        logger.info("{} words loaded from file!", wordsList.size());

        return wordsList;
    }

    // Randomly chooses a word from a given list `wordsList`
    public void chooseWord(List<String> wordsList) {
        this.chosenWord = wordsList.get(rand.nextInt(wordsList.size()));

        logger.info("The chosen word is {}", chosenWord);
    }

    // Given a `word`, 'scrambles' up the letters to use as a puzzle
    public String getScrambledWord() {
        List<String> letters = Arrays.asList(this.chosenWord.split(""));
        Collections.shuffle(letters);
        String scrambledWord = String.join("", letters);

        logger.info("The scrambled word is {}", scrambledWord);

        return scrambledWord;
    }

    // Check if a guess is correct
    public boolean guess(String guess) {
        return guess.toLowerCase().equals(this.chosenWord);
    }

}
