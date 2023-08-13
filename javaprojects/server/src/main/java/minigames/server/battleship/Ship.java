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

    // Getters and Setters -> should be split up
    public Cell[] getShipParts() {
        return shipParts;
    }

    public void setShipParts(Cell[] shipParts) {
        this.shipParts = shipParts;
    }

    public Coordinate[] getOccupiedCells() {
        return occupiedCells;
    }

    public void setOccupiedCells(Coordinate[] occupiedCells) {
        this.occupiedCells = occupiedCells;
    }

    public boolean isSunk() {
        return sunk;
    }

    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }
}
