package minigames.server.battleship;

/**
 * This class represents the grid coordinates and is used to convert user input
 */
public class Coordinate {

    // Fields
    private String vertical;   // This could also be changed to a char if we wanted to be finicky - CRAIG
    private Integer horizontal;// And this could be changed to a byte if we wanted to be ultra finicky - CRAIG

    // Constructor

    /**
     * Constructor to create a coordinate pair
     * @param vertical coordinate (A-J)
     * @param horizontal coordinate (0-9)
     */
    Coordinate(String vertical, Integer horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    // Getters
    /**
     * Get the vertical coordinate from the Coordinate object
     * @return A String containing the vertical position (A - J)
     */
    public String getVertical() {
        return vertical;
    }

    /**
     * Get the horizontal coordinate from the Coordinate object
     * @return An Integer containing the horizontal position (0 - 9)
     */
    public Integer getHorizontal() {
        return horizontal;
    }

    // Setters
    /**
     * Sets the vertical value to the user's input
     * @param vertical A string value from the user
     */
    public void setVertical(String vertical) {
        this.vertical = vertical;
    }
    /**
     * Sets the horizontal value to the user's input
     * @param horizontal an int value from the user
     */
    public void setHorizontal(Integer horizontal) {
        this.horizontal = horizontal;
    }


    // Create a method to compare if two coordinates are the same?
}
