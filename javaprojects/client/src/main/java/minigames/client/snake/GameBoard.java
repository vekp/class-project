package minigames.client.snake;

/**
 * The GameBoard class represents a 2D grid game board used in the Snake game.
 * Each cell in the board can contain an item, represented by an ItemType enum.
 * The board is initialized as vacant and can be modified by setting specific ItemTypes at
 * particular coordinates.
 */
public class GameBoard {

    // Instance variables representing the width and height of the board
    private final int width;
    private final int height;

    // 2D array representing the board
    private final ItemType[][] board;

    /**
     * Constructs a new GameBoard object with the specified width and height.
     * Initializes all cells as ItemType.VACANT.
     *
     * @param width  The width of the game board in cells.
     * @param height The height of the game board in cells.
     */
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new ItemType[width][height];
        initBoard();
    }

    /**
     * Initializes the board by setting all cells to ItemType.VACANT.
     */
    public void initBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board[x][y] = ItemType.VACANT;
            }
        }
    }

    /**
     * Retrieves the width of the game board.
     *
     * @return The width of the game board in cells.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the game board.
     *
     * @return The height of the game board in cells.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the ItemType at a specific coordinate on the board.
     *
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return The ItemType at the specified coordinate.
     * @throws IndexOutOfBoundsException if the x or y coordinates are out of the board's bounds.
     */
    public ItemType getItemTypeAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        return board[x][y];
    }

    /**
     * Sets a cell on the board to a specified ItemType.
     *
     * @param x    The x-coordinate of the cell to set.
     * @param y    The y-coordinate of the cell to set.
     * @param type The ItemType to set the cell to.
     * @throws IndexOutOfBoundsException if the x or y coordinates are out of the board's bounds.
     */
    public void setItemTypeAt(int x, int y, ItemType type) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        board[x][y] = type;
    }

    /**
     * Clears all cells of a specific ItemType from the board.
     *
     * @param typeToClear The ItemType to clear from the board.
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
