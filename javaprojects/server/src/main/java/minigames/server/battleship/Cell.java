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

    public boolean hasBeenShot() {return this.shotAt;}

    public int getHorizontalCoord() {
        return horizontalCoord;
    }

    public int getVerticalCoordInt() {
        return verticalCoord;
    }

    public String getVerticalCoordString() {
        String chars = "ABCDEFGHIJ";
        return String.valueOf(chars.charAt(verticalCoord));
    }

    public String getBothCoords(){
        String returnString = ""+this.getVerticalCoordString()+this.getHorizontalCoord();
        System.out.println(returnString);
        return returnString;
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

    public void setHorizontalCoord(int horizontalCoord) {
        this.horizontalCoord = horizontalCoord;
    }

    public void setVerticalCoord(int verticalCoord) {
        this.verticalCoord = verticalCoord;
    }

    public void shoot(){this.shotAt = true;}
}
