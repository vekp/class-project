package minigames.server.battleship;

/**
 * A Class to represent a ship on the game board
 */
public class Ship {
    // Fields
    private Cell[] shipParts;           // An Array containing all the Cells of the ship
    private Coordinate[] occupiedCells; // An Array containing the location of the ship
    private boolean sunk;               // A boolean for whether the ship has been sunk

    // Constructor

    /**
     * Creates a new Ship Object, taking in the desired position and size (shipParts) of the ship
     * @param shipParts The composition of the cells contained within the ship
     * @param occupiedCells The cells (location) that the ship is occupying
     */
    public Ship(Cell[] shipParts, Coordinate[] occupiedCells) {
        this.shipParts = shipParts;
        this.occupiedCells = occupiedCells;
        this.sunk = false;
    }

    // These will likely be wanted when custom ship movement is added
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
     * Returns the coordinates that the ship is currently occupying
     * @return A Coordinate Array of Cell objects for the current ship
     */
    public Coordinate[] getOccupiedCells() {
        return occupiedCells;
    }

    /**
     * Set the coordinate location for the current ship
     * @param occupiedCells An Array containing Coordinate Objects for the ship's location
     */
    public void setOccupiedCells(Coordinate[] occupiedCells) {
        this.occupiedCells = occupiedCells;
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
