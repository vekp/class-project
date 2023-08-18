package minigames.server.battleship;

/**
 * A Class to represent a ship on the game board
 */
public class Ship {
    // Fields
    private String shipClass;
    private Cell[] shipParts;           // An Array containing all the Cells of the ship
    private boolean sunk;               // A boolean for whether the ship has been sunk

    // Constructor

    /**
     * Creates a new Ship Object, taking in the desired position and size (shipParts) of the ship
     * @param shipParts The composition of the cells contained within the ship
     */
    public Ship(String shipClass, Cell[] shipParts) {
        this.shipClass = shipClass;
        this.shipParts = shipParts;
        this.sunk = false;
    }

    // These will likely be wanted when custom ship movement is added

    /**
     * Returns the ship class
     * @return String value
     */
    public String getShipClass() {
        return shipClass;
    }

    /**
     * Returns the Cell composition of the current ship
     * @return A Cell Array containing all the cells for the current ship
     */
    // Getters and Setters -> should be split up
    public Cell[] getShipParts() {
        return shipParts;
    }

    /**
     * Set the composition of a ship (ship hull, ship left-most part, etc.)
     * @param shipParts An Array containing Cell Objects representing the current ship
     */
    public void setShipParts(Cell[] shipParts) {
        this.shipParts = shipParts;
    }

    /**
     * Returns whether the current Ship Object has been sunk
     * @return The boolean representing whether the ship's status is sunk
     */
    public boolean isSunk() {
        return sunk;
    }

    /**
     * Sets the boolean for the current ship
     * @param sunk A boolean to represent whether the ship has been sunk
     */
    public void setSunk(boolean sunk) {
        // We could probably just change this to a public void sink() function, since the boolean is false by default
        this.sunk = sunk;
    }
}
