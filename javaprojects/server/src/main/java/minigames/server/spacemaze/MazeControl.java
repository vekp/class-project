package minigames.server.spacemaze;

import java.util.*;
import java.awt.Point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MazeControl class to verifies and controls player input,
 * controls maze elements (such as keys) and returns an updated
 * maze array for display to screen.
 * 
 * @author Natasha Hay
 * 
 */

public class MazeControl {

    private static final Logger logger = LogManager.getLogger(MazeControl.class);
        
    // Maze info -- hard coded for now
    private int mazeWidth = 25;
    private int mazeHeight = 25;
    private int currentLevel;               // level of current maze
    private int maxLevel = 3;               // maximum number of levels
    //private char[][] mazeArray = new char[mazeWidth][mazeHeight];  
    private char[][] mazeArray;

    // MazeTimer
    public GameTimer mazeTimer = new GameTimer();
    public int timeTaken;

    // Player info
    SpacePlayer mazePlayer;
    private Point playerLocation;
    private List<Point> playerPrevLocationList = new ArrayList<Point>();

    // Start info - locations of maze entrances
    //private List<Point> startLocationsList = new ArrayList<Point>();  
    private Point startLocation;
    //private Point[] startLocations;  

    // Exit info - location of maze exit
    private Point exitLocation;
    private Boolean exitUnlocked = false;

    // Key info
    private int numKeysToUnlock;
    private List<Point> keyLocationsList = new ArrayList<Point>();
    //private Point[] keyLocations;
    private HashMap<Point, Boolean> keyStatus = new HashMap<Point, Boolean>();

    // PickUps info - locations of Pickups in maze
    private List<Point> pickUpLocationsList = new ArrayList<Point>(); 
    //private Point[] pickUpLocations;

    // Traps info
    private List<Point> wormholeLocationsList = new ArrayList<Point>();
    private List<Point> bombLocationsList = new ArrayList<Point>();
    // Bots info
    private List<Point> botsLocationsList = new ArrayList<Point>();

    // Bonus Points info
    private List<Point> bonusPointsLocationsList = new ArrayList<Point>();

    // Gameover info
    public Boolean gameFinished = false;
    // winner variable???


    
    // MazeControl - set player
    public MazeControl(SpacePlayer player)
    {
        // Set/link player;
        this.mazePlayer = player;
        // Start maze on level 1
        this.currentLevel = 1;   
        // Set number of keys to unlock exit (level num)
        this.numKeysToUnlock = currentLevel;
        // Create new Maze, dependent on current level
        this.mazeArray = createNewMaze(currentLevel);

        // Set start, key... locations
        set_locations();

        // Exit location
        this.exitLocation = getExitLocation();

        // Initalise keyStatus with collected = false
        setKeyStatus(keyLocationsList);

        
        // Timer starts (here for now)
        //this.mazeTimer = new GameTimer();
    }

    /*
     * createNewMaze function - creates a new maze for each level
     * @param level - level for maze creation
     * @return mazeArray
     */
    public char[][] createNewMaze(int level)
    {
        MazeInit newMaze = new MazeInit(currentLevel);
        return newMaze.getMazeInitArray();
    }

 
    /*
     * playerEntersMaze function - populates maze with player and starts timer
     * controlled by SpaceMazeGame
     * @param player - SpacePlayer 
    */
    public void playerEntersMaze(Point playerLoc)
    {
        // Place player in maze - start location
        // Sets players location
        playerLocation = playerLoc;
        // checks players location is in startLocations List
        if (startLocation.equals(playerLocation))
        {
            // Set (x, y) to 'P'
            // NB - array[row = y][col = x]
            //mazeArray[x][y] = 'P';
            mazeArray[playerLocation.y][playerLocation.x] = 'P';
        }
        else
        {
            throw new IllegalArgumentException("Player not at valid start location");
        }
        // Start timer
        mazeTimer.startTimer();

    }
    


    /*
     * getExitLocation function - returns exit location in maze array
     * @return exitLocation - Point(x, y) of exit location 
     */
    public Point getExitLocation()
    {
        return exitLocation;
    }

    /*
     * set_Locations function - iterates throught the maze array and sets locations
     *  
     */
    public void set_locations()
    {
        // Iterate through array add start locations to startLoc list
        for (int y = 0; y < mazeArray.length; y++)
        {
            for (int x = 0; x < mazeArray[y].length; x++)
            {
                // Add start location
                if (mazeArray[y][x] == 'S')
                {
                    startLocation = new Point(x, y);
                }
                // Add exit location
                else if (mazeArray[y][x] == 'E')
                {
                    exitLocation = new Point(x, y);
                }
                // Add key locations
                else if (mazeArray[y][x] == 'K')
                {
                    keyLocationsList.add(new Point(x, y));
                }
                
                // Add bot locations
                else if (mazeArray[y][x] == 'B')
                {
                    botsLocationsList.add(new Point(x, y));

                    // Removing bot locations once logged, controlled by client.
                    mazeArray[y][x] = '.';
                }
                // Add trap locations - wormhole and timewarp
                // Add wormhole
                else if (mazeArray[y][x] == 'H')
                {
                    wormholeLocationsList.add(new Point(x, y));
                }
                // Add timewarp

                // Add pickup locations - bomb and bonus points
                // Add bomb
                else if (mazeArray[y][x] == 'M')
                {
                    bombLocationsList.add(new Point(x, y));
                }
                // Add bonus points
                else if (mazeArray[y][x] == '$')
                {
                    bonusPointsLocationsList.add(new Point(x, y));
                }

            }
        }    
    }

    /* 
    * setKeysStatus function - inputs keyLocations into a map and defaults collected bool value to false
    * @param keyLocations - Point object array of key locations in mazeArray
    */
    private void setKeyStatus(List<Point> keyLocationsList)
    {
        // Key Status: (x, y)-> False
        for (int i = 0; i < keyLocationsList.size(); i++)
        {
            keyStatus.put(keyLocationsList.get(i), false);
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

            // Attempting to unlock the exit
            unlockExit(player);
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


    // bypassUnlockExit function - dev tool to check validMove - delete after tests
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
     * newLevel function - if player exits maze, start new level
     */
    public void newLevel()
    {
        if ((currentLevel < maxLevel)) {
        logger.info("Moving to next level");
        // Pause Timer
        mazeTimer.pauseTimer();

        // increment current level
        currentLevel++;
        // Create new Maze, dependent on current level
        this.mazeArray = createNewMaze(currentLevel);

        // Removing the previous bot locations
        botsLocationsList.clear();

        // Set start, key... locations
        set_locations();

        // Exit location
        this.exitLocation = getExitLocation();

        // Initalise keyStatus with collected = false
        setKeyStatus(keyLocationsList);

        // Current time taken to update score
        timeTaken = mazeTimer.getSubTotalTime();

        // Re-position player's start location -- this will be problematic for >1 players
        // Select random location from startLocationsList and calls playerEntersMaze
        //Random rand = new Random();
        //int randomStartIndex = rand.nextInt(startLocationsList.size());
        //playerLocation = startLocationsList.get(randomStartIndex);
        playerLocation = startLocation;
        playerEntersMaze(playerLocation);
        // Reset player's number of keys to zero

        mazePlayer.resetKeys();
        int mazePlayerNumKeys = mazePlayer.checkNumberOfKeys()
        System.out.println("Called resetKeys(), player numKeys = " + mazePlayerNumKeys);
        //logger.info("PlayerKeys reset attempt! numKeys = " + mazePlayer.checkNumberOfKeys()); 
        

        } else if (currentLevel == maxLevel) {
            callGameOver();
        } else {
            logger.info("New Level conditions were not met!");
        }
    }

    /*
    * playerDead function - called by the server if the registered player runs out
    * of lives
     */
    public void playerDead() {
        gameFinished = true;
        callGameOver();
    }


    /* 
    * gameOver function - checks if player is at exit - called in updatePlayerLocationMaze
    * @ return timeTaken - return 
    */
    public void callGameOver()
    {
        gameFinished = true;
        // Could also have a print message of game over
        mazeTimer.stopTimer();
        // Update time taken
        timeTaken = mazeTimer.getTimeTaken();
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
        playerPrevLocationList.add(playerLocation);
        // Update playerLocation in mazeArray to newMove
        //playerLocation = new Point(x, y);
        playerLocation = new Point(newMove);

        // Reducing ellapsed time for chest collection
        if (bonusPointsLocationsList.contains(playerLocation)) {
            mazeTimer.reduceTime();
        }

        // Set new player location to 'P'
        mazeArray[newMove.y][newMove.x] = 'P';
        // Set previous player location to '.'
        mazeArray[prevMove.y][prevMove.x]= '.';

        // Check if player location picks up a key
        updateKeyStatus(mazePlayer, playerLocation);

        // Check if player steps on a trap - wormhole
        if (collisionDetectWormhole())
        {
            // Change player location to a random Point
            playerLocation = new Point(randomRelocationPoint());
            playerPrevLocationList.add(playerLocation);
            // Update player location in map to 'P'
            mazeArray[playerLocation.y][playerLocation.x] = 'P';
            // Update previous position to '.'
            mazeArray[prevMove.y][prevMove.x]= '.';
            mazeArray[newMove.y][newMove.x]= '.';
        }
        
        // Check if player steps on a trap - bomb
        if(collisionDetectBomb())
        {
            // Remove all walls (replace with path - '.') within one tile
            blowUpWalls();

        }
    }

    /*
     * collisionDetectWormhole()
     * @return boolean - true of playerLocation equals a wormhole location
     */
    public boolean collisionDetectWormhole()
    {
        // If player location equals location of wormhole trap - return true
        if (wormholeLocationsList.contains(playerLocation))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * collisionDetectBomb()
     * @return boolean - true of playerLocation equals a bomb location
     */
    public boolean collisionDetectBomb()
    {
        // If player location equals location of bomb trap - return true
        if (bombLocationsList.contains(playerLocation))
        {
            return true;
        }
        else{
            return false;
        }
    }
    
    /*
     * randomRelocationPoint() function - randomly selects a valid random Point location
     * @return point - a randomly selected Point
     */
    // Random player relocation
    public Point randomRelocationPoint()
    {
        // Find random location that is valid
        Point randomLocation;
        do{
            Random rand = new Random();
            randomLocation = new Point(rand.nextInt(mazeWidth), rand.nextInt(mazeWidth));
        }
        while (!validMove(randomLocation));
        // update player location to there
        return randomLocation;
    }

    /*
     * blowUpWalls function - blows up the wall tiles around the player's location
     */
    public void blowUpWalls()
    {
        // Set allDirections to co-ords north, south, eaat, west of player by one tile
        Point north = new Point(playerLocation.x, playerLocation.y-1);
        Point east = new Point(playerLocation.x+1, playerLocation.y);
        Point south = new Point(playerLocation.x, playerLocation.y+1);
        Point west = new Point(playerLocation.x-1, playerLocation.y);
        List<Point> allDirections = new ArrayList<Point>(){
            {
                add(north);
                add(east);
                add(south);
                add(west);
            }
        };
        // Iterate through allDirections and change tiles to path('.')
        for (int i = 0; i < allDirections.size(); i++)
        {
            int tempX = allDirections.get(i).x;
            int tempY = allDirections.get(i).y;
            mazeArray[tempY][tempX] = '.';
        }

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
     * Pass a list<string> with the coordinates of the bots.
     */
    public List<Point> getBotStartLocations()
    {
        return botsLocationsList;
    }

}