package minigames.client.hangman;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HangmanPanelTest {
    HangmanPanel hangmanPanel;

    @BeforeEach
    void setUp(){ hangmanPanel = new HangmanPanel();}

    /**
     * disableInputGameLostTest function to check input is disable when game is lost
     *
     * @author Sushil Kandel
     *
     **/
    @Test
    @DisplayName("Disable Input when Game Lost")
    void disableInputGameLostTest(){
        hangmanPanel.gameLost = true;
        hangmanPanel.handleLetterPress('A');
        assertEquals(0, hangmanPanel.errors);
    }

    /**
     * disableInputGameWonTest function to check input is disable when game won
     *
     * @author Sushil Kandel
     *
     **/
    @Test
    @DisplayName("Disable Input when Game Lost")
    void disableInputGameWoTest() {
        hangmanPanel.gameWon = true;
        hangmanPanel.handleLetterPress('A');
        assertTrue(hangmanPanel.gameWon);
    }

}

