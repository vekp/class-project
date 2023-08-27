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
    /*
     *  Randomly decide which way to move, if move is not valid do not move anywhere this time.
     */
    public void moveBot() {
        Point moveNext = getMoveAttempt();
        if (MazeDisplay.isMoveValid(moveNext)) {
            updateLocation(moveNext);
        }
    }
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

    // Method for testing moveAttempt
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

    // Testing movement, method to ONLY get valid moves the bot can make.
    public void validMoveBot() {
        Random ran = new Random();
        ArrayList<Point> validMoves = new ArrayList<Point>();
        
        // Checking for valid moves, 0 : Up, 1 : Right, 2 : Down, 3 : Left.
        for(int i = 0;i<4;i++)
        {
            Point newLocation = moveAttempt(i);
             // Is a valid move
             if (MazeDisplay.isMoveValid(newLocation)) {
            validMoves.add(newLocation);
            }
        }       
        int validDecision = ran.nextInt((validMoves.size() - 1));
        // Get the random valid move.
        Point selectedMove = validMoves.get(validDecision);
        // perform the move.
        updateLocation(selectedMove);
    }

    // Move attempt method to perform the actual move.
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