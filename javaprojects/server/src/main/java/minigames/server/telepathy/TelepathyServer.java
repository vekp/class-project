package minigames.server.telepathy;

import io.vertx.core.Future;
import minigames.rendering.GameServerDetails;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;
import minigames.commands.CommandPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;

import java.util.HashMap;

/**
 * The Telepathy Server containing any Telepathy games being run on the
 * server.
 */
public class TelepathyServer implements GameServer {

    HashMap<String, TelepathyGame> games = new HashMap<>();

    /**
     * The details about the game to be registed on the minigame server.
     * 
     * @return A new GameServerDetails object with the name and a description
     *         of the game.
     */
    public GameServerDetails getDetails() {
        return new GameServerDetails("Telepathy", "The Telepathy board game on your computer!");
    }

    /**
     * The clients Telepathy is supported on. Currently only Java Swing.
     * 
     * @return Enum showing Telepathy only supports Java Swing (ClientType.Swing).
     */
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing };
    }

    /**
     * Get an array of all the current Telepathy games being played.
     * 
     * @return Array of GameMetaData objects each containing information about
     *         the current Telepathy games being played.
     */
    public GameMetadata[] getGamesInProgress() {
        // TODO Create TelepathyGame class to store data about each game

        String[] tempPlayers = { "Player 1", "Player 2" };
        GameMetadata[] tempData = { new GameMetadata("Telepathy", "test game", tempPlayers, false) };
        return tempData;
    }

    /**
     * Create a new game of Telepathy.
     * 
     * @param playerName: String with name of the player creating the game.
     * 
     * @return Future object where player has joined the new game.
     */
    public Future<RenderingPackage> newGame(String playerName) {
        TelepathyGame game = new TelepathyGame(playerName);
        games.put(game.name, game);
        return Future.succeededFuture(game.joinGame(playerName));
    }

    /**
     * Allow a player to join a game of Telepathy
     * 
     * @param game:   The name of the game the player wishes to join.
     * @param player: The name of the player joining the game.
     * @return Future object on success of player joining to game.
     */
    public Future<RenderingPackage> joinGame(String game, String player) {
        TelepathyGame gameToJoin = games.get(game);
        return Future.succeededFuture(gameToJoin.joinGame(player));
    }

    /**
     * Used by the client to send command packages to the server. Sends the command
     * package through to the TelepathyGame class to decide how to handle the
     * command.
     * 
     * @param command: The CommandPackage object being sent to the server.
     * @return Future object where the command has been successfully run.
     */
    public Future<RenderingPackage> callGame(CommandPackage command) {
        TelepathyGame gameToCall = games.get(command.gameId());
        return Future.succeededFuture(gameToCall.runCommands(command));
    }
}
