package minigames.server.battleship;

/**
 * A Class to represent a ship on the game board
 */
public class Ship {
    // Fields
    Cell[] shipParts;           // An Array containing all the Cells of the ship
    Coordinate[] occupiedCells; // An Array containing the location of the ship
    boolean sunk;               // A boolean for whether the ship has been sunk

    // Constructor

    /**
     * Creates a new Ship Object, taking in the desired position and size (shipParts) of the ship
     * @param shipParts The composition of the cells contained within the ship
     * @param occupiedCells The cells (location) that the ship is occupying
     */
    Ship(Cell[] shipParts, Coordinate[] occupiedCells) {
        this.shipParts = shipParts;
        this.occupiedCells = occupiedCells;
        this.sunk = false;
    }
}
