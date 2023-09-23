package minigames.server.battleship;

import minigames.achievements.Achievement;
import minigames.server.achievements.AchievementHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Ensures that Battleship is performing as expected
 */
public class BattleshipTests {

    @DisplayName("Tests whether a default board is correctly created for the player")
    @Test
    public void testBoardConstructor(){
        // define the ship classes
        String[] shipClasses = {"Carrier", "Patrol Boat", "Submarine", "Destroyer", "Battleship"};
        // create boards using the default methods
        for (int i = 0; i < 50; i++) {
            Board testBoard = new Board();
            // check number of vessels is correct
            assertEquals(shipClasses.length, testBoard.getVessels().size());
            // check each ship class is present in board
            for (String shipClass : shipClasses) {
                assert testBoard.getVessels().containsKey(shipClass);
            }
        }
    }

    @DisplayName("Tests whether hitting an ocean tile will register as a miss, and the cell will be changed as appropriate")
    @Test
    public void testOceanCell(){
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
     * Create a Cell array of 5 elements, one for each of the different ship part cell types.
     * Used for testing hits and sinking of ships.
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

    @DisplayName("Tests whether hitting any of the ship hull types will register a hit, and the cell is changed as appropriate")
    @Test
    public void testShipCell(){
        // create cells for each of the ship hull types and add to an array
        Cell[] cells = makeTestShipCells();
        // assert each cell is not hit yet, then shoot at each one and assert it is now hit
        for (Cell cell : cells) {
            assert !cell.getCellType().equals(CellType.HIT);
            cell.shoot();
            assert cell.getCellType().equals(CellType.HIT);
        }
    }

    @DisplayName("Tests whether destroying all cells of a ship will cause the ship to be considered sunk")
    @Test
    public void testShip(){
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


    @DisplayName("Tests that valid user input is accepted and invalid input is rejected")
    @Test
    public void testProcessTurnValidation() {
        // create a test player
        Board testBoard = new Board();
        Board opponentBoard = new Board();
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

    @DisplayName("Tests that gameFinished returns true only after all ships are sunk")
    @Test
    public void testGameOver(){
        // Register all achievements to prevent errors when unlocking ship sinking achievements
        AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
        for (achievements a : achievements.values()) {
            handler.registerAchievement(new Achievement(a.toString(), "", 0, "", false));
        }
        // Create test board, iterate through each ship
        Board testBoard = new Board();
        testBoard.getVessels().forEach((shipType, ship) -> {
            Cell[] shipParts = ship.getShipParts();
            // iterate through each cell in ship's parts
            for (Cell shipPart : shipParts) {
                // assert game not yet over
                assert !testBoard.checkGameOver("");
                int col = shipPart.getVerticalCoordInt();
                int row = shipPart.getHorizontalCoord();
                // shoot the cell
                ship.updateShipStatus(col, row, "");
            }
        });
        // All ships have been sunk, assert that the game has ended
        assert testBoard.checkGameOver("");
    }
}
