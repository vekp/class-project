package minigames.server.battleship;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

/**
 * Ensures that Battleship is performing as expected
 */
public class BattleshipTests {
    private final String[] shipClasses = {"Carrier", "Patrol Boat", "Submarine", "Destroyer", "Battleship"};

    // Board and Ship Placement
    /** Tests whether a default board is created for the player*/
    @Test
    public void boardDefaultPopulates(){
        // create boards using the default methods
        Board testBoard = new Board(0);
        Board otherTestBoard = new Board(1);
        // check that the appropriate number of ships are present on the boards
        assert isBoardValid(testBoard);
        assert isBoardValid(otherTestBoard);
    }

    /**
     * Returns whether given Board contains all ships as specified in shipClasses
     */
    private boolean isBoardValid (Board board) {
        for (String shipClass : shipClasses) {
            if (!board.getVessels().containsKey(shipClass)) {
                return false;
            }
        }
        return board.getVessels().size() == shipClasses.length;
    }

    // TODO: implement test after ship placement done
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
    @Test
    public void missDetected(){
        // create an ocean cell
        Cell testCell = new Cell(0, 0);
        // assert cell is ocean
        assert testCell.getCellType().equals(CellType.OCEAN);
        assert testCell.getCellTypeString().equals("~");
        // shoot at the ocean cell
        testCell.shoot();
        // assert that the cell is now a "miss" cell
        assert testCell.getCellType().equals(CellType.MISS);
        assert testCell.getCellTypeString().equals(".");
    }

    /**
     * Create a Cell array of 5 elements, one for each of the ship parts.
     */
    private Cell[] makeTestShipCells() {
        Cell[] cells = new Cell[5];
        for (int i = 0; i < 5; i++) {
            Cell cell = new Cell(i, 0);
            // first 5 values of CellTypes are ship parts
            cell.setCellType(CellType.values()[i]);
            cells[i] = cell;
        }
        return cells;
    }

    /** Tests whether hitting any of the ship hull types will register a hit, and the cell is changed as appropriate*/
    @Test
    public void hitDetected(){
        // create cells for each of the ship hull types and add to an array
        Cell[] cells = makeTestShipCells();
        // assert each cell is not hit yet, then shoot at each one and assert it is now hit
        for (Cell cell : cells) {
            assert !cell.getCellType().equals(CellType.HIT);
            cell.shoot();
            assert cell.getCellType().equals(CellType.HIT);
        }
    }

    /** Tests whether destroying all cells of a ship will cause the ship to be considered "sunk"*/
    @Test
    public void shipSinks(){
        // create a ship
        Cell[] cells = makeTestShipCells();
        Ship ship = new Ship("TestShip", 0, cells,0, 0, true);
        // shoot at all cells in ship
        for (int i = 0; i < cells.length; i++) {
            assert !ship.isSunk();
            assert !ship.isJustSunk();
            cells[i].shoot();
            ship.updateShipStatus(i, 0, "TestName");
        }
        // assert that the ship is sunk
        assert ship.isSunk();
        assert ship.isJustSunk();
    }


    /**
     * Test that valid user input is accepted and invalid input is rejected
     */
    @Test
    public void testInputValidation() {
        // create a test player
        Board testBoard = new Board(0);
        Board opponentBoard = new Board(1);
        BattleshipPlayer testPlayer = new BattleshipPlayer("test", testBoard, true, "");
        // test invalid inputs
        String[] invalidInputs = {"a11", "k5", "1a", "7j", "4", "c", "Your mama", "COSC220 is awesome!", "Is anyone reading this?"};
        for (String invalidInput : invalidInputs) {
            BattleshipTurnResult result = testPlayer.processTurn(invalidInput, opponentBoard);
            assert result.playerMessage().startsWith(invalidInput + " is an invalid entry. Please enter a coordinate in the form 'A7'");
        }
        // test valid inputs
        String[] validInputs = {"A0", "a5", "A9", "e2", "E4", "g7", "J0", "j5", "J9"};
        for (String validInput : validInputs) {
            BattleshipTurnResult result = testPlayer.processTurn(validInput, opponentBoard);
            assert result.playerMessage().endsWith("Prepare for incoming fire!");
        }

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
