public class Number {
    package main.java.cosc220.minigame;


public enum Number {
    Ace,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Jack,
    Queen,
    King;

    private final int num;

    private Number() {
        this.num = this.ordinal() + 1;
    }

    public int getNum() {
        return this.num;
    }

    public String getRealValue() {
        // Most often cases should be the first if statement
        if(this.num >= 2 && this.num <= 10) { 
            return Integer.toString(this.num);
        }else if(this.num == 1){
            return "A";
        }else if(this.num == 11){
            return "J";
        } else if(this.num == 12){
            return "Q";
        }else if(this.num == 13){
            return "K";
        }
        return "0";
    }

}

}
