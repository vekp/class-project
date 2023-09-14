package minigames.server.telepathytests;

import minigames.server.telepathy.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Ignore;


public class TelepathyTileTests{

    @Test
    @DisplayName("Tile object getters work as expected")
    public void testTileGetters(){
        
        Tile tile = new Tile(0, 0, Colours.RED, Symbols.CLUBS);

        assertEquals(0, tile.getHorizontalPos());
        assertEquals(0, tile.getVerticalPos());
        assertEquals(Colours.RED, tile.getTileColour());
        assertEquals(Symbols.CLUBS, tile.getTileSymbol());

    }

    @Test
    @Disabled
    @DisplayName("Test Tile comparison")
    public void testTileComparisons(){
        // Check each field for a full match
        Tile t1 = new Tile(0, 0, Colours.BLUE, Symbols.SPADES);
        
            // Will match t1
        Tile t2 = new Tile(0, 0, Colours.BLUE, Symbols.SPADES);
        
            // Will not match t1
        Tile t3 = new Tile(0, 0, Colours.RED, Symbols.SPADES);
        Tile t4 = new Tile(0, 1, Colours.BLUE, Symbols.SPADES);
        Tile t5 = new Tile(1, 0, Colours.BLUE, Symbols.SPADES);
        
        assertTrue(t1.isFullMatch(t2));

        assertFalse(t1.isFullMatch(t3));
        assertFalse(t1.isFullMatch(t4));
        assertFalse(t1.isFullMatch(t5));

        // Check each field for a partial match compared against t1
        t2 = new Tile(1, 1, Colours.RED, Symbols.SPADES);
        t3 = new Tile(1, 1, Colours.BLUE, Symbols.CIRCLES);
        t4 = new Tile(1, 0, Colours.RED, Symbols.CIRCLES);
        t5 = new Tile(0, 1, Colours.RED, Symbols.CIRCLES);
        
        assertTrue(t1.isPartialMatch(t2));
        assertTrue(t1.isPartialMatch(t3));
        assertTrue(t1.isPartialMatch(t4));
        assertTrue(t1.isPartialMatch(t5));

        Tile t6 = new Tile(1, 1, Colours.RED, Symbols.CIRCLES);
        assertFalse(t1.isPartialMatch(t6));
    }

}