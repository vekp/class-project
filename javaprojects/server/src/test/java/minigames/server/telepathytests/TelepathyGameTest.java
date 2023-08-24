package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;
import minigames.server.telepathy.TelepathyGame;
import minigames.telepathy.TelepathyCommands;

public class TelepathyGameTest {
    @Test
    @DisplayName("The TelepathyGame constructor should initalise a name and players correctly")
    public void testTelepathyGameInitialisation(){
        String testGameName = "Game 1";
        TelepathyGame game = new TelepathyGame(testGameName);

        assertTrue(game.getName().equals(testGameName));
        TelepathyGame.Player[] gamePlayers = game.getPlayers();
        
        assertTrue(gamePlayers.length == 2);
        assertTrue(gamePlayers[0] == null);
        assertTrue(gamePlayers[1] == null);
    }

    @Test
    @DisplayName("Assert GameMetadata is returning correct values")
    public void testTelepathyGameMetadata(){
        String testGameName = "Game 1";
        TelepathyGame game = new TelepathyGame(testGameName);

        GameMetadata metadata = game.telepathyGameMetadata();

        assertTrue(metadata.gameServer().equals("Telepathy"));
        assertTrue(metadata.name().equals(testGameName));

        assertTrue(metadata.players().length == 0);
        // Check that game is joinable to start
        assertTrue(metadata.joinable());
    }

    @Test
    @DisplayName("Assert players can join the game correctly")
    public void testJoinGame(){
        TelepathyGame game = new TelepathyGame("TestGame");

        // Test joining empty game (when creating new game)
        game.joinGame("Bob");
        assertTrue(game.getPlayers()[0].name().equals("Bob"));

        // Check that another Bob cannot join
        RenderingPackage renderingPackage = game.joinGame("Bob");
        assertTrue((renderingPackage.renderingCommands().get(0).getString("command"))
                .equals(TelepathyCommands.JOINGAMEFAIL.toString()));
        
        TelepathyGame.Player players[] = game.getPlayers();
        assertTrue(players[0].name().equals("Bob"));
        assertTrue(players[1] == null);

        // Check another player can join a game with a free spot
        renderingPackage = game.joinGame("Alice");
        assertTrue((renderingPackage.renderingCommands().get(0).getString("nativeCommand").equals("client.loadClient")));
        assertTrue((renderingPackage.renderingCommands().get(1).getString("command").equals(TelepathyCommands.JOINGAMESUCCESS.toString())));

        players = game.getPlayers();
        assertTrue(players[0].name().equals("Bob"));
        assertTrue(players[1].name().equals("Alice"));

        // Check that a player cannot join a full game
        renderingPackage = game.joinGame("Fred");
        assertTrue((renderingPackage.renderingCommands().get(0).getString("command").equals(TelepathyCommands.JOINGAMEFAIL.toString())));

        players = game.getPlayers();
        assertTrue(players[0].name().equals("Bob"));
        assertTrue(players[1].name().equals("Alice"));
    }
    
}
