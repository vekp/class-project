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
     * @param shipClass The class of the ship (Battleship, sub, etc)
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
     * Updates the cells within the current Ship Object, should be performed after every round of enemy firing
     * @param target The Cell that has just been fired upon
     */
    public void updateShipStatus(Cell target) {
        // Get the length of the Cell array within the Ship (the ship's size) and put this within an int variable called "size"

        // initialise a "hits" counter at 0

        // For-loop for cells within the ship
            // For every cell within the Ship, if it has been hit, increment the "hits" counter

            // If the current Cell of the ship is at the same coord as the "target" cell, set the cell-type to "hit"
            // and increment the "hits" counter

        // If size == hits, then the ship should be sunk
            // set sunk to true then exit the function

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
