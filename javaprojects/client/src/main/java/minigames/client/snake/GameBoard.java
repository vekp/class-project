package minigames.client.snake;

/**
 * The GameBoard class represents a 2D grid game board.
 * Each cell in the board can contain an item represented by ItemType.
 * The board is initialized as vacant and can be modified by setting specific ItemTypes at
 * coordinates.
 */

class GameBoard {
    private final int width;
    private final int height;
    private final ItemType[][] board;

    /**
     * Constructs a new GameBoard with the given dimensions.
     * Initializes all cells as ItemType.VACANT.
     *
     * @param width  the width of the game board
     * @param height the height of the game board
     */

    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        board = new ItemType[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board[x][y] = ItemType.VACANT; // Initialize board to be empty
            }
        }
    }

    /**
     * Returns the width of the game board.
     *
     * @return the width of the game board
     */

    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the game board.
     *
     * @return the height of the game board
     */

    public int getHeight() {
        return height;
    }

    /**
     * Returns the ItemType at a specific coordinate on the game board.
     * Throws IndexOutOfBoundsException if the coordinates are out of bounds.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the ItemType at the given coordinate
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */

    public ItemType getItemTypeAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        return board[x][y];
    }

    /**
     * Sets the ItemType at a specific coordinate on the game board.
     * Throws IndexOutOfBoundsException if the coordinates are out of bounds.
     *
     * @param x    the x-coordinate
     * @param y    the y-coordinate
     * @param type the ItemType to set
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */

    public void setItemTypeAt(int x, int y, ItemType type) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        board[x][y] = type;
    }

    /**
     * Clears all cells of a specific ItemType on the game board.
     *
     * @param typeToClear the ItemType to clear
     */

    public void clearTilesOfType(ItemType typeToClear) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (board[x][y] == typeToClear) {
                    board[x][y] = ItemType.VACANT;
                }
            }
        }
    }

}
