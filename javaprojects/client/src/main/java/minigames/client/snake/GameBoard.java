package minigames.client.snake;

class GameBoard {
    private final int width;
    private final int height;
    private final ItemType[][] board;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ItemType getItemTypeAt(int x, int y) {
        return board[x][y];
    }

    public void setItemTypeAt(int x, int y, ItemType type) {
        board[x][y] = type;
    }

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
