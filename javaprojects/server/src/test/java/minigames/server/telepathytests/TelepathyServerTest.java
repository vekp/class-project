package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import minigames.rendering.GameMetadata;
import minigames.server.ClientType;
import minigames.server.telepathy.TelepathyServer;

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
    public void testCallGame(){
        // Complete when client/server communication protocol has been decided
    }    
}
