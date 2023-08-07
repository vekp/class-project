package minigames.server.solitaireOrMemory;

import minigames.server.solitaireOrMemory.DeckOfCards.PlayingCard;
import java.util.Random;

public class FisherYatesShuffle {



    public FisherYatesShuffle( ){

    }


    /**
     * This method takes an array of playing card objects and performs 
     * the fisher-yates in place shuffling algorithm to them. 
     * @param cards -> an array of playingCard objects
     * This method will modify the original array so ensure you have a copy of
     * your original array, or an easy way to create a new unshuffled version
     * If you want to use this to randomise objects in your game, I would recommend
     * overwriting the method, I'll place a commented function interface below. 
     */
    public PlayingCard[] shuffle(PlayingCard[] cards){
        Random rand = new Random();
        for(int i = cards.length -1; i > 0; i--){
            int j = rand.nextInt(i);
            PlayingCard temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
        return cards;
    }
    
    public int[] intShuffle(int[] integers){
        Random rand = new Random();
        for(int i = integers.length -1; i > 0; i--){
            int j = rand.nextInt(i);
            int temp = integers[i];
            integers[i] = integers[j];
            integers[j] = temp;
        }
        return integers;
    }
}
