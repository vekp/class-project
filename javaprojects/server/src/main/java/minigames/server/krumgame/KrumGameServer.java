package minigames.server.krumgame;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.MinigameNetworkServer;

import minigames.server.krumgame.database.TableManager;

import java.util.HashMap;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.Level;

public class KrumGameServer implements GameServer{

    HashMap<String, KrumGame> games = new HashMap<>();
    private KrumDatabase db = new KrumDatabase();
    private TableManager tableManager;

    public KrumGameServer(){
        Configurator.setLevel(LogManager.getLogger(MinigameNetworkServer.class), Level.WARN);

        // Start the database
        db.startDatabase();
        tableManager = db.getTableManager();
    }

    @Override
    public GameServerDetails getDetails(){
        return new GameServerDetails("KrumGame", "Turn based artillery game");
    }

    @Override
    public ClientType[] getSupportedClients(){
        return new ClientType[]{ ClientType.Swing};
    }

    @Override
    public GameMetadata[] getGamesInProgress(){
        return games.keySet().stream().map((name)->{
            return new GameMetadata("KrumGame", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]:: new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName){
        String gameName = NameGenerator.generateName(games.keySet());
        
        KrumGame game = new KrumGame(gameName, tableManager);

        games.put(gameName, game);

        return Future.succeededFuture(game.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName){
        KrumGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp){
        KrumGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }
    
}

