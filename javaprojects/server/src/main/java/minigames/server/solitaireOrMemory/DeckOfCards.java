package minigames.server.solitaireOrMemory;

import java.util.Random;

public class DeckOfCards {

    /**
     * A class that represents a playing card with data members for suit, value and
     * if the card is face up or not
     * There are no setters for the card, it's immutable and won't change.
     * The only data member we can change is the faceUp value, which we can change
     * from true to false and back
     * with the flipCard() method.
     */
    public class PlayingCard implements Cloneable{
        private String suit;
        private String value;
        private boolean faceUp;

        public PlayingCard(String suit, String value, boolean faceUp) {
            this.suit = suit;
            this.value = value;
            this.faceUp = faceUp;
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

        // Incorporating clone() and making the PlayingCard object cloneable
        // This will enable easy deep copies for when we want to create pairs of cards.
        // Wasn't strictly necessary to do it this way, but it makes the code later a
        // lot cleaner.
        // From the reading I've done, clone() is more efficient then new, so I thought
        // I'd just do it this way.
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
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
     * This constructor will create slightly modified card decks, with fewer cards, or with pairs of cards for example
     * @param numberOfCards an int representing the number of cards (if you want 8 pairs you would enter 16 here)
     * @param pairs a boolean decribing if you want the cards to be in pairs or not
     */
    public DeckOfCards(int numberOfCards, boolean pairs) {
        Random rand = new Random();
        cardStack = new PlayingCard[numberOfCards];
        if (numberOfCards % 2 == 1) {
            System.out.println("If you want pairs please enter an even number");
            System.exit(-2);
        }
        if (pairs == true) {
            for (int i = 0; i < numberOfCards / 2; i++) {
                PlayingCard card = new PlayingCard(suits[rand.nextInt(suits.length)], values[i], true);
                cardStack[i] = card;
                try {
                    //Cloning the card object that's already been made, we want to make pairs of cards
                    //The cloned object is just of type Object, so it needs to be cast to PlayingCard
                    cardStack[i + 1] = (PlayingCard) card.clone();
                } catch (CloneNotSupportedException e) {
                    System.out.println("PlayingCard cannot be cloned " + e.getCause());
                    System.exit(-1);
                }

            }
        } else {
            for (int i = 0; i < numberOfCards; i++) {
                PlayingCard card = new PlayingCard(suits[rand.nextInt(suits.length)], values[i], true);
                cardStack[i] = card;
            }
        }
    }

}
