package minigames.server;

import org.junit.jupiter.api.*;

import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;
import minigames.server.telepathy.TelepathyGame;
import minigames.server.telepathy.TelepathyServer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import io.vertx.core.json.JsonObject;

/**
 * Tests for the Telepathy server side code
 */
public class TelepathyTests {

    /* Tests for TelepathyGame */
    
    @Test
    @DisplayName("The TelepathyGame constructor should initalise a name and players correctly")
    public void testTelepathyGameInitialisation(){
        String testGameName = "Game 1";
        TelepathyGame game = new TelepathyGame(testGameName);

        assertTrue(game.getName().equals(testGameName));
        TelepathyGame.Player[] gamePlayers = game.getPlayers();
        
        assertTrue(gamePlayers.length == 2);
        assertTrue(gamePlayers[0].name().equals("Empty"));
        assertTrue(gamePlayers[1].name().equals("Empty"));
    }

    @Test
    @DisplayName("Assert GameMetadata is returning correct values")
    public void testTelepathyGameMetadata(){
        String testGameName = "Game 1";
        TelepathyGame game = new TelepathyGame(testGameName);

        GameMetadata metadata = game.telepathyGameMetadata();

        assertTrue(metadata.gameServer().equals("Telepathy"));
        assertTrue(metadata.name().equals(testGameName));

        assertTrue(metadata.players().length == 2);
        // Check that game is joinable to start
        assertTrue(metadata.joinable());
    }

    @Test
    @DisplayName("Assert players can join the game correctly")
    public void testJoinGame(){
        TelepathyGame game = new TelepathyGame("TestGame");

        game.joinGame("Bob");
        assertTrue(game.getPlayers()[0].name().equals("Bob"));

        // Check that another Bob cannot join
        RenderingPackage renderingPackage = game.joinGame("Bob");
        for(JsonObject command : renderingPackage.renderingCommands()){
            System.out.println(command.getString("command"));
            assertTrue(command.getString("command").equals("joinedGameFail"));
            
            TelepathyGame.Player players[] = game.getPlayers();
            assertTrue(players[0].name().equals("Bob"));
            assertTrue(players[1].name().equals("Empty"));
        }

        // Check another player can join a game with a free spot
        renderingPackage = game.joinGame("Alice");
        for(JsonObject command : renderingPackage.renderingCommands()){
            System.out.println(command.getString("command"));
            assertTrue(command.getString("command").equals("joinedGameSuccess"));
            
            TelepathyGame.Player players[] = game.getPlayers();
            assertTrue(players[0].name().equals("Bob"));
            assertTrue(players[1].name().equals("Alice"));
        }

        // Check that a player cannot join a full game
        renderingPackage = game.joinGame("Fred");
        for(JsonObject command : renderingPackage.renderingCommands()){
            assertTrue(command.getString("command").equals("joinedGameFail"));

            TelepathyGame.Player players[] = game.getPlayers();
            assertTrue(players[0].name().equals("Bob"));
            assertTrue(players[1].name().equals("Alice"));
        }
    }

    /* Tests for TelepathyServer */

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
        assertTrue(server.getGamesInProgress()[0].name().equals("Bob's game"));
    }

    @Test 
    @DisplayName("Player joining specific Telepathy game on server")
    public void testServerJoinGame(){
        TelepathyServer server = new TelepathyServer();
        server.newGame("Bob");
        server.joinGame("Bob's game", "Alice");

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

