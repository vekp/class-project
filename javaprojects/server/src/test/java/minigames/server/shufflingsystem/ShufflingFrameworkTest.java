package minigames.server.shufflingsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.solitaireOrMemory.DeckOfCards;
import minigames.server.solitaireOrMemory.DeckOfCards.PlayingCard;

import java.util.Random;

public class ShufflingFrameworkTest {

    private static final Logger logger = LogManager.getLogger(ShufflingFrameworkTest.class);

    @Test
    @DisplayName("Is shuffle producing consistent results?")
    public void shuffleMethodShufflesArray() {
        long seed = 44567;
        Random random = new Random(seed);
        Random random2 = new Random(seed);
        PlayingCard[] controlDeck = new DeckOfCards(5, false).getCards();
        PlayingCard[] deckOfCards = new DeckOfCards(5, false).getCards();

        ShufflingFramework.shuffle(deckOfCards, random);
        ShufflingFramework.shuffle(controlDeck, random2);
        for (int i = 0; i < controlDeck.length; i++) {
            assertTrue(controlDeck[i].equals(deckOfCards[i]));
        }

    }

    @Test
    @DisplayName("Full deck shuffle test")
    /**
     * This is more of a determanism test as is the previous test. 
     * Both tests are attempting to prove that if 2 identical decks are 
     * shuffled with the same random seed that the results will be the same
     */
    public void shuffleFullDeckTest() {
        long seed = 44567;
        Random random = new Random(seed);
        Random random2 = new Random(seed);

        PlayingCard[] fullDeckControl = new DeckOfCards().getCards();
        PlayingCard[] fullDeckCards = new DeckOfCards().getCards();

        ShufflingFramework.shuffle(fullDeckControl, random);
        ShufflingFramework.shuffle(fullDeckCards, random2);

        assertArrayEquals(fullDeckControl, fullDeckCards);        
    }

}
