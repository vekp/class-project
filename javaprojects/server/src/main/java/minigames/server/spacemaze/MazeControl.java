package minigames.server.spacemaze;

import java.util.*;
import java.util.HashMap;
import java.awt.Point;

/**
 * MazeControl class to verifies and controls player input,
 * controls maze elements (such as keys) and returns an updated
 * maze array for display to screen.
 * 
 * @author Natasha Hay
 * 
 */

public class MazeControl {
        
    // Map info -- hard coded for now
    private int mazeWidth = 25;
    private int mazeHeight = 25;
    //private char[][] mazeArray = new char[mazeWidth][mazeHeight];
    // mazeArray at bottom of file for now
    
    // Player info
    private Point playerLocation;

    // Exit info
    private Point exitLocation;
    private Boolean exitUnlocked = false;

    // Key info
    private int numKeysToUnlock = 1;
    private Point[] keyLocations;
    private HashMap<Point, Boolean> keyStatus = new HashMap<Point, Boolean>();

    // Gameover info
    private Boolean gameFinished = false;
    // winner variable???

    // Constructor - sets fields (update to take player???)
    public MazeControl() 
    {
        // Player (start) location - updated during gameplay
        // this could be separated into start location and current location
        //this.playerLocation = new Point(player.getLocation());       // could be a getOrElse(startlocation)
        this.playerLocation = new Point(1, 0);
        // Exit location
        this.exitLocation = new Point(24, 23);

        // Key locations: (x, y) - Hard coded atm
        Point[] keyLocations = 
        {
            new Point(17, 5),
            new Point(7, 17)
        };
        setKeyStatus(keyLocations);
        //this.keyStatus.put(new Point(17, 5), false);
        //this.keyStatus.put(new Point(7, 17), false);
        
    }

    /* 
    * setKeysStatus function - inputs keyLocations into a map and defaults collected bool value to false
    * @param keyLocations - Point object array of key locations in mazeArray
    */
    private void setKeyStatus(Point[] keyLocations)
    {
        // Key Status: (x, y)-> False
        for (int i = 0; i < keyLocations.length; i++)
        {
            this.keyStatus.put(keyLocations[i], false);
        }
    }

    /*
     * getKeyStatus function - checks keyStatus
     * @return keyStatus - returns hashmap of keys and their collected status
     */
    public HashMap<Point, Boolean> getKeyStatus()
    {
        return keyStatus;
    }

    /* 
    * updateKeyStatus function - updates keyStatus if player's location is at key's location
    * @param player - SpacePlayer to update key status
    * @param playerLoc - Point object of player's location
    */
    public void updateKeyStatus(SpacePlayer player, Point playerLoc)
    {
        if (keyStatus.containsKey(playerLoc))
        {
            keyStatus.put(playerLoc, true); 
            player.addKey();   
        }
    }  

    /*
    * unlockExit function - unlocks exit if player has correct number of keys 
    * @param player - SpacePlayer to unlock exit
    * 
    * Can update function to require player to be near exit before unlocking
    */   
    public void unlockExit(SpacePlayer player)
    {
        // Check player has correct number of keys to unlock exit
        if (player.checkNumberOfKeys() >= numKeysToUnlock)
        {
            exitUnlocked = true;
            // NB - array[row=y][col=x]
            mazeArray[exitLocation.y][exitLocation.x] ='U';
        }
    }

    // bypassUnlockExit function - dev tool to check validMove - delete 
    public void bypassUnlockExit(Boolean status) {
        exitUnlocked = status;
    }
    
    /*
     * getExitUnLockedStatus function - returns bool value -> unlocked == true
     * @return exitUnlocked - bool
     */
    public Boolean getExitUnLockedStatus()
    {
        return exitUnlocked;
    }

    /* 
    * gameOver function - checks if player is at exit - called in updatePlayerLocationMaze
    */
    public void checkGameOver()
    {
        if (playerLocation == exitLocation)
        {
            gameFinished = true;
            // Could also have a print message of game over

        }
    }

    /* 
    * validMove function - checks if potential move coord is not a wall or locked exit
    * @param posMove - Point object of possible (player) move
    */
    public Boolean validMove(Point posMove)
    {
        // Check if (x, y) is a wall 
        // NB - array[row=y][col=x]
        if (mazeArray[posMove.y][posMove.x] == 'W')
        {
           return false;
        }
        // Check if (x, y) are out of bounds (< 0, or > mazeWidth/mazeHeight)
        // ** negative/greater than inputs throw ArrayIndexOutOfBoundsException
        else if ((posMove.x < 0) || (posMove.y < 0) 
            || (posMove.x > mazeWidth) || (posMove.y > mazeHeight))
        {
            return false;
        }
        // Check if posMove is the exit
        else if (posMove.equals(exitLocation))
        {
            if (exitUnlocked == true)
                {
                    return true;
                }
                else 
                {
                    return false;
                    // Could print out a message about requiring a key
                }
        }
        else 
        {
            return true;
        }
    }

    /* 
    * getPlayerLocationInMaze function - returns player's current location
    * @param player - SpacePlayer to return location for
    */
    public Point getPlayerLocationInMaze(SpacePlayer player)
    {
        return playerLocation;
    }

    /*
    * updatePlayerLocationMaze function - takes a SpacePlayer and Point(x, y) coords updates playerLocation and mazeArray,
    *              and updates keyStatus and checks if game is over. 
    * @param player - SpacePlayer to update Maze for
    * @param newMove - player's new move to update maze with
    */ 
    public void updatePlayerLocationMaze(SpacePlayer player, Point newMove)
    {
        // Assume move is valid --  should have been checked via SpaceMazeGame
        
        // Set previous player location (temp object)
        Point prevMove = new Point(playerLocation);
        // Update playerLocation in mazeArray to newMove
        //playerLocation = new Point(x, y);
        playerLocation = new Point(newMove);
        // Set (x, y) to 'P'
        // NB - array[row = y][col = x]
        //mazeArray[x][y] = 'P';
        mazeArray[newMove.y][newMove.x] = 'P';
        // Set previous player location to '.'
        mazeArray[prevMove.y][prevMove.x]= '.';
        
        // Check if player location picks up a key
        updateKeyStatus(player, playerLocation);

        // Check if game is over
        checkGameOver();
    }

    /*
     * getMazeArray function - returns 2D char array
     * @return mazeArray - char[][]
     */ 
    public char[][] getMazeArray()
    {
        return mazeArray;
    }

    /*
     * MazeArray - hard coded for now as a private char[][]
     */
    
    private char[][] mazeArray = {
        {'W','P','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W'},
        {'W','.','.','.','.','.','.','.','.','.','.','.','.','.','W','.','.','.','W','.','.','.','.','.','W'},
        {'W','W','W','W','W','W','W','W','W','.','W','.','W','W','W','W','W','.','W','W','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','.','W','.','W','.','.','.','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','.','W','W','W','W','.','.','W','.','W','W','W','W','.','.','W','W','W','.','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','.','.','.','W','.','.','.','.','.','W','K','W','.','.','.','.','.','W'},
        {'W','W','W','.','.','W','W','W','W','.','W','.','.','W','W','W','W','.','W','W','W','W','W','W','W'},
        {'W','.','.','.','.','W','.','.','.','.','W','.','.','.','.','.','W','.','W','.','.','.','.','.','W'},
        {'W','W','W','.','.','W','W','W','W','.','W','.','.','W','.','.','W','.','W','.','W','W','W','.','W'},
        {'W','.','W','.','.','.','.','.','W','.','.','.','.','W','.','.','.','.','.','.','W','.','W','.','W'},
        {'W','.','W','W','W','W','.','.','W','.','W','W','W','W','W','W','W','W','W','W','W','.','W','.','W'},
        {'W','.','.','.','.','.','.','W','W','.','W','.','.','.','.','.','W','.','.','.','.','.','W','.','W'},
        {'W','W','W','W','.','W','.','.','W','W','W','.','.','W','.','.','W','.','W','.','W','W','W','.','W'},
        {'W','.','.','.','.','W','.','.','.','.','.','.','W','W','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','.','W','.','.','W','W','W','W','W','W','.','.','W','W','W','W','.','W','.','W','W','W','W','W'},
        {'W','.','.','.','.','.','.','W','W','.','.','.','.','.','.','.','W','.','W','.','.','.','.','.','W'},
        {'W','W','W','W','.','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','.','W'},
        {'W','.','W','.','.','W','.','K','W','.','.','.','.','.','.','.','W','.','W','.','.','.','.','.','W'},
        {'W','.','W','.','W','W','.','W','W','W','W','.','.','W','W','W','W','.','W','.','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','W'},
        {'W','W','W','W','.','W','W','W','W','.','W','W','W','W','.','.','W','W','W','.','W','W','W','W','W'},
        {'W','.','.','.','.','.','.','.','W','.','W','.','.','.','.','.','.','.','.','.','W','.','.','.','W'},
        {'W','W','.','W','W','W','W','W','W','.','W','W','W','W','W','W','W','.','W','.','W','.','W','W','W'},
        {'W','.','.','.','.','.','.','.','.','.','.','.','.','W','.','.','.','.','W','.','.','.','.','.','E'},
        {'W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W'},
        };


}