package minigames.common.memory;

import java.util.*;
import java.util.stream.*;

public class DeckOfCards {

    /**
     * A class that represents a playing card with data members for suit, value and
     * if the card is face up or face down
     * There are no setters for the card, it's immutable and won't change.
     * The only data member we can change is the faceUp value, which we can change
     * from true to false and back
     * with the flipCard() method.
     */


    public class PlayingCard {
        private String suit, value;
        private boolean faceUp;

        public PlayingCard(String suit, String value, boolean faceUp) {
            this.suit = suit;
            this.value = value;
            this.faceUp = faceUp;
        }


        /*
         * This is a constructor to generate a duplicate of an existing playing card
         */
        public PlayingCard(PlayingCard cardToCopy){
            this.suit = cardToCopy.getSuit();
            this.value = cardToCopy.getValue();
            this.faceUp = cardToCopy.isFaceUp();
        }

        public String getSuit() {
            return suit;
        }

        public String getValue() {
            return value;
        }

        public boolean isFaceUp() {
            return faceUp;
        }

        public void flipCard() {
            faceUp = !faceUp;
        }

        /**
         * Overriding the equals operator so that playing cards can be tested for equality. 
         */
        @Override
        public boolean equals(Object o){
            if(o == this) return true;

            if(!(o instanceof PlayingCard)) return false;

            PlayingCard p = (PlayingCard) o;
            return value.equals(p.value) && suit.equals(p.suit);
        }

    }

    private PlayingCard[] cardStack;
    private String[] suits = new String[] { "Clubs", "Diamonds", "Hearts", "Spades" };
    private String[] values = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "JACK", "QUEEN", "KING", "ACE" };


    //Default constructor if you just want a standard deck of playing cards
    public DeckOfCards() {
        cardStack = new PlayingCard[52];
        int counter = 0;
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < values.length; j++) {
                PlayingCard card = new PlayingCard(suits[i], values[j], true);
                cardStack[counter] = card;
                counter++;
            }
        }
    }

    public DeckOfCards(DeckOfCards deckOfCards){
        cardStack = new PlayingCard[deckOfCards.getCards().length];
    }

    /**
     * This is a constructor if you want a non-standard length deck or deck of pairs 
     * This constructor will create slightly modified card decks, with fewer cards, or with pairs of cards for example
     * @param numberOfCards an int representing the number of cards (if you want 8 pairs you would enter 16 here)
     * @param pairs a boolean decribing if you want the cards to be in pairs or not
     */
    public DeckOfCards(int numberOfCards, boolean pairs) {
        Random rand = new Random();
        cardStack = new PlayingCard[numberOfCards];
        if (numberOfCards % 2 == 1 && pairs) {
            throw new IllegalArgumentException("Deck of pairs cannot be constructed", new Throwable("If pairs == true, number of cards % 2 must == 0"));
        }
        if (pairs) {
            for (int i = 0; i < numberOfCards; i+=2) {
                PlayingCard card = null;
                while (card == null) {
                    card = new PlayingCard(suits[rand.nextInt(suits.length)], values[rand.nextInt(values.length)], true);
                    if (!Arrays.asList(cardStack).contains(card) && card != null) { // no duplicate cards allowed
                        PlayingCard cardPair = new PlayingCard(card);
                        cardStack[i] = card;
                        cardStack[i+1] = cardPair;
                    } else {
                        card = null;
                    } 
                }
            }
        } else {
            for (int i = 0; i < numberOfCards; i++) {
                PlayingCard card = null;
                while (card == null) {
                    card = new PlayingCard(suits[rand.nextInt(suits.length)], values[rand.nextInt(values.length)], true);
                    if (!Arrays.asList(cardStack).contains(card) && card != null) { // no duplicate cards allowed
                        cardStack[i] = card;
                    } else {
                        card = null;
                    }                    
                }
            }
        }
    }

    /* 
     * Method to return the array holding the deck of cards
     */
    public PlayingCard[] getCards(){
        return this.cardStack;
    }

}
