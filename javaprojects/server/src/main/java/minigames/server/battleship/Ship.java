package minigames.server.battleship;

/**
 *
 */
public class Ship {
    // Fields
    Cell[] shipParts;
    Coordinate[] occupiedCells;
    boolean sunk;

    // Constructor

    /**
     *
     * @param shipParts
     * @param occupiedCells
     */
    Ship(Cell[] shipParts, Coordinate[] occupiedCells) {
        this.shipParts = shipParts;
        this.occupiedCells = occupiedCells;
        this.sunk = false;
    }
}
