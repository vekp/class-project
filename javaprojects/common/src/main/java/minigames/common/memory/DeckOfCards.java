package minigames.common.memory;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DeckOfCards {

    /**
     * A class that represents a playing card with data members for suit, value and
     * if the card is face up or not
     * There are no setters for the card, it's immutable and won't change.
     * The only data member we can change is the faceUp value, which we can change
     * from true to false and back
     * with the flipCard() method.
     */


    public class PlayingCard {
        private String suit;
        private String value;
        private boolean faceUp;

        public PlayingCard(String suit, String value, boolean faceUp) {
            this.suit = suit;
            this.value = value;
            this.faceUp = faceUp;
        }


        /*
         * This is a copy constructor? I think?
         * I saw someone post an interface of a copy constructor and tried to infer how it would work.
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
    private String[] values = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };


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

    /**
     * This is the original constructor if you want a non-standard length deck or deck of pairs 
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


    // public DeckOfCards(int numberOfCards, boolean pairs){
    //     Random random = new Random(44567);
    //     Map<String, LinkedList<String>> combinations = new HashMap<>();
    //     cardStack = new PlayingCard[numberOfCards];
    //     int counter = 0;

    //     //Creating a hash map the keys will be the values of the cards 2-Ace
    //     //The value will be a linked list of the suits
    //     for(int i = 0; i < values.length; i++){
    //         combinations.put(values[i], Arrays.stream(suits).collect(Collectors.toCollection(LinkedList::new)));
    //     }
    //     //When a value is selected we check to see if it has any more valid suit combinations
    //     //If it does we proceed, if not we continue to roll new values until we get one that has available combinations left
    //     for(int i = 0; i < numberOfCards; i++){
    //         String value = values[random.nextInt(values.length)];
    //         while(combinations.get(value).size() == 0){
    //             value = values[random.nextInt(values.length)];
    //         }
    //         //Now that we've checked there are available suits to make pick one at random
    //         int suit = random.nextInt(suits.length);
    //         PlayingCard card = new PlayingCard(combinations.get(value).get(suit), value, true);
    //         combinations.get(value).remove(suit);
    //         cardStack[counter] = card;
    //         counter++;
    //         if(pairs == true){
    //             PlayingCard copy = new PlayingCard(card);
    //             cardStack[counter] = copy;
    //             counter++;
    //             i++;
    //         }
    //     }
    // }

    /* 
     * This returns the array which honestly probably violates some OOP principal
     * I think for our purposes this implementation is fine but in a more 'real-world' scenario
     * I would probably return a copy of the array not the array itself
     */
    public PlayingCard[] getCards(){
        return this.cardStack;
    }

}
