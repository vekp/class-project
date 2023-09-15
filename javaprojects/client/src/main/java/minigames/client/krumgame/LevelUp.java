package minigames.client.krumgame;

/**
 * This class will be used in the Game class 
 * for username, xp and level. This class communicates 
 * with the MYSQLconnector class to connect with the 
 * database and retrieve or update the xp. 
 */

public class LevelUp {

    private int xp;
    private int level;
    private String username; 
    /**
     * Constructor to initialize the username, xp and level.
     */
    public LevelUp(String username) {
        this.username = username;
        loadXp();
        calculateLevel();
    }
    /**
     * This method will load xp.
     */
    private void loadXp() {
        this.xp = MySQLConnector.getXpForUser(username);
    }

    /**
     * This method will update xp for the player
     */
    public void updateXp(int newXp) {
        MySQLConnector.updateXpForUser(username, newXp);
        calculateLevel();
    }

    private int calculateLevel() {
        if (xp > 500) {
            this.level = 5;
        } else {
            this.level = xp / 100;
        }
        return this.level;
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
    }
}
