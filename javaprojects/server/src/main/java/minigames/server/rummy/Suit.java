package main.java.minigame.server.rummy;


/*
 * loading card images as enum
 */


public enum Suit {
    spades("img/spades.png")
    club("img/club.png"),
    diamond("img/diamond.png"),
    heart(img/hearth.png);


    private  String image;

    private Suit(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }
}
