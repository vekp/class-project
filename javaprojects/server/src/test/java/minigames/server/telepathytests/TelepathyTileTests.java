package minigames.server.telepathy;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


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

}