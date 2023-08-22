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
     * @param x The x coordinate for the target
     * @param y The y coord
     */
    public void updateShipStatus(int x, int y) {
        // Get the length of the Cell array within the Ship (the ship's size) and put this within an int variable called "size"
        boolean iWantPrintouts = true;
        Cell target = new Cell(x, y);
        if(iWantPrintouts){
            System.out.println("Updating ship status");
        }

        Cell[] shipParts = this.getShipParts();

        int size = shipParts.length;
        if(iWantPrintouts){
            System.out.println("Size of this " + this.getShipClass() +": " + size);
        }
        int hits = 0;


        // Loop through all cells within the current ship to see if any of the cells were hit by the previous shot
        for (int i = 0; i < size; i++){
            Cell current = shipParts[i];
            if(iWantPrintouts){
                System.out.println("Current cell: " + current);
                System.out.println("Current cell coords: " + current.getBothCoords());
                System.out.println("Current cell y: " +current.getVerticalCoordString() + "Target cell y: "+target.getVerticalCoordString());
                System.out.println("Current cell x: "+current.getHorizontalCoord() + "Target cell x: " +target.getHorizontalCoord());
                System.out.println("Has current cell been shot? " + current.hasBeenShot());
            }
            // For every cell within the Ship, if it has been hit, increment the "hits" counter
            if(current.hasBeenShot()){
                hits ++;
            }
            // If the current Cell of the ship is at the same coord as the "target" cell, set the cell-type to "hit"
            // and increment the "hits" counter
            if(current.getBothCoords().equals(target.getBothCoords())){
                if(iWantPrintouts) {
                    System.out.println("Current coordinates: " + current.getBothCoords());
                    System.out.println("Target coordinates: " + target.getBothCoords());
                }
                shipParts[i].shoot();
                hits++;
            }
        }
        // update the cells of the Ship Object
        this.shipParts = shipParts;

        // if all cells within the Ship have been hit, sink the ship
        if(size==hits){
            this.sink();
            System.out.println("You sank my "+ this.getShipClass());
        }



    }

    /**
     * Sinks the current ship
     */
    public void sink() {
        this.sunk=true;
    }
}
