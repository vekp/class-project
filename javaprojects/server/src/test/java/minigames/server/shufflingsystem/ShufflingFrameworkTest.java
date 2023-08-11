package minigames.server.shufflingsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.solitaireOrMemory.DeckOfCards;
import minigames.server.solitaireOrMemory.DeckOfCards.PlayingCard;

import java.util.Arrays;
import java.util.Random;
public class ShufflingFrameworkTest {

    private static final Logger logger = LogManager.getLogger(ShufflingFrameworkTest.class);



    @Test
    @DisplayName("Is shuffle producing consistent results?")
    public void shuffleMethodShufflesArray(){
        long seed = 44567;
        Random random = new Random(seed);
        Random random2 = new Random(seed);
        PlayingCard[] controlDeck = new DeckOfCards(5, false).getCards();
        PlayingCard[] deckOfCards = new DeckOfCards(5, false).getCards();

        ShufflingFramework.shuffle(deckOfCards, random);
        ShufflingFramework.shuffle(controlDeck, random2);
        for(int i = 0; i < controlDeck.length; i++){
            assertTrue(controlDeck[i].equals(deckOfCards[i]));
        }

    }    
}
