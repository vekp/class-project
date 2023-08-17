package minigames.server.spacemaze;

import java.awt.Point;
import java.util.Random;

/*
 * Class for bot.
 * @author Nikolas Olins
 */

public class SpaceBot extends SpaceEntity {
    public SpaceBot(Point startLocation) {
        super(startLocation);
    }

    /*
     * Method to randomly move a bot around.
     * @param Optional int to test a specific movement direction, must be  0 <= x >= 3.
     * @return Point with the attempted move location.
     */

    // Method for testing moveAttempt
    public Point getMoveAttempt(int move)
    {
        if(move < 0 || move > 3)
        {
            throw new IllegalArgumentException("The movement test input must be between 0 and 3 inclusive.");
        }

        return moveAttempt(move);
    }
    // Method for normal use of the moveAttempt method.
    public Point getMoveAttempt()
    {
        Random ran = new Random();
        int decision = ran.nextInt(4); 
     
        return moveAttempt(decision);
    }

    // Move attempt method to perform the actual move.
    private Point moveAttempt(int move)
    {
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
}