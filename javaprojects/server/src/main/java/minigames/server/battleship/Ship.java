package minigames.server.battleship;

import minigames.server.achievements.AchievementHandler;

import static minigames.server.battleship.achievements.*;

/**
 * A Class to represent a ship on the game board
 */
public class Ship {
    // Fields
    private String shipClass;
    private Cell[] shipParts;           // An Array containing all the Cells of the ship
    private boolean sunk;               // A boolean for whether the ship has been sunk
    int hits;
    int size;
    String owner;                       // The name of the player that owns this ship
    AchievementHandler achievementHandler;

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
        this.hits = 0;
        this.size = shipParts.length;
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
     * Set the owner for the current ship
     * @param owner The name of the player that the ship belongs to
     */
    public void setOwner(String owner){
        this.owner = owner;
    }

    /**
     * Returns whether the current Ship Object has been sunk
     * @return The boolean representing whether the ship's status is sunk
     */
    public boolean isSunk() {
        return sunk;
    }

    /**
     * Updates the cells within the current Ship Object, should be performed after every round of enemy firing.
     * NOTE: These are reversed because the player enters the y coord before the x coord e.g. C1
     * @param x The x coordinate for the target
     * @param y The y coord
     */
    public Ship updateShipStatus(int y, int x) {
        // booleans that can be set to reduce console spam
        hits = 0;
        boolean iWantPrintouts = false;
        boolean iWantMorePrintouts = false;
        // Get the length of the Cell array within the Ship (the ship's size) and put this within an int variable called "size"
        Cell target = new Cell(x, y);

        Cell[] shipParts = this.getShipParts();

        int size = shipParts.length;


        // Loop through all cells within the current ship to see if any of the cells were hit by the previous shot
        for (int i = 0; i < size; i++){
            Cell current = shipParts[i];
            if(iWantMorePrintouts){
                System.out.println("Current cell coords: " + current.getBothCoords());
                System.out.println("Target cell coords: " + target.getBothCoords());
                System.out.println("Has current cell been shot? " + current.hasBeenShot());
            }
            // For every cell within the Ship, if it has been hit, increment the "hits" counter
            if(current.hasBeenShot()){
                hits ++;
            }
            // If the current Cell of the ship is at the same coord as the "target" cell, set the cell-type to "hit"
            // and increment the "hits" counter
            if(current.getBothCoords().equals(target.getBothCoords())){

                shipParts[i].shoot();
                if(iWantPrintouts){
                    System.out.println("Shot on target");
                    System.out.println("Hit here: " + current.getBothCoords());
                    System.out.println("Target coords: " + target.getBothCoords());
                }
                hits++;
            }
        }
        // update the cells of the Ship Object
        this.shipParts = shipParts;
        if(iWantPrintouts) {
            System.out.println("Ship class: " + this.getShipClass());
            System.out.println("hits: " + this.hits);
            System.out.println("to hit: " + this.size);
        }
        // if all cells within the Ship have been hit, sink the ship
        if(this.size==hits ){
            this.sink();
            if(this.getShipClass().equals("Carrier")){
                achievementHandler.unlockAchievement(owner, THE_BIGGER_THEY_ARE.toString());
            } else if (this.getShipClass().equals("Patrol Boat")) {
                achievementHandler.unlockAchievement(owner, THREE_HOUR_CRUISE.toString());
            } else if (this.getShipClass().equals("Submarine")){
                achievementHandler.unlockAchievement(owner, HUNTER_KILLER.toString());
            } else if (this.getShipClass().equals("Destroyer")) {
                achievementHandler.unlockAchievement(owner, DESTROYER_DESTROYED.toString());
            } else if (this.getShipClass().equals("Battleship")) {
                achievementHandler.unlockAchievement(owner, TITLE_DROP.toString());
            }
            System.out.println("You sank the enemy "+ this.getShipClass());
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
