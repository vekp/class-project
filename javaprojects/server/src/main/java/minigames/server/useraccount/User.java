package minigames.server.useraccount;

// represents actual user objects
public class User {

    private String username; 
    private String currentGame; 
    private boolean active;
    private int score;
    private HashMap<String, int> gameScores = new HashMap<>();

    /*
     * constructor for User objects
     * @param userName: a String object representing the username of the user.
     */
    public User(String userName) {
        this.username = userName;
        this.active = true;
        this.gameScores = gameScores;
    }

    // getter for the objects username
    public String getUserName() {
        return this.username;
    }

    // getter for the objects current active status (can check if user has chosen to be marked offline)
    public boolean getActiveStatus() {
        return this.active;
    }

    // setter for active status, can be toggled on/off by user potentially?
    public toggleActive() {
        this.active = !this.active;
    }

    // overrides existing high score saved to user account, uses game name Strings as a key.
    public addGameScore(String game, int score) {
        this.gameScores.put(game, score);
    } 
}
