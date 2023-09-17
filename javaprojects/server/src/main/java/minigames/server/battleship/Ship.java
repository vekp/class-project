package minigames.server.battleship;

import minigames.server.achievements.AchievementHandler;

import static minigames.server.battleship.achievements.*;

/**
 * A Class to represent a ship on the game board
 */
public class Ship {

    // Fields
    private String shipClass;  // Name of the vessel
    private Cell[] shipParts;  // An Array containing all the Cells of the ship
    private boolean sunk;      // A boolean for whether the ship has been sunk
    private boolean justSunk;  // Boolean for whether the ship has just been sunk on that turn
    int hits;
    int size;
    private int row;
    private int col;
    private boolean horizontal;
    String owner;              // The name of the player that owns this ship
    AchievementHandler achievementHandler;

    // Constructor

    /**
     * Creates a new Ship Object, taking in the desired position and size (shipParts) of the ship
     * @param shipClass The class of the ship (Battleship, sub, etc)
     * @param shipParts The composition of the cells contained within the ship
     */
    public Ship(String shipClass, Cell[] shipParts, int row, int col, boolean horizontal) {
        this.shipClass = shipClass;
        this.shipParts = shipParts;
        this.sunk = false;
        this.justSunk = false;
        this.hits = 0;
        this.size = shipParts.length;
        this.row = row;
        this.col = col;
        this.horizontal = horizontal;
        this.achievementHandler = new AchievementHandler(BattleshipServer.class);
        this.owner = "";
    }

    /**
     * A function to return information about the current Ship in a formatted String
     * @return A String containing some information about the current ship object
     */
    @Override
    public String toString(){
        return "This is a "+shipClass+ " with total hitpoints of "+this.size +
                " is it currently sunk?: " + this.sunk +"\nIt's owner is " + owner;
    }

    // Getters

    /**
     * Returns the ship class
     * @return String value
     */
    public String getShipClass() {
        return this.shipClass;
    }

    /**
     * Returns the Cell composition of the current ship
     * @return A Cell Array containing all the cells for the current ship
     */
    // Getters and Setters -> should be split up
    public Cell[] getShipParts() {
        return this.shipParts;
    }

    /**
     * Returns whether the current Ship Object has been sunk
     * @return The boolean representing whether the ship's status is sunk
     */
    public boolean isSunk() {
        return this.sunk;
    }

    /**
     * get the just sunk status for a Ship object - used to control terminal output to the player
     * @return boolean value determining whether the ship has just been sunk
     */
    public boolean isJustSunk() {
        return this.justSunk;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    // Setters

    /**
     * Set the composition of a ship (ship hull, ship left-most part, etc.)
     * @param shipParts An Array containing Cell Objects representing the current ship
     */
    public void setShipParts(Cell[] shipParts) {
        this.shipParts = shipParts;
    }

    /**
     * Set the owner for the current ship
     * @param owner The name of the player that the ship belongs to
     */
    public void setOwner(String owner){
        this.owner = owner;
    }

    /**
     * Set the just sunk status for a Ship object - used to control terminal output to the player
     * @param sunk boolean value determining whether the ship has just been sunk
     */
    public void setJustSunk(boolean sunk) {
        this.justSunk = sunk;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Updates the cells within the current Ship Object, should be performed after every round of enemy firing.
     * @param row The x coordinate for the target
     * @param col The y coord
     */
    public Ship updateShipStatus(int col, int row) {

        hits = 0;
        // booleans that can be set to reduce console spam
        boolean iWantPrintouts = false;
        boolean iWantMorePrintouts = false;
        // Get the length of the Cell array within the Ship (the ship's size) and put this within an int variable called "size"
        Cell target = new Cell(row, col);

        Cell[] shipParts = this.getShipParts();

        int size = shipParts.length;


        // Loop through all cells within the current ship to see if any of the cells were hit by the previous shot
        for (int i = 0; i < size; i++) {
            Cell current = shipParts[i];
            if (iWantMorePrintouts) {
                System.out.println("Current cell coords: " + current.getBothCoords());
                System.out.println("Target cell coords: " + target.getBothCoords());
                System.out.println("Has current cell been shot? " + current.hasBeenShot());
            }
            // For every cell within the Ship, if it has been hit, increment the "hits" counter
            if (current.hasBeenShot()) {
                hits++;
            }
            // If the current Cell of the ship is at the same coord as the "target" cell, set the cell-type to "hit"
            // and increment the "hits" counter
            if (current.getBothCoords().equals(target.getBothCoords())) {

                shipParts[i].shoot();
                if (iWantMorePrintouts) {
                    System.out.println("Shot on target");
                    System.out.println("Hit here: " + current.getBothCoords());
                    System.out.println("Target coords: " + target.getBothCoords());
                }
                hits++;
            }
        }
        // update the cells of the Ship Object
        this.shipParts = shipParts;
        if (iWantPrintouts) {
            System.out.println("Ship class: " + this.getShipClass());
            System.out.println("hits: " + this.hits);
            System.out.println("to hit: " + this.size);
        }
        // if all cells within the Ship have been hit, sink the ship
        if (this.size == hits && !this.sunk) {
            this.sink();
            this.setJustSunk(true);  // Set just sunk
            if (this.getShipClass().equals("Carrier")) {
                achievementHandler.unlockAchievement(owner, THE_BIGGER_THEY_ARE.toString());
            } else if (this.getShipClass().equals("Patrol Boat")) {
                achievementHandler.unlockAchievement(owner, THREE_HOUR_CRUISE.toString());
            } else if (this.getShipClass().equals("Submarine")) {
                achievementHandler.unlockAchievement(owner, HUNTER_KILLER.toString());
            } else if (this.getShipClass().equals("Destroyer")) {
                achievementHandler.unlockAchievement(owner, DESTROYER_DESTROYED.toString());
            } else if (this.getShipClass().equals("Battleship")) {
                achievementHandler.unlockAchievement(owner, TITLE_DROP.toString());
            }
        } else {
            if (this.justSunk) this.setJustSunk(false);
        }
        return this;
    }

    /**
     * Sinks the current ship
     */
    public void sink() {
        this.sunk=true;
    }
}
