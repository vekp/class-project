package minigames.telepathy;


/**
 * Symbols enum, a list of constants representing tile symbols
 */

public enum Symbols{

HEARTS, STARS, DIAMONDS, SPADES, CLUBS, MOONS, CIRCLES, QUESTION_MARKS, EXCLAMATION_MARKS;


/**
 * gets the path for the black symbols
 */
public String getPath(){
    String path = "src/main/resources/telepathyicons/blacksymbols/";
    switch (this){
        case HEARTS: return path + "HEARTS.png";
        case STARS: return path + "STARS.png";
        case DIAMONDS: return path + "DIAMONDS.png";
        case SPADES: return path + "SPADES.png";
        case CLUBS: return path + "CLUBS.png";
        case MOONS: return path + "MOONS.png";
        case CIRCLES: return path + "CIRCLES.png";
        case QUESTION_MARKS: return path + "QUESTION_MARKS.png";
        case EXCLAMATION_MARKS: return path + "EXCLAMATION_MARKS.png";
        default: return "NA";

    }

}


}