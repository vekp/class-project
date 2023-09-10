package minigames.client.telepathy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.vertx.core.json.JsonObject;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommands;

/**
 * Unit tests for the Telepathy client class.
 */
public class TelepathyTest {

    @Test
    @DisplayName("Test Telepathy initialisation")
    public void testTelepathyInitialisation(){
        // TODO: Implement constructor test
        Telepathy gameClient = new Telepathy();
    }

    @Test
    @DisplayName("Test sending CommandPackages")
    public void testSendCommand(){
        /*
        TODO: Fix test for sendCommand(). Cannot find way to verify the CommandPackage
        Vertx vertx = mock(Vertx.class);
        MinigameNetworkClient mnClient = new MinigameNetworkClient();
        String[] players = {"Jim"};
        GameMetadata gameData = new GameMetadata("Telepathy", "Telepathy", players, true);
        Telepathy gameClient = new Telepathy();
        gameClient.load(mnClient, gameData, "Bob");

        // Use a valid command
        gameClient.sendCommand(TelepathyCommands.REQUESTUPDATE);
        */
    }

    @Test
    @DisplayName("Test execution of renderingCommands")
    public void testExecute(){
        Telepathy gameClient = new Telepathy();

        String[] players = {"Bob"}; 
        GameMetadata serverData = new GameMetadata("Telepathy", "TelepathyGame", players, true);

        // Test QUIT
        // Test GAMEOVER
        // Test BUTTONUPDATE
        // Test invalid command
        JsonObject invalidCommand = new JsonObject().put("command", "Not a command");
        assertThrows(TelepathyCommandException.class, () -> { 
            gameClient.execute(serverData, invalidCommand);
        });

        // Test unused TelpathyCommand
        JsonObject unusedValidCommand = new JsonObject().put("command", TelepathyCommands.INVALIDCOMMAND.toString());
        assertDoesNotThrow(() -> {
            gameClient.execute(serverData, unusedValidCommand);
        });
    }
    
}
