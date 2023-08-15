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
    private HashMap<Point, Boolean> keyStatus;

    // Gameover info
    private Boolean gameFinished = false;
    // winner variable???

    // Constructor - sets fields (update to take player???)
    public MazeControl() 
    {
        // Player (start) location - updated during gameplay
        // this could be separated into start location and current location
        playerLocation = new Point(1, 0);
        
        // Exit location
        exitLocation = new Point(24, 23);

        // Key locations: (x, y) - Hard coded atm
        Point[] keyLocations = 
        {
            new Point(17, 5),
            new Point(7, 17)
        };
        setKeyStatus(keyLocations);
        
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
            keyStatus.put(keyLocations[i], false);
        }
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
        if (player.checkNumberOfKeys() == numKeysToUnlock)
        {
            exitUnlocked = true;
            mazeArray[exitLocation.x][exitLocation.y] ='U';
        }
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
        //Point posMove = new Point(x, y);    // temporarily set (x, y) to Point object
        // Check if (x, y) is a wall, or if (x, y) are out of bounds (< 0, or > mazeWidth/mazeHeight)
        if (mazeArray[posMove.x][posMove.y] == 'W' || (posMove.x < 0) || (posMove.y < 0) 
            || (posMove.x > mazeWidth) || (posMove.y > mazeHeight))
        {
           return false;
        }
        else
        {
            // Check if posMove is the exit
            if (posMove.equals(exitLocation))
            {
                // Check if exit is unlocked
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
                return false;
            }
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
        //mazeArray[x][y] = 'P';
        mazeArray[newMove.x][newMove.y] = 'P';
        // Set previous player location to '.'
        mazeArray[prevMove.x][prevMove.y] = '.';
        
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