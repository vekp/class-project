package minigames.server.shufflingsystem;

import org.junit.jupiter.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.solitaireOrMemory.DeckOfCards;

public class ShufflingFrameworkTest {

    private static final Logger logger = LogManager.getLogger(ShufflingFrameworkTest.class);



    @Test
    public void shuffleMethodShufflesArray(){
        DeckOfCards controlDeck = new DeckOfCards();
        DeckOfCards deckOfCards = new DeckOfCards();

        ShufflingFramework.shuffle(deckOfCards.getCards());
        int counter = 0;
        for(int i = 0; i < deckOfCards.getCards().length; i++){
            //TODO: -- Implement unit test with random seed!
        }   
    }    
}
