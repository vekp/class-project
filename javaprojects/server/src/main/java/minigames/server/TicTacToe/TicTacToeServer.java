package minigames.server.tictactoe;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;

import java.util.HashMap;

/**
 * The TicTacToeServer class manages individual games of Tic Tac Toe.
 */
public class TicTacToeServer implements GameServer {

    /** Holds the games in progress in memory (no db) */
    HashMap<String, TicTacToeGame> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("TicTacToe", "Classic 3x3 grid game");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[]{ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx};
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map(gameName -> 
            new GameMetadata("TicTacToe", gameName, games.get(gameName).getPlayerNames(), false)
        ).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        TicTacToeGame game = new TicTacToeGame();
        games.put(playerName, game);  // Using playerName as game key for simplicity
        return Future.succeededFuture(game.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        TicTacToeGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        TicTacToeGame game = games.get(cp.gameId());
        return Future.succeededFuture(game.processCommand(cp));
    }
}
