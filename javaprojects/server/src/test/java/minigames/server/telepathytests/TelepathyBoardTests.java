package minigames.server.telepathy;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Tests for the Telepathy board class
 */

public class TelepathyBoardTests{

    //Tests to be discussed...extremely likely there are much better ways to implement these...
    
    @Test
    @DisplayName ("The generateBoard method should return a 2D array of square objects")
    public void testGenerateBoard(){ 
        Board testTelepathyBoard = new Board();
        int testBoardLength = testTelepathyBoard.getBoard().length;
        ArrayList<Object> checkLength = new ArrayList<>();
        
        for (int row = 0; row < testBoardLength; row++){
            for (int col = 0; col < testBoardLength; col++) {
                Square testSquare = testTelepathyBoard.getBoard()[row][col];

                int horizontal = testSquare.getHorizontalPos();
                int vertical = testSquare.getVerticalPos();
                
                assertTrue(horizontal == row);
                assertTrue(vertical == col);
                checkLength.add(testSquare);
                
            }
        }
        assertTrue(checkLength.size() == (testBoardLength * testBoardLength));

        
    }

    @Test
    @DisplayName ("Assert the generateBoard method returns the same array of square objects")
    public void testBoardEquality(){
        Board testTelepathyBoard = new Board();
        Board testTelepathyBoard2 = new Board();

        Square testSquare1 = testTelepathyBoard.getBoard()[0][0];
        Square testSquare2 = testTelepathyBoard2.getBoard()[0][0];

        int horizontal1 = testSquare1.getHorizontalPos();
        int vertical1 = testSquare1.getVerticalPos();
        Colours colour1 = testSquare1.getSquareColour();
        Symbols symbol1 = testSquare1.getSquareSymbol();

        int horizontal2 = testSquare2.getHorizontalPos();
        int vertical2 = testSquare2.getVerticalPos();
        Colours colour2 = testSquare2.getSquareColour();
        Symbols symbol2 = testSquare2.getSquareSymbol();

        assertTrue(horizontal1 == horizontal2);
        assertTrue(vertical1 == vertical2);
        assertTrue(colour1.equals(colour2));
        assertTrue(symbol1.equals(symbol2));
    }


    @Test
    @DisplayName ("Test getSquare method works as predicted")
    public void testGetSquare(){
        Board testTelepathyBoard = new Board();
        Square testSquare = Board.getSquare(0, 0, testTelepathyBoard);
        int horizontal = testSquare.getHorizontalPos();
        int vertical = testSquare.getVerticalPos();
    
        assertTrue(horizontal == 0);
        assertTrue(vertical == 0);
    }

    
    @Test
    @DisplayName ("Test generateColours returns an array of colour constants")
    public void testGenerateColours(){
       
        ArrayList<Object> expectedList = new ArrayList<>();
        expectedList.add(Colours.RED);
        expectedList.add(Colours.PINK);
        expectedList.add(Colours.BLUE);
        expectedList.add(Colours.BROWN);
        expectedList.add(Colours.YELLOW);
        expectedList.add(Colours.ORANGE);
        expectedList.add(Colours.PURPLE);
        expectedList.add(Colours.GREEN);
        expectedList.add(Colours.GREY);

        ArrayList<Object> actualList = Board.generateColours();

        assertEquals(expectedList, actualList);
      
    } 


    @Test
    @DisplayName ("Test generateColours returns an array of symbol constants")
    public void testGenerateSymbols(){
       
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

        ArrayList<Object> actualList = Board.generateSymbols();
        

        assertEquals(expectedList, actualList);
      
    } 

} 