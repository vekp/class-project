package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import io.vertx.core.json.JsonObject;

import minigames.server.telepathy.Player;
import minigames.telepathy.TelepathyCommandException;



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
}
