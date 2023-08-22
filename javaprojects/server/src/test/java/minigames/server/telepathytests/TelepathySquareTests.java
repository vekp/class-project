package minigames.server.telepathy;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class TelepathySquareTests{

    @Test
    @DisplayName("Square object getters work as expected")
    public void testSquareGetters(){
        
        Square square = new Square(0, 0, Colours.PURPLE, Symbols.HEARTS);

        assertEquals(0, square.getHorizontalPos());
        assertEquals(0, square.getVerticalPos());
        assertEquals(Colours.PURPLE, square.getSquareColour());
        assertEquals(Symbols.HEARTS, square.getSquareSymbol());

    }

}