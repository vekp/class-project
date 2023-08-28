package minigames.server.telepathy;

/**
 * Stores player information amd state for use in Telepathy Minigame.
 */
public class Player {
    private String name;
    private Board board;

    /**
     * Construct a new player with a new board state.
     * 
     * Board state is random however the current construction uses the same seed for each 
     * board generated - therefore every board will start with the same layout of symbols/colours.
     * 
     * @param name String with the name of the Player to create.
     */
    public Player(String name) {
        this.name = name;
        this.board = new Board();
    }

    /**
     * Getter for the Player's name.
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get a reference to this Player's board state.
     * 
     * @return Board object representing this Player's board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Get a String that can represent this Player. Uses their name.
     */
    public String toString() {
        return this.name;
    }
}

