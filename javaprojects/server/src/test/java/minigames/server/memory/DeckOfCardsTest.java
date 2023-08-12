package minigames.server.memory;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.solitaireOrMemory.DeckOfCards;
import minigames.server.solitaireOrMemory.DeckOfCards.PlayingCard;

public class DeckOfCardsTest {

    private static final Logger logger = LogManager.getLogger(DeckOfCardsTest.class);
    
    String[] suits = { "Clubs", "Diamonds", "Hearts", "Spades" };
    String[] values = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
    /**
     * This unit test will test to see if each card in the deck has been constructed as per the constructor.
     * Are the cards the right values and suits. s
     */
    @Test
    @DisplayName("Testing deck constructor")
    public void testDeckConstructor(){
        PlayingCard[] cardDeck = new DeckOfCards().getCards();
        int counter = 0;
        for(int i = 0; i < suits.length; i++){
            for(int j = 0; j < values.length; j++ ){
                assertTrue(cardDeck[counter].getSuit() == suits[i] && cardDeck[counter].getValue() == values[j]);
                counter++;
            }
        }

    }


/**
 * This test will check to see if building a deck with pairs works
 * I anticipate this test to fail right now because I have not overridden the default implementation of clone
 * Deep copies of PlayingCard should not be possible at this time, so the test will fail.
 * It did fail but more research into clone() showed that it was not worth the added complexity
 * Clone was replaced with a copy constructor -> test now passes
 */

    @Test
    @DisplayName("Testing pair constructor")
    public void testPairConstructor(){
        int numberOfCards = 2;
        PlayingCard[] pairDeck = new DeckOfCards(numberOfCards, true).getCards();
        for(int i = 0; i < numberOfCards/2; i+=2){
            assertTrue(pairDeck[i].equals(pairDeck[i+1]));
            // assertTrue(pairDeck[i].getValue().equals(pairDeck[i+1].getValue()));
        }
    }
}
