package minigames.server.battleship;

/**
 *
 */
public class Cell {

    // Fields
    private boolean shotAt;
    CellType cellType;

    // Constructor

    /**
     *
     */
    Cell() {
        this.shotAt = false;
        this.cellType = CellType.OCEAN;
    }

    /**
     *
     * @return
     */
    public String getCellType() {
        return cellType.toString();
    }

    /**
     *
     * @param cellType
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }
}
