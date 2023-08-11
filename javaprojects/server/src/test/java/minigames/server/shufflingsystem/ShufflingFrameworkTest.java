package minigames.server.shufflingsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.solitaireOrMemory.DeckOfCards;

import java.util.Random;
public class ShufflingFrameworkTest {

    private static final Logger logger = LogManager.getLogger(ShufflingFrameworkTest.class);



    @Test
    @DisplayName("Is shuffle producing consistent results?")
    public void shuffleMethodShufflesArray(){
        long seed = 44567;
        Random random = new Random(seed);
        DeckOfCards controlDeck = new DeckOfCards(5, false);
        DeckOfCards deckOfCards = new DeckOfCards(5, false);

        ShufflingFramework.shuffle(deckOfCards.getCards(), random);
        ShufflingFramework.shuffle(controlDeck.getCards(), random);

        for(int i = 0; i < deckOfCards.getCards().length; i++){
            assertTrue(deckOfCards.getCards()[i].equals(controlDeck.getCards()[i]));
        }
    }    
}
