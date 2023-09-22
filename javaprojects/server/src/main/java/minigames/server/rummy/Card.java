// package main.java.minigame.server.rummy;


public class Card {
    // Card parameters
    private Number number; // 1- 13. 11 is J, 12 is Q, 13 is K;
    private Suit suit; // suit name with an image

    // Constructor to create the card with appropriate number and suit
    public Card(Number number, Suit suit) {
        this.number = number;
        this.suit = suit;
    }

    // Getter method - to get the number of the card
    public Number getNumber() {
        return this.number;
    }

    // Getter method - to get the suit of the card
    public Suit getSuit() {
        return this.suit;
    }

    // description method returns the description of the card
    public String description() {
        StringBuilder cardDiscrip = new StringBuilder("");
        cardDiscrip.append(" " + this.number.getRealValue());
        cardDiscrip.append(" of " + this.suit);
        return cardDiscrip.toString();
    }

    // method will allow us to compare a card to another card
    public int compareTo(Card anotherCard) {
        if(this.number.getNum() > anotherCard.number.getNum()) {
            return 1;
        }else if(this.number.getNum() == anotherCard.number.getNum()) {
            return 0;
        }else {
            return -1;
        }
    }


}
