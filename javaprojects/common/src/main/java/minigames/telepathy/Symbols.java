package minigames.telepathy;


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

public static Symbols fromString(String inString){
    switch(inString){
        case "<3": return Symbols.HEARTS;
        case "*": return Symbols.STARS;
        case "d": return Symbols.DIAMONDS;
        case "s": return Symbols.SPADES;
        case "c": return Symbols.CLUBS;
        case "D": return Symbols.MOONS;
        case "o": return Symbols.CIRCLES;
        case "??": return Symbols.QUESTION_MARKS;
        case "!!": return Symbols.EXCALAMATION_MARKS;
        default: return null;
    }
}
}