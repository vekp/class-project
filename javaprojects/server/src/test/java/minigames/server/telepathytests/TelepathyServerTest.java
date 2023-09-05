package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.vertx.core.Future;
import minigames.rendering.GameMetadata;
import minigames.server.ClientType;
import minigames.server.telepathy.TelepathyServer;
import minigames.commands.CommandPackage;
import minigames.rendering.RenderingPackage;
import minigames.telepathy.TelepathyCommands;

public class TelepathyServerTest {
    @Test
    @DisplayName("Initialisation of TelepathyServer that can be registered to the server")
    public void testTelepathyServer(){
        TelepathyServer server = new TelepathyServer();
        
        // Ensure it will only run on Java swing clients
        ClientType[] supportedClients = server.getSupportedClients();
        assertTrue(supportedClients.length == 1);
        assertTrue(supportedClients[0].equals(ClientType.Swing));
    
        // Check server starts correctly with no games in progress
        assertTrue(server.getGamesInProgress().length == 0);
    }

    @Test
    @DisplayName("Test getting current games in progress")
    public void testGetGamesInProgress(){
        TelepathyServer server = new TelepathyServer();

        // Check no games are running
        assertTrue(server.getGamesInProgress().length == 0);

        // Check a new game is running
        server.newGame("Bob");
        GameMetadata[] games = server.getGamesInProgress();
        assertTrue(games.length == 1);

        // Check that when a player leaves and game is empty, game is deleted
        JsonObject json = new JsonObject().put("command", TelepathyCommands.QUIT.toString());
        GameMetadata gameData = server.getGamesInProgress()[0];
        CommandPackage command = new CommandPackage(gameData.gameServer(), gameData.name(), "Bob",
                Collections.singletonList(json));
        server.callGame(command);
        
        // Game should now be empty so will be deleted
        assertTrue(server.getGamesInProgress().length == 0);
    }

    @Test
    @DisplayName("New Telepathy game creation")
    public void testNewGame(){
        TelepathyServer server = new TelepathyServer();

        server.newGame("Bob");
        
        assertTrue(server.getGamesInProgress().length == 1);
        assertTrue(server.getGamesInProgress()[0].name().equals("BobTelepathyGame"));
    }

    @Test 
    @DisplayName("Player joining specific Telepathy game on server")
    public void testServerJoinGame(){
        TelepathyServer server = new TelepathyServer();
        server.newGame("Bob");
        server.joinGame("BobTelepathyGame", "Alice");

        // Assert that Alice is in Bob's game
        GameMetadata[] games = server.getGamesInProgress();
        boolean inGame = false;
        
        for(GameMetadata g : games){
            System.out.println(g.players()[0] + g.players()[1]);
            for(String name : g.players()){
                if(name.equals("Alice")) inGame = true;
            }
        }
        assertTrue(inGame);
    }

    @Test
    @DisplayName("Telepathy client/server communication method")
    public void testCallGame() {
        // This test only checks a single command to verify that callGame() actually works
        // More in depth testing of each command response is done in TelepathyGameTest

        TelepathyServer server = new TelepathyServer();
        server.newGame("Bob");
        GameMetadata gameData = server.getGamesInProgress()[0];
        
        // Send a test CommandPackage to the server to verify an INVALIDCOMMAND response is sent back        
        CommandPackage command = new CommandPackage(gameData.gameServer(), gameData.name(), "Bob",
                Collections.singletonList(new JsonObject().put("command", TelepathyCommands.TESTCOMMAND.toString())));
        server.callGame(command).onSuccess((r) -> {
            String response = r.renderingCommands().get(0).getString("command");
            assertTrue(response.equals(TelepathyCommands.INVALIDCOMMAND.toString()));
        });
    }    
}
