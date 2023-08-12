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
        
    // Map info
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

    // setKeysStatus functions - inputs keyLocations into a map and defaults collected bool value to false
    public void setKeyStatus(Point[] keyLocations)
    {
        // Key Status: (x, y)-> False
        for (int i = 0; i < keyLocations.length; i++)
        {
            keyStatus.put(keyLocations[i], false);
        }
    }

    // updateKeyStatus function - updates keyStatus
    public void updateKeyStatus(Point keyLoc, Boolean collected)
    {
        if (keyStatus.containsKey(keyLoc))
        {
            keyStatus.put(keyLoc, collected);    
        }
    }  
    
    // unlockExit function - unlocks exit if player has correct number of keys
    //   ** Can update function to require player to be near exit before unlocking
    public void unlockExit(SpacePlayer player)
    {
        // Check player has correct number of keys to unlock exit
        if (player.CheckNumberOfKeys() == numKeysToUnlock)
        {
            exitUnlocked = true;
        }
    }

    // gameOver function - checks if player is at exit
    public void checkGameOver()
    {
        if (playerLocation == exitLocation)
        {
            gameFinished = true;
            // Could also have a print message of game over
        }
    }

    // validMove function - checks if potential move coord is not a wall or locked exit
    public Boolean validMove(int x, int y)
    {
        Point posMove = new Point(x, y);    // temporarily set (x, y) to Point object
        // Check if (x, y) is a wall
        if (mazeArray[x][y] == 'W')
        {
           return false;
        }
        else
        {
            // Check if (x, y) is the exit
            if (mazeArray[x][y] == 'E' || posMove.equals(exitLocation))
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

    // getPlayerLocation function - returns player's current location
    // ??? return Point object or int[][]

    // updatePlayerLocation function - takes (x, y) coords, checks if move is valid, then
    //                                 updates playerLocation and mazeArray
    public void updatePlayerLocation(int x, int y)
    {
        // Check if move is valid
        if (validMove(x, y))
        {
            // Set previous player location (temp object)
            Point prevMove = new Point(playerLocation);
            // Update playerLocation
            playerLocation = new Point(x, y);
            // Set (x, y) to 'P'
            mazeArray[x][y] = 'P';
            // Set previous player location to '.'
            mazeArray[prevMove.x][prevMove.y] = '.';
        }
        // Check if game is over
        checkGameOver();
    }

    // getMazeArray function - returns 2D char array
    public char[][] getMazeArray()
    {
        return mazeArray;
    }

    // setMazeArray - hard coded for now
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