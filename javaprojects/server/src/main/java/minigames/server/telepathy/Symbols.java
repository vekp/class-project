package minigames.server.telepathy;


/**
 * Symbols enum, a list of constants representing tile symbols
 */

public enum Symbols{

HEARTS, STARS, DIAMONDS, SPADES, CLUBS, MOONS, CIRCLES, QUESTION_MARKS, EXCALAMATION_MARKS;

public String toString(){
    switch (this){
        case HEARTS: return "<3";
        case STARS: return "*";
        case DIAMONDS: return "d";
        case SPADES: return "s";
        case CLUBS: return "c";
        case MOONS: return "D";
        case CIRCLES: return "O";
        case QUESTION_MARKS: return "??";
        case EXCALAMATION_MARKS: return "!!";
        default: return "NA";

    }

 }
}