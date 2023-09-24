package minigames.telepathy;


/**
 * Symbols enum, a list of constants representing tile symbols
 */

public enum Symbols{

HEARTS, STARS, DIAMONDS, SPADES, CLUBS, MOONS, CIRCLES, QUESTION_MARKS, EXCLAMATION_MARKS;


    /**
     * gets the path for the black symbols
     * 
     * @return: String with the path for the symbol files (colour black) 
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

    /**
     * Get a string representation of the Symbols enum.
     * 
     * @return: A String that can be used to represent a Symbol enum.
     */
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
            case EXCLAMATION_MARKS: return "!!";
            default: return "NA";
        }
    }

    /**
     * Convert the String output of this enums toString method to the corresponding
     * Symbol enum constant.
     * 
     * @param inString: String to be converted to a Symbol enum value.
     * @return The corresponding Symbol enum. If the string does not match one
     *  of the output values from toString return null.
     */
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
            case "!!": return Symbols.EXCLAMATION_MARKS;
            default: return null;
        }
    }
}