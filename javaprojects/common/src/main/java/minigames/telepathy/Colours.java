package minigames.telepathy;

/**
 * Colour enum, a list of constants repesenting tile colours
 */


public enum Colours{

RED, PINK, BLUE, CYAN, YELLOW, ORANGE, MAGENTA, GREEN, GREY;

public String toString(){
    switch (this){
        case RED: return "Red";
        case PINK: return "Pink";
        case BLUE: return "Blue";
        case CYAN: return "Cyan";
        case YELLOW: return "Yellow";
        case ORANGE: return "Orange";
        case MAGENTA: return "Magenta";
        case GREEN: return "Green";
        case GREY: return "Grey";
        default: return "NA";
    }

}

}