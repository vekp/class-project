package minigames.telepathy;

import java.awt.*;

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

    /** Switch code suggested by Craig Pettifor
     * Returns a Color Object to be used when defining the background colour of JPanels and JButtons
     * @return A Color Object for the current enum
     */
    public Color getColor(){
        switch (this){
            case RED: return Color.RED;
            case GREEN: return Color.GREEN;
            case PINK: return Color.PINK;
            case BLUE: return Color.BLUE;
            case GREY: return Color.GRAY;
            case YELLOW: return Color.YELLOW;
            case CYAN: return Color.CYAN;
            case MAGENTA: return Color.MAGENTA;
            case ORANGE: return Color.ORANGE;
            default: return Color.BLACK;
        }
    }
}