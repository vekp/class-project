package minigames.server.shufflingsystem;

import minigames.server.solitaireOrMemory.DeckOfCards.PlayingCard;
import java.util.Random;


public class ShufflingFramework {

        /**
     * This method takes an array of playing card objects and performs 
     * the fisher-yates in place shuffling algorithm on them. 
     * @param cards -> an array of playingCard objects
     * This method will modify the original array so ensure you have a copy of
     * your original array, or an easy way to create a new unshuffled version
     * If you want to use this to randomise objects in your game, I would recommend
     * overwriting the method, I'll place a commented function interface below. 
     */
    public PlayingCard[] shufle(PlayingCard[] cards){
        //We will need to implement a playing card class, I have a basic one if you'd like to look at it Melinda.
        //It's early days so play around with the code, it can definitely be better than what I've done here.
        Random rand = new Random();
        for(int i = cards.length -1; i > 0; i--){
            int swap = rand.nextInt(i + 1);
            PlayingCard temp = cards[i];
            cards[i] = cards[swap];
            cards[swap] = temp;
        } 
        return cards;
    }
    
}
