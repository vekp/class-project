package minigames.server.battleship;

/**
 *
 */
public enum CellType {
    SHIP_LEFT, SHIP_RIGHT, SHIP_UP, SHIP_DOWN, SHIP_HULL, HIT, MISS, OCEAN, FLASH;

    @Override
    public String toString() {
        return switch (this) {
            case SHIP_LEFT -> "<";
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
