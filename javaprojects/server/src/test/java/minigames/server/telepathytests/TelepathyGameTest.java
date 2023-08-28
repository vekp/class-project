package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.launcher.Command;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;
import minigames.server.telepathy.TelepathyGame;
import minigames.server.telepathy.Player;
import minigames.telepathy.TelepathyCommands;

import java.util.ArrayList;
import java.util.HashMap;


public class TelepathyGameTest {
    @Test
    @DisplayName("The TelepathyGame constructor should initalise a name and players correctly")
    public void testTelepathyGameInitialisation(){
        String testGameName = "Game 1";
        TelepathyGame game = new TelepathyGame(testGameName);

        assertTrue(game.getName().equals(testGameName));
        HashMap<String,Player> gamePlayers = game.getPlayers();
        
        assertTrue(gamePlayers.size() == 0);
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
    public void testJoinGame() {
        TelepathyGame game = new TelepathyGame("TestGame");

        // Test joining empty game (when creating new game)
        game.joinGame("Bob");
        assertTrue(game.getPlayers().get("Bob").getName().equals("Bob"));

        // Check that another Bob cannot join
        RenderingPackage renderingPackage = game.joinGame("Bob");
        assertTrue((renderingPackage.renderingCommands().get(0).getString("command"))
                .equals(TelepathyCommands.JOINGAMEFAIL.toString()));

        HashMap<String,Player> players = game.getPlayers();
        assertTrue(players.size() == 1);
        assertTrue(players.keySet().contains("Bob"));

        // Check another player can join a game with a free spot
        renderingPackage = game.joinGame("Alice");
        assertTrue(
                (renderingPackage.renderingCommands().get(0).getString("nativeCommand").equals("client.loadClient")));

        players = game.getPlayers();
        assertTrue(players.size() == 2);
        assertTrue(players.keySet().contains("Bob"));
        assertTrue(players.keySet().contains("Alice"));

        // Check that a player cannot join a full game
        renderingPackage = game.joinGame("Fred");
        assertTrue((renderingPackage.renderingCommands().get(0).getString("command")
                .equals(TelepathyCommands.JOINGAMEFAIL.toString())));

        players = game.getPlayers();
        assertTrue(players.size() == 2);
        assertTrue(players.keySet().contains("Bob"));
        assertTrue(players.keySet().contains("Alice"));
    }
    
    @Test
    @DisplayName("Test runCommand to ensure custom commands are handled correctly.")
    public void testRunCommand() {
        TelepathyGame game = new TelepathyGame("TestGame");

        String testPlayerName = "Bob";

        // Test QUIT
        assertTrue(game.telepathyGameMetadata().players().length == 0);

        game.joinGame(testPlayerName); // Join game to test QUIT

        assertTrue(game.telepathyGameMetadata().players().length == 1);

        CommandPackage cp = makeCommandPackage(game.telepathyGameMetadata(), testPlayerName, TelepathyCommands.QUIT.toString());
        RenderingPackage response = game.runCommands(cp);
        assertTrue(response.renderingCommands().get(0).getString("nativeCommand").equals("client.quitToMGNMenu"));
        assertTrue(game.telepathyGameMetadata().players().length == 0); // Player has been removed from the game
        
        // Test SYSTEMQUIT
        game.joinGame(testPlayerName);

        cp = makeCommandPackage(game.telepathyGameMetadata(), testPlayerName,
                TelepathyCommands.SYSTEMQUIT.toString());
        response = game.runCommands(cp);
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.QUIT.toString()));
        assertTrue(game.telepathyGameMetadata().players().length == 0);
        
        // Test empty, invalid commands - expect an INVALIDCOMMAND response
        game.joinGame(testPlayerName);

        cp = makeCommandPackage(game.telepathyGameMetadata(), testPlayerName, " ");
        response = game.runCommands(cp);
        assertTrue(response.renderingCommands().get(0).getString("command")
                .equals(TelepathyCommands.INVALIDCOMMAND.toString()));

    }
    
    /* HELPER METHODS */

    /**
     * Create a CommandPackage with a single command to be used for testing.
     * @param data GameMetadata to apply to the CommandPackage
     * @param playerName Name of the 'player' sending the command
     * @param commandString The command to send with the CommandPackage
     * @return A new CommandPackage with required data
     */
    private CommandPackage makeCommandPackage(GameMetadata data, String playerName, String commandString){
        ArrayList<JsonObject> jsonCommands = new ArrayList<>();
        jsonCommands.add(new JsonObject().put("command", commandString));
        CommandPackage cp = new CommandPackage(data.gameServer(), data.name(), playerName, jsonCommands);

        return cp;
    }
    
}
