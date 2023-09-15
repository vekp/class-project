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
        TelepathyGame game = makeTestGame(1);
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
        TelepathyGame game = makeTestGame(1);

        RenderingPackage response = game.runCommands(makeCommandPackage(
            game.telepathyGameMetadata(),
            playerNames[0],
            TelepathyCommands.CHOOSETILE.toString()));

        // Will return an INVALIDCOMMANd as not in correct state for choosing tiles
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));
        ArrayList<String> attributes = TelepathyCommandHandler.getAttributes(response.renderingCommands().get(0));
        assertTrue(attributes.get(0).equals(
            "ERROR: Attempting to choose Tile while not in " + State.TILESELECTION + " state."
        ));
    }

    @Test
    @DisplayName("Test running an ASKQUESTION command")
    public void testAskQuestion(){
        TelepathyGame game = makeTestGame(2);

        CommandPackage cp = makeCommandPackage(game.telepathyGameMetadata(), playerNames[0],
                TelepathyCommands.ASKQUESTION.toString());
        RenderingPackage response = game.runCommands(cp);

        // Will just return INVALIDCOMMAND unless in correct state
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));

        // Check correct player turn - can only ask questions on your turn
        game = progressGameToState(State.RUNNING);

        
        String player = game.getCurrentPlayerTurn();
        cp = makeCommandPackage(game.telepathyGameMetadata(), 
            player, 
            TelepathyCommands.ASKQUESTION, "1", "1");
        response = game.runCommands(cp);

        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.BUTTONUPDATE.toString()));
        
        // Check that turns have switched
        assertTrue(!game.getCurrentPlayerTurn().equals(player));

        // Check that same player cannot take another turn
        response = game.runCommands(cp);
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));
    }

    @Test
    @DisplayName("Test running a FINALGUESS command")
    public void testFinalQuestion(){
        TelepathyGame game = progressGameToState(State.RUNNING);
        assertTrue(game.getState() == State.RUNNING);
        // TODO Do proper checks for correct/incorrect guesses
        // Check a correct FINALGUESS / both players have to send one as turn start is random
        RenderingPackage response = game.runCommands(makeCommandPackage(
            game.telepathyGameMetadata(), 
            playerNames[0],
            TelepathyCommands.FINALGUESS, "1", "1"));
        System.out.println("Response from FINALGUESS command: " + response.renderingCommands().toString());
        
        response = game.runCommands(makeCommandPackage(
            game.telepathyGameMetadata(),
            playerNames[1], 
            TelepathyCommands.FINALGUESS, "0", "0"));
        System.out.println("Response from FINALGUESS command: " + response.renderingCommands().toString());
        System.out.println("Game state after final guess: " + game.getState());
        assertTrue(game.getState() == State.GAMEOVER);
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
        assertTrue(game.getState() == State.INITIALISE);
        
        
        game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[1], TelepathyCommands.TOGGLEREADY.toString()));
        assertTrue(game.getState() == State.TILESELECTION);

        // Test TILESELECTION state

        assertTrue(game.getState() == State.TILESELECTION);

        // Make and run a CHOOSETILE command
        CommandPackage selectTile = makeCommandPackage(
            game.telepathyGameMetadata(),
            playerNames[0], 
            TelepathyCommands.CHOOSETILE,
            "0", "0");
        RenderingPackage response = game.runCommands(selectTile);
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.POPUP.toString()));
        assertTrue(response.renderingCommands().get(1).getString("command").equals(TelepathyCommands.BUTTONUPDATE.toString()));

        // Check that chosen tile is the correct tile TOOD
        //game.getPlayers().get(playerNames[0]).getChosenTile();
        
        // Can only choose tile once
        selectTile = makeCommandPackage(
            game.telepathyGameMetadata(),
            playerNames[0],
            TelepathyCommands.CHOOSETILE,
            "1", "2");
        response = game.runCommands(selectTile);
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));
        

        // Players cannot toggle ready state again
        response = game.runCommands(makeCommandPackage(
            game.telepathyGameMetadata(), 
            playerNames[0], 
            TelepathyCommands.TOGGLEREADY.toString()));
        assertTrue(game.getState() == State.TILESELECTION);
        assertTrue(response.renderingCommands().get(0).getString("command").equals(TelepathyCommands.INVALIDCOMMAND.toString()));



        // Test RUNNING state

        // Send toggle ready during running state
        response = game.runCommands(makeCommandPackage(game.telepathyGameMetadata(), playerNames[0], TelepathyCommands.TOGGLEREADY.toString()));
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
     * Create a new TelepathyGame set that has been progressed to the specified
     * game state.
     * @param state: The State the TelepathyGame should be progressed to.
     * @return TelepathyGame at specified state
     */
    private TelepathyGame progressGameToState(State state){
        TelepathyGame game = makeTestGame(2);

        if(state == State.TILESELECTION || state == State.RUNNING){
            game.runCommands(makeCommandPackage(
                game.telepathyGameMetadata(),
                playerNames[0], 
                TelepathyCommands.TOGGLEREADY));
            game.runCommands(makeCommandPackage(
                game.telepathyGameMetadata(),
                playerNames[1],
                TelepathyCommands.TOGGLEREADY));
        }

        if(state == State.RUNNING){
            game.runCommands(makeCommandPackage(
                game.telepathyGameMetadata(), 
                playerNames[0], 
                TelepathyCommands.CHOOSETILE, "0", "0"));
            game.runCommands(makeCommandPackage(
                game.telepathyGameMetadata(), 
                playerNames[1], 
                TelepathyCommands.CHOOSETILE, "1", "1"));
        }
        return game;
    }

    /**
     * Create a CommandPackage with a single command to be used for testing.
     * @param data GameMetadata to apply to the CommandPackage
     * @param playerName Name of the 'player' sending the command
     * @param command The command to send with the CommandPackage
     * @return A new CommandPackage with required data
     */
    private CommandPackage makeCommandPackage(GameMetadata data, String playerName, String command){
        ArrayList<JsonObject> jsonCommands = new ArrayList<>();
        jsonCommands.add(new JsonObject().put("command", command));
        CommandPackage cp = new CommandPackage(data.gameServer(), data.name(), playerName, jsonCommands);

        return cp;
    }

    /**
     * Create a CommandPackage with a single command to be used for testing.
     * @param data GameMetadata to apply to the CommandPackage
     * @param playerName Name of the 'player' sending the command
     * @param command The command to send with the CommandPackage
     * @return A new CommandPackage with required data
     */
    private CommandPackage makeCommandPackage(GameMetadata data, String playerName, TelepathyCommands command, String... attributes){
        ArrayList<JsonObject> jsonCommands = new ArrayList<>();
        jsonCommands.add(TelepathyCommandHandler.makeJsonCommand(command, attributes));
        CommandPackage cp = new CommandPackage(data.gameServer(), data.name(), playerName, jsonCommands);

        return cp;
    }
}
