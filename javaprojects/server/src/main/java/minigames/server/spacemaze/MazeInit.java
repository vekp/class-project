package minigames.server.spacemaze;

import java.util.*;
import java.io.*;
import java.awt.Point;



/*
 * MazeInit class selects a maze appropriate for the level, initialises the maze and 
 * inputs elements such as startLocations, keyLocations, pickups (traps), ready to be 
 * to MazeControl.
 * 
 * @author Natasha Hay
 * 
 */
public class MazeInit {
    // Maze info
    //private int mazeWidth = 25;                  // maze dimension: width (hard coded for now)
    //private int mazeHeight = 25;                 // maze dimension: height (hard coded for now)
    //private Point exitLocation;                // location of maze exit
    //private int numStartLocs;                    // number of start locations
    //private List<Point> startLocations = new ArrayList<Point>();    // locations of maze entrances
    //private int numKeyLocs;                      // number of key locations
    //private List<Point> keyLocations = new ArrayList<Point>();      // locations of keys
    //private int numPickUpLocs;                   // number of pickUp locations
    //private List<Point> pickUpLocations = new ArrayList<Point>();   // locations of pickups (traps etc)
    
    //private int numBotsLocs;                    // locations of bots
    //private int numTrapHoles;                   // locations of traps
    //private int numBonusPointsLocs;             // locations of bonus points chests
    //private int numBombLocs;                    // 



    // Maze legend
    private HashMap<String, Character> mazeLegend;

    private char[][] mazeArray;                                     // array of maze with all locations included
   

    // Constructor - Select maze and read in from resources 
    /*
     * Constructor - 
     * @param level - takes an int to identify the level of maze to construct
     */
    public MazeInit(int level) 
    {
        // Set mazeLegend
        this.mazeLegend = new HashMap<String, Character>();
        mazeLegend.put("Wall", 'W');        
        mazeLegend.put("Start", 'S');
        mazeLegend.put("Key", 'K');
        mazeLegend.put("LockedExit", 'E');
        mazeLegend.put("Player", 'P');
        mazeLegend.put("Path", '.');
        mazeLegend.put("Bot", 'B');
        mazeLegend.put("UnlockedExit", 'U');
        mazeLegend.put("PickUps", '?');
        mazeLegend.put("Wormhole", 'H');
        mazeLegend.put("Timewarp", 'T');
        mazeLegend.put("BonusPoints", '$');
        mazeLegend.put("Bomb", 'M');

        // Create maze depending on current level
        //mazeRules(level);
        // Read mazeArray in for current level
        //this.mazeArray = readMazeCSV(level);

        // Set mazeArray depending on level
        setMazeInitArray(level);

        // Set maze components - randomly place keys, bots pickups
        // setMazeComponents(level);
    
    }


    /* 
     * getMazeLedgend()
     * @return mazeLegend - HashMap
     */
    public HashMap<String, Character> getMazeLedgend()
    {
        return mazeLegend;
    }

    
    /*
     * mazeRules - make rules depending on level
     */
    /*
    public void mazeRules(int level)
    {
        // Keys required = same as level number
        // One exit location
        // Two start locations
        this.numStartLocs = 1;      // number of start location is same for all levels
        switch (level)
        {
            case 1:
                // start = 2, keys = 3, pickUps = 2, bots = 1;
                this.numKeyLocs = 3;
                this.numPickUpLocs = 2;
                this.numBotsLocs = 1;
                this.numTrapHoles = 2;
                this.numBonusPointsLocs = 2;
                this.numBombLocs = 2;
                break;
            case 2:
                // start = 2, keys = 4, pickUps = 3, bots = 2
                this.numKeyLocs = 3;
                this.numPickUpLocs = 2;
                this.numBotsLocs = 2;
                this.numTrapHoles = 4;
                this.numBonusPointsLocs = 3;
                this.numBombLocs = 2;
                break;
            case 3:
                // start = 2, keys = 2, pickUps = 4, bots = 3
                this.numKeyLocs = 3;
                this.numPickUpLocs = 4;
                this.numBotsLocs = 3;
                this.numTrapHoles = 6;
                this.numBonusPointsLocs = 4;
                this.numBombLocs = 2;
                break;
            default:
                throw new IllegalArgumentException(("Level " + level + " does not exist"));
        }
    }
    */


    /*
     * setMazeInitArray - sets maze depending on level
     * @param level - level of maze 
     * @return mazeArray = char[][] or required maze
     */
    public void setMazeInitArray(int level) 
    {
        // Set mazeArray depending on level passed in
        switch (level)
        {
            case 1:
                this.mazeArray = mazeArray1;
                break;
            case 2:
                this.mazeArray = mazeArray2;
                break;
            case 3:
                this.mazeArray = mazeArray3;
                break;
            default:
                throw new IllegalArgumentException(("Level " + level + " does not exist"));
        }
        
    }


    /*
     * validPlacement function - checks Point(x, y) location is not a wall and is 
     * within maze array bounds, and is not taken by another element
     * @param posPoint - Point(x, y) location of possible move/placement
     * @return bool - boolean value -> false = not valid, true = valid
     */
    public Boolean validPlacement(Point posPoint)
    {
        // If (x, y) == '.' -> valid placement(true)
        if (mazeArray[posPoint.y][posPoint.x] == '.')
        {
            return true;
        }
        // Else all other (x, y) are walls/starts/exit/keys/bots... or OOB
        else
        {
            return false;
        }
    }


    /*
     * getMazeInitArray() - returns mazeArray
     * @return mazeArray
     */
    public char[][] getMazeInitArray()
    {
        return mazeArray;
    }


    // Tried to pass mazes in as csv - could not test properly
    // Use hard coded mazeArrays until MazeArrayAlgorithm is created
    
    
    // HARD CODED MAZES FOR NOW
    /*
     * MazeArray - hard coded for now as a private char[][]
     */
    
     private char[][] mazeArray1 = {
        {'W','S','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W'},
        {'W','.','.','.','.','.','.','.','.','.','.','.','.','H','W','.','.','.','W','.','.','.','.','.','W'},
        {'W','W','W','W','W','W','W','W','W','.','W','.','W','W','W','W','W','.','W','W','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','.','W','.','W','.','.','.','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','.','W','W','W','W','.','.','W','.','W','W','W','W','.','.','W','W','W','.','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','.','.','.','W','.','.','.','.','B','W','K','W','.','.','.','.','.','W'},
        {'W','W','W','.','.','W','W','W','W','.','W','.','.','W','W','W','W','.','W','W','W','W','W','W','W'},
        {'W','K','.','.','.','W','$','.','.','.','W','.','.','.','.','.','W','.','W','.','.','.','.','.','W'},
        {'W','W','W','.','.','W','W','W','W','.','W','.','.','W','W','.','W','.','W','.','W','W','W','.','W'},
        {'W','.','W','.','.','.','.','.','W','.','W','.','M','W','.','.','.','.','.','.','W','.','W','.','W'},
        {'W','.','W','W','W','W','.','.','W','.','W','W','W','W','W','W','W','W','W','W','W','.','W','.','W'},
        {'W','.','.','.','.','.','.','W','W','.','W','.','.','.','.','.','W','.','.','.','.','.','W','.','W'},
        {'W','W','W','W','.','W','.','.','W','W','W','.','.','W','.','.','W','.','W','.','W','W','W','.','W'},
        {'W','.','.','.','.','W','.','.','.','.','.','.','W','W','.','.','.','.','.','.','.','.','$','.','W'},
        {'W','.','W','.','.','W','W','W','W','W','W','.','.','W','W','W','W','.','W','.','W','W','W','W','W'},
        {'W','.','.','.','.','.','.','W','W','M','.','.','.','.','.','.','W','.','W','.','.','.','.','.','W'},
        {'W','W','W','W','.','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','.','W'},
        {'W','.','W','.','.','W','.','K','W','.','.','.','.','.','.','.','W','$','W','.','.','.','.','.','W'},
        {'W','.','W','.','W','W','.','W','W','W','W','.','.','W','W','W','W','.','W','.','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','W'},
        {'W','W','W','W','.','W','W','W','W','.','W','W','W','W','.','.','W','W','W','.','W','W','W','W','W'},
        {'W','.','.','.','.','.','.','.','W','.','W','.','.','.','.','.','.','.','M','.','W','.','.','.','W'},
        {'W','W','.','W','W','W','W','W','W','.','W','W','W','W','W','W','W','.','W','.','W','.','W','W','W'},
        {'W','.','.','.','.','.','.','.','.','.','H','.','.','.','.','.','.','.','W','.','.','.','.','.','W'},
        {'W','W','W','W','W','W','W','W','W','W','W','E','W','W','W','W','W','W','W','W','W','W','W','W','W'},
        };

    private char[][] mazeArray2 = {
        {'W','W','W','W','W','W','W','W','W','W','W','S','W','W','W','W','W','W','W','W','W','W','W','W','W'},
        {'W','.','.','.','.','.','W','.','.','.','.','.','W','.','.','.','.','.','W','.','.','.','.','.','W'},
        {'W','.','W','W','.','.','.','.','.','W','.','.','W','.','.','W','.','.','.','.','W','W','W','.','W'},
        {'W','.','$','W','.','.','W','.','H','W','.','.','.','.','.','W','.','K','W','.','.','W','.','.','W'},
        {'W','W','W','W','.','.','W','.','.','W','.','.','W','W','W','W','W','W','W','.','.','W','.','.','W'},
        {'W','.','.','.','.','.','W','.','.','.','.','.','W','.','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','.','.','W','W','W','W','W','W','W','.','.','W','.','.','W','W','W','W','W','W','W','.','W','W'},
        {'W','W','.','W','K','.','.','.','.','W','W','.','.','.','.','W','.','.','.','.','.','W','B','.','W'},
        {'W','.','.','W','W','W','W','.','.','W','.','.','W','W','W','W','.','.','W','.','.','W','.','.','W'},
        {'W','.','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','M','W','.','.','.','.','.','W'},
        {'W','W','W','W','W','W','W','.','.','W','W','W','W','H','.','W','W','W','W','W','W','W','.','.','W'},
        {'W','.','.','.','.','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','.','W','.','.','W'},
        {'W','.','.','W','W','W','W','W','W','W','.','.','W','.','.','W','.','.','W','.','.','W','.','W','W'},
        {'W','.','.','.','.','.','.','.','$','W','.','.','.','.','.','W','.','.','W','.','.','.','.','.','W'},
        {'W','W','W','W','.','.','W','W','W','W','.','.','W','W','W','W','W','.','W','W','W','W','.','W','W'},
        {'W','B','.','.','.','.','W','K','.','.','.','.','.','.','.','.','.','.','W','$','.','W','.','.','W'},
        {'W','.','.','W','.','.','W','W','M','W','W','W','W','W','W','W','W','.','W','W','.','.','.','W','W'},
        {'W','.','.','W','.','.','.','.','.','.','.','.','.','.','.','K','W','.','.','.','.','W','.','.','W'},
        {'W','.','.','W','W','W','W','H','.','W','.','.','W','W','W','W','W','W','W','W','W','W','.','.','W'},
        {'W','.','.','W','.','.','W','.','.','W','.','.','W','.','.','W','.','.','.','.','.','.','.','.','W'},
        {'W','.','.','.','.','.','.','.','.','W','.','.','.','.','.','W','.','.','.','.','W','W','.','.','W'},
        {'W','W','W','.','.','W','W','W','W','W','W','W','W','.','H','W','W','W','W','.','.','W','W','W','W'},
        {'W','.','.','.','.','.','.','.','W','.','.','.','.','.','.','.','M','.','.','.','.','W','K','.','W'},
        {'W','.','.','.','.','W','.','.','.','.','.','.','W','.','.','.','W','.','W','.','.','.','.','.','W'},
        {'W','W','W','E','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W'}
        };

    private char[][] mazeArray3 = {
        {'W','W','W','S','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W'},
        {'W','.','.','.','.','.','H','W','.','.','W','.','.','.','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','.','W','W','.','W','W','W','.','.','W','.','W','W','W','W','W','W','.','W','W','W','W','W','W'},
        {'W','.','W','.','.','.','.','.','.','.','.','.','.','.','.','$','W','.','.','W','.','.','W','.','W'},
        {'W','.','W','.','W','W','.','W','W','W','W','W','W','W','W','W','W','.','H','W','.','.','W','.','W'},
        {'W','.','W','.','.','W','.','.','.','.','.','.','.','.','W','M','.','.','.','.','.','.','.','.','W'},
        {'W','.','W','.','.','W','.','W','.','.','W','W','W','.','W','W','W','W','W','W','.','.','W','.','W'},
        {'W','.','W','.','K','W','M','W','.','.','.','K','W','.','.','.','.','.','M','W','K','.','W','.','W'},
        {'W','.','W','W','W','W','W','W','W','W','W','W','W','W','W','.','W','W','W','W','W','W','W','.','W'},
        {'W','B','.','.','.','.','.','W','.','.','.','.','W','.','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','W','W','W','W','W','.','W','.','.','W','.','W','.','W','.','W','W','W','W','.','.','W','.','W'},
        {'W','$','W','.','B','W','.','.','.','.','W','.','.','.','W','.','W','.','.','.','.','.','W','.','W'},
        {'W','.','W','.','.','W','W','W','W','W','W','.','W','.','W','W','W','W','W','W','.','.','W','W','W'},
        {'W','.','.','.','.','W','.','.','.','.','.','.','W','.','.','.','.','.','$','W','.','.','.','.','W'},
        {'W','W','W','H','.','W','.','W','W','W','W','.','W','W','W','W','W','W','W','W','W','.','H','W','W'},
        {'W','.','.','.','.','.','.','W','.','.','W','.','.','.','.','.','.','.','.','.','.','.','.','.','W'},
        {'W','.','W','W','W','W','W','W','.','.','W','W','W','.','H','.','W','W','W','W','W','W','W','.','W'},
        {'W','.','W','.','.','W','.','.','.','.','.','.','.','.','W','.','W','B','.','W','M','.','.','.','W'},
        {'W','.','W','.','.','W','.','W','W','W','W','.','W','W','W','.','W','.','.','W','W','W','W','.','W'},
        {'W','.','W','.','.','.','.','W','.','.','.','.','W','$','W','.','W','.','.','.','.','.','.','.','W'},
        {'W','.','W','.','.','W','W','W','.','.','W','.','W','.','W','.','W','.','.','W','W','W','W','.','W'},
        {'W','.','.','.','.','.','.','W','$','.','W','.','.','.','W','.','W','.','.','.','K','.','W','.','W'},
        {'W','.','W','W','W','W','W','W','W','W','W','.','W','W','W','.','W','W','W','W','.','.','W','.','W'},
        {'W','.','.','.','K','W','.','.','.','.','.','.','.','.','W','.','W','H','.','.','.','.','.','.','W'},
        {'W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','W','E','W'}
    };
}
