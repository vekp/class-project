package minigames.server.krumgame;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.server.ClientType;
import minigames.server.GameServer;

import java.util.HashMap;
import java.util.Random;

public class KrumGameServer implements GameServer{

    HashMap<String, KrumGame> games = new HashMap<>();

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
        
        KrumGame game = new KrumGame(gameName);

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

