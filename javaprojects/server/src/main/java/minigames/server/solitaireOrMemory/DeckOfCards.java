package minigames.server.solitaireOrMemory;

public class DeckOfCards {

    public class PlayingCard {
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
        public void flipCard(){
            faceUp = !faceUp;
        }
    }


    private PlayingCard[] cardStack = new PlayingCard[52];

    private String[] suits = new String[]{"Clubs","Diamonds","Hearts","Spades"};
    private String[] values = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};

    public DeckOfCards() {
        int counter = 0;
        for(int i = 0; i < suits.length; i++){
            for (int j = 0; j < values.length; j++){
                PlayingCard card = new PlayingCard(suits[i], values[j], false);
                cardStack[counter] = card;
            }
        }

    }


}
