package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import io.vertx.core.json.JsonObject;
import minigames.server.telepathy.Colours;
import minigames.server.telepathy.Player;
import minigames.server.telepathy.Symbols;
import minigames.telepathy.TelepathyCommandException;

import minigames.server.telepathy.Tile;



public class TelepathyPlayerTests {
    @Test
    @DisplayName("Test initial Player construction")
    public void testConstructPlayer(){
        Player testPlayer = new Player("Bob");
        assertTrue(testPlayer.getName().equals("Bob"));
        assertTrue(testPlayer.isReady() == false);
    }

    @Test
    @DisplayName("Test Player pending updates")
    public void testPendingUpdates(){
        Player testPlayer = new Player("Bob");

        // Begins with no updates - NOUPDATES
        ArrayList<JsonObject> updates = testPlayer.getUpdates(); 
        assertTrue(updates.size() == 1);
        System.out.println(updates.get(0).getString("command"));
        assertTrue(updates.get(0).getString("command").equals("NOUPDATE"));

        // Try invalid command
        assertThrows(TelepathyCommandException.class, () -> {
            testPlayer.addUpdate(new JsonObject().put("command", "not a command"));
        });
        updates = testPlayer.getUpdates();
        assertTrue(updates.size() == 1);
        assertTrue(updates.get(0).getString("command").equals("NOUPDATE"));

        // Add valid update - test that queue is cleared after get
        assertDoesNotThrow(() ->{
            testPlayer.addUpdate(new JsonObject().put("command", "TESTCOMMAND"));
        });
        updates = testPlayer.getUpdates();
        assertTrue(updates.size() == 1);
        assertTrue(updates.get(0).getString("command").equals("TESTCOMMAND"));

        updates = testPlayer.getUpdates();
        assertTrue(updates.size() == 1); // Queue is cleared after a get
        assertTrue(updates.get(0).getString("command").equals("NOUPDATE"));

    }

    @Test
    @DisplayName("Test player ready state")
    public void testPlayerReady(){
        Player testPlayer = new Player("Bob");

        // Initialised to false
        assertTrue(testPlayer.isReady() == false);

        // Toggle sets to true
        testPlayer.toggleReady();
        assertTrue(testPlayer.isReady() == true);

        // Toggle when ready sets back to false
        testPlayer.toggleReady();
        assertTrue(testPlayer.isReady() == false);
    }

    @Test
    @DisplayName("Test player turn counter")
    public void testPlayerTurnCounter(){
        // Create a test player
        Player testPlayer = new Player("Bob");

        // Check the counter is being incremented correctly
        assertTrue(testPlayer.getTurnCounter() == 0);
        testPlayer.incrementTurns();
        assertTrue(testPlayer.getTurnCounter() == 1);
        testPlayer.incrementTurns();
        assertFalse(testPlayer.getTurnCounter() == 1);
    }

    @Test
    @DisplayName("Test player set 'chosen tile'")
    public void testSettingPlayerTile(){
        Player testPlayer = new Player("Bob");

        assertTrue(testPlayer.getChosenTile() == null);
        
        // Test tile to set as players tile
        Tile testTile = new Tile(0, 0, Colours.BLUE, Symbols.CIRCLES);
        assertTrue(testPlayer.setChosenTile(testTile));
        assertTrue(testPlayer.getChosenTile() == testTile);

        Tile secondTestTile = new Tile(0, 1, Colours.CYAN, Symbols.CLUBS);
        assertFalse(testPlayer.setChosenTile(secondTestTile));
        assertTrue(testPlayer.getChosenTile() == testTile);

    }
}
