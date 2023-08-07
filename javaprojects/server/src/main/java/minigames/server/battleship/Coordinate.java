package minigames.server.battleship;

/**
 * Class representing a coordinate
 */
public class Coordinate {

    // Fields
    private String vertical;
    private Integer horizontal;

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

    /**
     *
     * @return
     */
    public String getVertical() {
        return vertical;
    }

    /**
     *
     * @return
     */
    public Integer getHorizontal() {
        return horizontal;
    }

    // Create a method to compare if two coordinates are the same?
}
