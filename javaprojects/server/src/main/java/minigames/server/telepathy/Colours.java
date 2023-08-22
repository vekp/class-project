package minigames.server.telepathy;

/**
 * Colour enum, a list of constants repesenting square colours
 */


public enum Colours{

RED, PINK, BLUE, BROWN, YELLOW, ORANGE, PURPLE, GREEN, GREY;

public String toString(){
    switch (this){
        case RED: return "Red";
        case PINK: return "Pink";
        case BLUE: return "Blue";
        case BROWN: return "Brown";
        case YELLOW: return "Yellow";
        case ORANGE: return "Orange";
        case PURPLE: return "Purple";
        case GREEN: return "Green";
        case GREY: return "Grey";
        default: return "NA";
    }

}

}