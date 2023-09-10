package minigames.server.battleship;

import org.junit.jupiter.api.Test;

/**
 * Ensures that Battleship is performing as expected
 */
public class BattleshipTests {

    // Board and Ship Placement
    /** Tests whether a default board is created for the player*/
    @Test
    public void boardDefaultPopulates(){
        // create a board using the default method
        // check that the appropriate number of ships are present on the board (maybe have a checker function)
    }


    /** Tests whether ships can be placed on a board*/
    public void shipPlaceable(){
        // create a board with custom placement
        // try to place a ship in a valid space
        // assert that the ship placement function will return true
    }

    /** Tests whether the board detects and corrects a ship being placed illegally*/
    //TODO: Come up with a better name for this function
    public void shipOutofBounds(){
        // create a board
        // try to place a ship on the edge where it won't fit
        // assert that the placement function will throw an appropriate exception
    }

    // Hit detection
    /** Tests whether hitting an ocean tile will register as a miss, and the cell will be changed as appropriate*/
    public void missDetected(){
        // create an ocean cell
        // shoot at the ocean cell
        // assert that the cell is now a "miss" cell
    }

    /** Tests whether hitting any of the ship hull types will register a hit, and the cell is changed as appropriate*/
    public void hitDetected(){
        // create cells for each of the ship hull types and add to an array
        // shoot at each of the cells in the array
        // assert that all the cells within the array are now "hit" cells
    }

    /** Tests whether destroying all cells of a ship will cause the ship to be considered "sunk"*/
    public void shipSinks(){
        // create a ship
        // shoot at all cells in ship
        // assert that the ship is sunk
    }

    // Rounds, game-over, etc.
    public void roundCounts(){
        // start a game, and increment rounds
        // assert that the round counter increments as expected
    }

    public void gameFinished(){
        // start a game and sink all ships except for one
        // shoot at the final ship until it is sunk
        // assert that the game has ended
    }

}
