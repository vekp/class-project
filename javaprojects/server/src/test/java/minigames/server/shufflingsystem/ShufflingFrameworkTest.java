package minigames.server.shufflingsystem;

import org.junit.jupiter.api.*;

import minigames.common.memory.DeckOfCards;
import minigames.common.memory.DeckOfCards.PlayingCard;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ShufflingFrameworkTest {

    private static final Logger logger = LogManager.getLogger(ShufflingFrameworkTest.class);


    public <T> List<T> arrayToArrayList(T array[]){
        List<T> list = new ArrayList<T>();

        for(int i = 0; i < array.length; i++){
            list.add(array[i]);
        }
        return list;
    }

    public <T> List<T> arrayToLinkedList(T array[]){
        List<T> list = new LinkedList<T>();
        for(int i = 0; i < array.length; i++){
            list.add(array[i]);
        }
        return list;
    }




    @Test
    @DisplayName("Shuffle Empty Array")
    public void testEmptyShuffle(){
        Integer[] array = null;

        assertThrows(RuntimeException.class,() ->{
            ShufflingFramework.shuffle(array);
        });
    }


    /**
     * This test is to ensure that when a deck of cards is shuffled that no elements are accidentally discarded.
     * Every index should contain an instance of PlayingCard
     */

    @Test
    @DisplayName("Are all elements preserved")
    public void elementsPreservedTest(){
        
        PlayingCard[] deckOfCards = new DeckOfCards().getCards();

        ShufflingFramework.shuffle(deckOfCards);

        for(int i = 0; i < deckOfCards.length; i++){
            assert deckOfCards[i] instanceof PlayingCard;
        }
    }


    @Test
    @DisplayName("Test shuffle with ArrayList")
    public void shuffleArrayList(){
        long seed = 44567;
        Random random = new Random(seed);
        Random random2 = new Random(seed);

        PlayingCard[] controlDeck = new DeckOfCards(5, false).getCards();
        List<PlayingCard> controlArrayList = arrayToArrayList(controlDeck);
        PlayingCard[] deckOfCards = new DeckOfCards(5,false).getCards();
        List<PlayingCard> cardArrayList = arrayToArrayList(deckOfCards);

        ShufflingFramework.shuffle(controlArrayList, random);
        ShufflingFramework.shuffle(cardArrayList, random2);

        for(int i = 0; i < controlArrayList.size(); i++){
            assertTrue(controlArrayList.get(i).equals(cardArrayList.get(i)));
        }

    }

    @Test
    @DisplayName("Test shuffle with LinkedList")
    public void shuffleLinkedList(){
        long seed = 44567;
        Random random = new Random(seed);
        Random random2 = new Random(seed);

        PlayingCard[] controlDeck = new DeckOfCards(5, false).getCards();
        List<PlayingCard> controlLinkedList = arrayToLinkedList(controlDeck);
        PlayingCard[] deckOfCards = new DeckOfCards(5, false).getCards();
        List<PlayingCard> cardLinkedList = arrayToLinkedList(deckOfCards);

        ShufflingFramework.shuffle(controlLinkedList, random);
        ShufflingFramework.shuffle(cardLinkedList, random2);

        for(int i = 0;i < controlLinkedList.size(); i++){
            assertTrue(controlLinkedList.get(i).equals(cardLinkedList.get(i)));
        }
    }

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
