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

    // Constructor

    /**
     * Default constructor, each Cell is initialised as an OCEAN Cell and is changed as needed
     */
    public Cell() {
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

    // Setters
    /**
     * Change the CellType of the current Cell
     * @param cellType The CellType to change the current cell to
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
        this.shotAt = true;
    }
}
