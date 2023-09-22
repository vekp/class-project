package minigames.client.hangman;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class KeywordPanelTest {
    KeywordPanel keywordPanel;

    @BeforeEach
    void setUp(){
        keywordPanel = new KeywordPanel();
    }

    /**
    * AvailableLetterTest function tests the use letter function in keywordPane.
    *
    * @author Sushil Kandel
    * */

    @Test
    @DisplayName("Set Letter Used to true when letter is used")
    void availableLetterTest(){
        keywordPanel.useLetter('A');
        assertTrue(keywordPanel.letterUsed[0]);
    }

    /**
     * resetLetterTest function tests the resetLetter Function in KeywordPanel
     *
     * @author Sushil Kandel
     * **/

    @Test
    @DisplayName("Reset all letters to unused")
    void resetLetterTest(){
        for (char c: new char[]{'A','B','C'}){
            keywordPanel.useLetter(c);
        }
        keywordPanel.resetLetters();
        for(int i = 0; i < 3; i++){
            assertFalse(keywordPanel.letterUsed[i]);
        }
    }
}
