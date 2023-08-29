package minigames.client.spacemaze;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Random;
import minigames.spacemaze.SpaceEntity;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.File;
import java.awt.Color;
//import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.lang.Math;


/*
 * Class for bot.
 * @author Nikolas Olins
 */

public class SpaceBot extends SpaceEntity {
    private char tile;
    //private Timer timer;
    // May need to use this IRT mazeControl. remove it not required.
    private char[][] mazeMap;

    //private Timer botTimer;
    private Image botImage;

    public SpaceBot(Point startLocation) {
        super(startLocation);
        this.tile = '.';
        //this.mazeMap = maze;
        //botTimer = new Timer(0, null);
        botImage = new ImageIcon(getClass().getResource("/images/spacemaze/alien1a.png")).getImage();
    }

    /**
     * Private method to get a list of valid moves the bot could make.
     * @return a ArrayList<Point> of valid moves.
     */
    private ArrayList<Point> getValidMoves() {
        ArrayList<Point> validMoves = new ArrayList<Point>();
        int validDecision;
        
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
     * @param playerPos a Point of the player's current position.
     * @param possibleMoves an ArrayList<Point> of valid moves the bot could take.
     * @return a Point of the a move to close the distance to the player.
     */
    private Point distanceCloser(Point playerPos, ArrayList<Point> possibleMoves) {
        //calculate current distance
        int currYDistance = Math.abs(playerPos.y - location.y);
        int currxDistance = Math.abs(playerPos.x - location.x);
        double currDistance = Math.hypot(currYDistance, currxDistance);
        // Set to -1 to show no current moves improve distance.
        int bestMove = -1;
        // set to a distance not possible on the map.
        double bestDistance = 50000.0;
        
        for(int i = 0;i<possibleMoves.size();i++) {
            // index out of bounds here.
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
        }
        else
        {
            Point invalid = new Point(-1,-1);
            return invalid;
        }

    }
    /*
    public void moveBot() {
        Point moveNext = getMoveAttempt();
        if (MazeDisplay.isMoveValid(moveNext)) {
            updateLocation(moveNext);
        }
    }
    */
    /*
     * Method to randomly move a bot around.
     * @param Optional int to test a specific movement direction, must be  0 <= x >= 3.
     * @return Point with the attempted move location.
     *//*
    public void startTimer(int updateSpeed){

        botTimer.setDelay(updateSpeed);
        botTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == botTimer) {
                    System.out.println("Current location: " + location.x + ", " + location.y);
                    Point moveNext = getMoveAttempt();
                    updateLocation(moveNext);
                }
            }

        });
        botTimer.start();
    }

    public void stopTimer() {
        if (botTimer != null) {
            botTimer.stop();
        }
    }*/

    /**
     * Public method to allow testing of the moveAttempt method.
     * @param move an int representing the direction to move. 0 : Up, 1 : right, 2 : down, 3 : left.
     * @return a Point representing the new position after making the move.
     */
    public Point getMoveAttempt(int move) {
        if(move < 0 || move > 3)
        {
            throw new IllegalArgumentException("The movement test input must be between 0 and 3 inclusive.");
        }

        return moveAttempt(move);
    }
    // Method for normal use of the moveAttempt method.
    public Point getMoveAttempt() {
        Random ran = new Random();
        int decision = ran.nextInt(4);

        return moveAttempt(decision);
    }
    /**
     * Method to make the bot choose a move which decreases the distance to the player.
     * @param playerPosition a Point representing the players current position.
     */
    public void moveCloser(Point playerPosition) {
         ArrayList<Point> validMoves = getValidMoves();

        if (validMoves.size() > 0) {
            Point closestMove = distanceCloser(playerPosition, validMoves);
            // A move to close the distance.
            if(closestMove.x > -1 && closestMove.y > -1) {

                updateLocation(closestMove);
                return;
            }
            else
            {
                // no valid moves to close the distance, so use a random move.
                Random ran = new Random();
                moveRandom(ran);
            }
        }
    }

    /**
     * Method to make the bot move randomly.
     * @param ran an instance of the Random class.
     */
    public void moveRandom(Random ran) {
        
        int validDecision;
        ArrayList<Point> validMoves = getValidMoves();

        
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
     * Public method to resolve the move command into a new Point.
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
    // Way for the bot to record what it is standing on (key,power up etc)
    /*
    public void setTile(char t) {
        tile = t;
    }

    public char getTile() {
        return tile;
    }
    */
    public Image getBotImage() {
        return botImage;
    }
}