package minigames.server.telepathytests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.launcher.Command;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;
import minigames.server.telepathy.TelepathyGame;
import minigames.server.telepathy.Player;
import minigames.telepathy.State;
import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommandHandler;
import minigames.telepathy.TelepathyCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class TelepathyGameTest {
    private String[] playerNames = {"Bob", "Alice", "Fred"};


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
        game.joinGame(playerNames[0]);
        assertTrue(game.getPlayers().get(playerNames[0]).getName().equals(playerNames[0]));

        // Check that another Bob cannot join
        RenderingPackage renderingPackage = game.joinGame("Bob");
        assertTrue((renderingPackage.renderingCommands().get(0).getString("command"))
                .equals(TelepathyCommands.JOINGAMEFAIL.toString()));

        HashMap<String,Player> players = game.getPlayers();
        assertTrue(players.size() == 1);
        assertTrue(players.keySet().contains(playerNames[0]));

        // Check that players with invalid names cannot join
        renderingPackage = game.joinGame(" ");
        assertTrue(
                (renderingPackage.renderingCommands().get(0).getString("command").equals("JOINGAMEFAIL"))
        );

        renderingPackage = game.joinGame(null);
        assertTrue(
            (renderingPackage.renderingCommands().get(0).getString("command").equals("JOINGAMEFAIL"))
        );

        // Check another player can join a game with a free spot
        renderingPackage = game.joinGame(playerNames[1]);
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
    @DisplayName("Test runCommand to ensure invalid commands are handled correctly")
    public void testRunCommand() {
        TelepathyGame game = new TelepathyGame("TestGame");
        game.joinGame(playerNames[0]); // Join game for player response

        // Test an INVALID command
        RenderingPackage response = game.runCommands(
            makeCommandPackage(game.telepathyGameMetadata(), 
            playerNames[0], 
            TelepathyCommands.INVALIDCOMMAND.toString())
        );
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));
        
        // Test empty, commands not defined will throw an exception
        assertThrows(TelepathyCommandException.class, () -> {
            game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[0], " "));
        });
    }

    @Test
    @DisplayName("Test running a REQUESTUPDATE command")
    public void testRequestUpdate(){
        // Set up
        TelepathyGame game = makeTestGame(1);

        // Run the command
        RenderingPackage response = game.runCommands(new CommandPackage(
            game.telepathyGameMetadata().gameServer(), 
            game.telepathyGameMetadata().name(), 
            playerNames[0], 
            Collections.singletonList(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.REQUESTUPDATE))));

        // Updates expected
        //          MODIFYPLAYER from joining game
        System.out.println(response.renderingCommands().get(0).getString("command"));
        assertTrue(response.renderingCommands().get(0).getString("command").equals("MODIFYPLAYER"));
        
    }

    @Test
    @DisplayName("Test running a SYSTEMQUIT command")
    public void testSystemQuit(){
        TelepathyGame game = makeTestGame(1);


        CommandPackage cp = makeCommandPackage(game.telepathyGameMetadata(), playerNames[0],
                TelepathyCommands.SYSTEMQUIT.toString());
        RenderingPackage response = game.runCommands(cp);
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.QUIT.toString()));
        assertTrue(game.telepathyGameMetadata().players().length == 0);
    }

    @Test
    @DisplayName("Test running a QUIT command")
    public void testQuit(){
        TelepathyGame game = makeTestGame(1);

        assertTrue(game.telepathyGameMetadata().players().length == 1);

        CommandPackage cp = makeCommandPackage(game.telepathyGameMetadata(), playerNames[0], TelepathyCommands.QUIT.toString());
        RenderingPackage response = game.runCommands(cp);
        assertTrue(response.renderingCommands().get(0).getString("nativeCommand").equals("client.quitToMGNMenu"));
        assertTrue(game.telepathyGameMetadata().players().length == 0); // Player has been removed from the game
        
    }

    @Test
    @DisplayName("Test running a TOGGLEREADY command")
    public void testToggleReady(){
        TelepathyGame game = makeTestGame(2);
        assertFalse(game.getPlayers().get(playerNames[0]).isReady());

        RenderingPackage response = game.runCommands(new CommandPackage(
            game.telepathyGameMetadata().gameServer(), 
            game.telepathyGameMetadata().name(), 
            playerNames[0],
            Collections.singletonList(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.TOGGLEREADY))));

        // Get the attributes and expected attributes
        ArrayList<String> returnedAttributes = TelepathyCommandHandler.getAttributes(response.renderingCommands().get(0));
        ArrayList<String> expectedAttributes = new ArrayList<>();
        expectedAttributes.add("readyButton");
        expectedAttributes.add("true");
        assertTrue(returnedAttributes.containsAll(expectedAttributes));
        assertTrue(game.getPlayers().get(playerNames[0]).isReady());
    }

    @Test
    @DisplayName("Test running a CHOOSETILE command")
    public void testChooseTile(){

    }

    @Test
    @DisplayName("Test running an ASKQUESTION command")
    public void testAskQuestion(){
        TelepathyGame game = makeTestGame(2);

        CommandPackage cp = makeCommandPackage(game.telepathyGameMetadata(), playerNames[0],
                TelepathyCommands.ASKQUESTION.toString());
        RenderingPackage response = game.runCommands(cp);

        // Will just return INVALIDCOMMAND unless in correct state - tested further in state testing
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));
    }

    @Test
    @DisplayName("TelepathyGame state testing")
    public void testGameState(){
        // Test state transitions and methods that check for state

        // Test INITIALISE state
        TelepathyGame game = new TelepathyGame("test");
        assertTrue(game.getState() == State.INITIALISE);

        game.joinGame(playerNames[0]);
        game.joinGame(playerNames[1]);

        game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[0], TelepathyCommands.TOGGLEREADY.toString()));
        game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[1], TelepathyCommands.TOGGLEREADY.toString()));
        
        // Test RUNNING state

        // Send toggle ready during running state
        RenderingPackage response = game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[0], TelepathyCommands.TOGGLEREADY.toString()));
        assertTrue(response.renderingCommands().get(0).getString("command").equals("INVALIDCOMMAND"));

        // Test GAMEOVER state

        // Game is over if player leaves while game is running
        game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[0], TelepathyCommands.QUIT.toString()));
        assertTrue(game.getState() == State.GAMEOVER);
    }
    
    /* HELPER METHODS */

    /**
     * Make a TelepathyGame for testing purposes. Add the specified number
     * of players to the game using test player names array.
     * 
     * @param playersToAdd: integer value with the number of players to add
     *  to the game.
     * @return TelepathyGame with the specified number of players. 
     */
    private TelepathyGame makeTestGame(int numPlayers){
        TelepathyGame game = new TelepathyGame("TestGame");
        int i = 0;
        while(i < numPlayers && i < playerNames.length){
            game.joinGame(playerNames[i]);
            i++;
        }

        return game;
    }

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
