package minigames.server.battleship;

/**
 * The CellType enum represents each of the different types that a cell could possibly be. Note that the "FLASH" type
 * represents the cursor that will flash on and off at the current position of the player's cursor
 */
public enum CellType {
    SHIP_LEFT, SHIP_RIGHT, SHIP_UP, SHIP_DOWN, SHIP_HULL, HIT, MISS, OCEAN, FLASH;

    /**
     * Convert the CellType into a String to be displayed on screen
     * NOTE: We could change this to return a char, as there wouldn't be situations where this should return
     * more than one char - CRAIG
     * @return A String of what to display at the position of the Cell
     */
    @Override
    public String toString() {
        return switch (this) {
            case SHIP_LEFT -> "&#60;"; // html for "<"
            case SHIP_RIGHT -> ">";
            case SHIP_UP -> "^";
            case SHIP_DOWN -> "v";
            case SHIP_HULL -> "0";
            case HIT -> "X";
            case MISS -> ".";
            case OCEAN -> "~";
            case FLASH -> " ";
        };
    }
}
