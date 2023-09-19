package minigames.client.spacemaze;

import java.awt.Image;
import java.awt.Point;
import java.util.Random;
import minigames.spacemaze.SpaceEntity;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.lang.Math;

/**
 * Class for bots used within the maze, derived from SpaceEntity.
 * @author Nikolas Olins
 */

public class SpaceBot extends SpaceEntity {
    private Image botImage;
    private ArrayList<Point> moves;
    private boolean seeking;
    private Point startLocation;

    public SpaceBot(Point startLocation) {
        super(startLocation);
        this.moves = new ArrayList<Point>();
        this.seeking = true;
        this.startLocation = new Point(startLocation);
        botImage = new ImageIcon(getClass().getResource("/images/spacemaze/alien1a.png")).getImage();

    }

    /**
     * Private method to get a list of valid moves the bot could make.
     * @return a ArrayList<Point> of valid moves.
     */
    private ArrayList<Point> getValidMoves() {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        // Checking for valid moves, 0 : Up, 1 : Right, 2 : Down, 3 : Left.
        for(int i = 0;i<4;i++) {
            Point newLocation = moveAttempt(i);
             // Is a valid move
             if (MazeDisplay.isMoveValid(newLocation)) {
            validMoves.add(newLocation);
            }

        }
        return validMoves;

    }

    /** 
     * Private method to decide which valid move end up with the bot closing the distance to the player the most.
     * If two moves are the same distance, it will prioritise Left, Down, Right, Up.
     * @param playerPos a Point of the player's current position.
     * @param possibleMoves an ArrayList<Point> of valid moves the bot could take.
     * @return a Point of the a move to close the distance to the player.
     */
    private Point distanceCloser(Point playerPos, ArrayList<Point> possibleMoves) {

        // Set to -1 to show no current moves improve distance.
        int bestMove = -1;
        // set to a distance not possible on the map.
        double bestDistance = 50000.0;
        
        for(int i = 0;i<possibleMoves.size();i++) {

            Point thisMove = possibleMoves.get(i);
            int yDistance = Math.abs(playerPos.y - thisMove.y);
            int xDistance = Math.abs(playerPos.x - thisMove.x);
            
            double possDistance = Math.hypot(yDistance, xDistance);

            if(possDistance <= bestDistance) {
                bestMove = i;
                bestDistance = possDistance;
            }

        }

        // If there is a best move, return it. If not, return an invalid location.
        if(bestMove > -1) {
            return possibleMoves.get(bestMove);
        } else {
            Point invalid = new Point(-1,-1);
            return invalid;
        }

    }
    /**
     * Private method to decide if the bot should perform seeking movement. This method checks if the bot is currently seeking, and if it is checks for a movement loop which may indicate a bot is stuck.
     * @return boolean indicating the bot will choose to seek the player instead of moving randomly.
     */
    private boolean chooseSeeking() {
        int aSize = moves.size();
        
        // is not seeking (random move) and moved at least 5 moves, start seeking again
        if(seeking == false && aSize >= 5) {
            seeking = true;
            moves.clear();
            return seeking;

        }
        // Check for a movement loop - last three elements form a sequence a,b,a if seeking and travelled at least three moves
        else if(aSize >= 3 && seeking == true) {
            Point moveZero = new Point(moves.get(aSize-1));
            Point moveTwo = new Point(moves.get(aSize-3));
            // stuck sequence detected
            if(moveZero.equals(moveTwo)) {
                moves.clear();
                seeking = false;
                return seeking;
            }

        }

        return seeking;

    }

    /**
     * Private method to make the bot choose a move which decreases the distance to the player, or attempt to move randomly.
     * @param playerPosition a Point representing the players current position.
     */
    private void moveCloser(Point playerPosition, Point closestMove, ArrayList<Point> validMoves) {

        // A move to close the distance, when the Point is not -1,-1
        if(closestMove.x > -1 && closestMove.y > -1) {

            updateLocation(closestMove);
        } else {
            // no valid moves or moves to close the distance, attempt to move randomly.
            moveRandom(new Random(), validMoves);
        }

    }

    /**
     * Private method to attempt to make the bot move randomly. If no valid moves, do not move.
     * @param ran an instance of the Random class.
     */
    private void moveRandom(Random ran, ArrayList<Point> validMoves) {
        int validDecision;

        if (validMoves.size() > 1) {
            validDecision = ran.nextInt((validMoves.size() - 1));
        } else if (validMoves.size() == 1) {
            validDecision = 0;
        } else {
            // No valid moves
            return;
        }
        // Get the random valid move.
        Point selectedMove = validMoves.get(validDecision);
        // perform the move.
        updateLocation(selectedMove);
        
    }

    /**
     * Private method to resolve the move command into a new Point.
     * @param move an int representing the direction to move. 0 : Up, 1 : right, 2 : down, 3 : left.
     * @return a Point representing the new position after making the move.
     */
    private Point moveAttempt(int move) {
        // New point for attempted move.
        Point moveAttempt = new Point();
        // Int passed in for testing purposes, or random int from 0 - 3 deciding which way to try and move.

        // 0 - 3: Up, Right, Down, Left
        switch(move) {
            // up
            case 0:
                moveAttempt.move(location.x, location.y-1);
                break;
            case 1:
                moveAttempt.move(location.x+1, location.y);
                break;
            case 2:
                moveAttempt.move(location.x, location.y+1);
                break;
            case 3:
                moveAttempt.move(location.x-1, location.y);
                break;
        }
        return moveAttempt;

    }

     /**
     * Protected overriden method to update the bot's location and add the move into the ArrayList<Point> for movement switching.
     * @param newLocation a point representing the new location moved to.
     */
    @Override
    public void updateLocation(Point newLocation) {
        location = new Point(newLocation);
        moves.add(new Point(newLocation));
    }
    
     /**
     * Public method - entry point to make the bot move, called by external classes
     * @param playerPos a Point representing the player's current position. Used only for seeking movement.
     */
    public void move(Point playerPos) {
        // decide if the bot should seek or move randomly.
        boolean goSeek = chooseSeeking();
        // get a list of valid moves. NOTE - static method for checking valid moves in MazeControl. 
        // MazeControl will always be instantiated when bots are in use, saved passing MazeControl into the bots or refactoring the check this late.
        ArrayList<Point> validMoves = getValidMoves();
        
        if(goSeek) {     
            Point closestMove = distanceCloser(playerPos, validMoves);
            moveCloser(playerPos, closestMove, validMoves);
        }
        else {
            moveRandom(new Random(), validMoves);
        }

    }
    
     /**
     * Public method to reset the bot - back to its start location, default to seeking behaviour and clear the moves ArrayList.
     */
    public void reset() {
        super.updateLocation(startLocation);
        seeking = true;
        moves.clear();

    }

    public Image getBotImage() {
        return botImage;

    }


    //-----------------------------Testing Methods------------------------------
     /**
     * Public test method to get a list of valid moves the bot could make.
     * @return a ArrayList<Point> of valid moves.
     */
    public ArrayList<Point> testingGetValidMoves() {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        // Checking for valid moves, 0 : Up, 1 : Right, 2 : Down, 3 : Left.
        for(int i = 0;i<4;i++) {
            Point newLocation = moveAttempt(i);
             // Is a valid move
             System.out.println("valid location x,y :" + newLocation.x + ", " + newLocation.y);
             if (MazeDisplay.isMoveValid(newLocation)) {
                validMoves.add(newLocation);
            }
        }
        return validMoves;

    }

    /**
     * Public method to allow testing of the moveAttempt method.
     * @param move an int representing the direction to move. 0 : Up, 1 : right, 2 : down, 3 : left.
     * @return a Point representing the new position after making the move.
     */
    public Point testingGetMoveAttempt(int move) {
        if(move < 0 || move > 3) {
            throw new IllegalArgumentException("The movement test input must be between 0 and 3 inclusive.");
        }
        return moveAttempt(move);

    }
    /**
     * Public method to allow getting of internal moves arraylist.
     * @return ArrayList<Point> used to store bot moves.
     */
    public ArrayList<Point> testingGetMovesList() {
        return moves;
        
    }

    /**
     * Public method to return the seeking boolean if the bot is in seeking mode.
     * @return boolean of if the bot is seeking.
     */
    public boolean testingIsSeeking() {
        return seeking;

    }
    /**
     * Public method to test the logic of deciding which move to make when seeking.
     * @return boolean of if the bot is seeking.
     */
    public Point testingDistanceCloser(Point playerPos, ArrayList<Point> possibleMoves) {
        return distanceCloser(playerPos, possibleMoves);

    }
    /**
     * Public method to test the ability of the bot to choose a move which decreases the distance to the player.
     * @param playerPosition a Point representing the players current position.
     */
    public void testingMoveCloser(Point playerPosition, Point closestMove, ArrayList<Point> validMoves) {
        moveCloser(playerPosition, closestMove, validMoves);

    }

    /**
     * Public test method to make the bot move randomly.
     * @param ran an instance of the Random class.
     */
    public void testingMoveRandom(Random ran, ArrayList<Point> validMoves) {   
        moveRandom(ran, validMoves);
        
    }

    /**
     * Public method to test the logic of switching movement modes.
     * @return boolean of if the bot is seeking.
     */
    public boolean testingChooseSeeking() {
        return chooseSeeking();
        
    }

}