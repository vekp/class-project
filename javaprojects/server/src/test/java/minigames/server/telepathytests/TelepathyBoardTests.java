package minigames.server.telepathy;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Tests for the Telepathy board class
 */

public class TelepathyBoardTests{

    //Tests to be discussed...extremely likely there are much better ways to implement these...
    
    @DisplayName ("The generateBoard method should return a 2D array of tile objects")
    @Test
    public void testGenerateBoard(){ 
        Board testTelepathyBoard = new Board();
        int testBoardLength = testTelepathyBoard.getBoard().length;
        ArrayList<Object> checkLength = new ArrayList<>();
        
        for (int row = 0; row < testBoardLength; row++){
            for (int col = 0; col < testBoardLength; col++) {
                Tile testTile = testTelepathyBoard.getBoard()[col][row];

                int horizontal = testTile.getHorizontalPos();
                int vertical = testTile.getVerticalPos();
                
                assertTrue(horizontal == col);
                assertTrue(vertical == row);
                checkLength.add(testTile);
                
            }
        }
        assertTrue(checkLength.size() == (testBoardLength * testBoardLength));

        
    }

    @DisplayName ("Assert the generateBoard method returns the same array of tile objects")
    @Test
    public void testBoardEquality(){
        Board testTelepathyBoard = new Board();
        Board testTelepathyBoard2 = new Board();

        Tile testTile1 = testTelepathyBoard.getBoard()[0][0];
        Tile testTile2 = testTelepathyBoard2.getBoard()[0][0];

        int horizontal1 = testTile1.getHorizontalPos();
        int vertical1 = testTile1.getVerticalPos();
        Colours colour1 = testTile1.getTileColour();
        Symbols symbol1 = testTile1.getTileSymbol();

        int horizontal2 = testTile2.getHorizontalPos();
        int vertical2 = testTile2.getVerticalPos();
        Colours colour2 = testTile2.getTileColour();
        Symbols symbol2 = testTile2.getTileSymbol();

        assertTrue(horizontal1 == horizontal2);
        assertTrue(vertical1 == vertical2);
        assertTrue(colour1.equals(colour2));
        assertTrue(symbol1.equals(symbol2));
    }

    @DisplayName ("Test getTile method works as predicted")
    @Test
    public void testGetTile(){
        Board testTelepathyBoard = new Board();
        Tile testTile = testTelepathyBoard.getTile(0, 0);
        int horizontal = testTile.getHorizontalPos();
        int vertical = testTile.getVerticalPos();
    
        assertTrue(horizontal == 0);
        assertTrue(vertical == 0);
    }



    @DisplayName ("Test generateColours returns an array of colour constants")
    @Test
    public void testGenerateColours(){
        Board board = new Board();
       
        ArrayList<Object> expectedList = new ArrayList<>();
        expectedList.add(Colours.RED);
        expectedList.add(Colours.PINK);
        expectedList.add(Colours.BLUE);
        expectedList.add(Colours.CYAN);
        expectedList.add(Colours.YELLOW);
        expectedList.add(Colours.ORANGE);
        expectedList.add(Colours.MAGENTA);
        expectedList.add(Colours.GREEN);
        expectedList.add(Colours.GREY);

        ArrayList<Object> actualList = board.generateColours();

        assertEquals(expectedList, actualList);
      
    } 


    @DisplayName ("Test generateSymbols returns an array of symbol constants")
    @Test
    public void testGenerateSymbols(){
        Board board = new Board();
       
        ArrayList<Object> expectedList = new ArrayList<>();
        expectedList.add(Symbols.HEARTS);
        expectedList.add(Symbols.STARS);
        expectedList.add(Symbols.DIAMONDS);
        expectedList.add(Symbols.SPADES);
        expectedList.add(Symbols.CLUBS);
        expectedList.add(Symbols.MOONS);
        expectedList.add(Symbols.CIRCLES);
        expectedList.add(Symbols.QUESTION_MARKS);
        expectedList.add(Symbols.EXCALAMATION_MARKS);

        ArrayList<Object> actualList = board.generateSymbols();
        

        assertEquals(expectedList, actualList);
      
    } 

} 