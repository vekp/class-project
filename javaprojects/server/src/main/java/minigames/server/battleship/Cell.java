package minigames.server.battleship;

/**
 * The cell represents each cell of the board grid, and can either be "shotAt" (has been targetted in a previous round),
 * or not.
 * Each cell is also of a certain type (CellType enum) to represent whether it is ocean, ship, player cursor, etc.
 */
public class Cell {

    // Fields
    private boolean shotAt;
    private CellType cellType;
    private int horizontalCoord;
    private int verticalCoord;

    // Constructor

    /**
     * Default constructor, each Cell is initialised as an OCEAN Cell and is changed as needed
     */
    public Cell(int horizontalCoord, int verticalCoord) {
        this.horizontalCoord = horizontalCoord;
        this.verticalCoord = verticalCoord;
        this.shotAt = false;
        this.cellType = CellType.OCEAN;
    }

    // Getters

    /**
     * Return the CellType of the current Cell
     * @return an enum value
     */
    public CellType getCellType() {
        return cellType;
    }

    /**
     * Return the CellType of the current Cell
     * @return A String representing the CellType
     */
    public String getCellTypeString() {
        return cellType.toString();
    }

    /**
     * Return Boolean value of shotAt
     * @return boolean representing if the cell has been shot
     */
    public boolean hasBeenShot() {return this.shotAt;}

    /**
     * Return the horizontal index of the cell
     * @return int representing the horizontal index
     */
    public int getHorizontalCoord() {
        return horizontalCoord;
    }

    /**
     * Return the vertical index of the cell
     * @return int representing the vertical index
     */
    public int getVerticalCoordInt() {
        return verticalCoord;
    }

    /**
     * Return vertical index as the corresponding grid character
     * @return String representing the vertical location character
     */
    public String getVerticalCoordString() {
        String chars = "ABCDEFGHIJ";
        return String.valueOf(chars.charAt(verticalCoord));
    }

    /**
     * Return both coordinates in the form 'character+integer' -> A0
     * @return String representing grid coordinate
     */
    public String getBothCoords(){
        return ""+this.getVerticalCoordString()+this.getHorizontalCoord();
    }

    // Setters

    /**
     * Change the CellType of the current Cell
     * @param cellType The CellType to change the current cell to
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    /**
     * Set the horizontal coordinate
     * @param horizontalCoord int value representing horizontal index
     */
    public void setHorizontalCoord(int horizontalCoord) {
        this.horizontalCoord = horizontalCoord;
    }

    /**
     * Set the vertical coordinate
     * @param verticalCoord int value representing vertical index
     */
    public void setVerticalCoord(int verticalCoord) {
        this.verticalCoord = verticalCoord;
    }

    /**
     * Set CellType to HIT/MISS depending on what is in it, and set shotAt to true
     */
    public void shoot() {
        if (this.hasBeenShot()) return;
        if (getCellType().equals(CellType.OCEAN)) setCellType(CellType.MISS);
        else setCellType(CellType.HIT);
        this.shotAt = true;
    }
}
