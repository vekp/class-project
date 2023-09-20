package minigames.server.krumgame;

import io.vertx.core.Future;
import minigames.achievements.Achievement;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.MinigameNetworkServer;
import minigames.server.achievements.AchievementHandler;

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

        // Register achievements
        AchievementHandler achievementHandler = new AchievementHandler(KrumGameServer.class);
        achievementHandler.registerAchievement(new Achievement("Long Range Bazooka", "Score a direct hit with the bazooka from more than 500 pixels away", 25, "", false));
        achievementHandler.registerAchievement(new Achievement("Grenade Direct Hit", "Hurt your opponent with a grenade that never bounced", 25, "", false));
        achievementHandler.registerAchievement(new Achievement("Airshot!", "Score a direct hit on an opponent while they're flying through the air", 100, "", true));
        achievementHandler.registerAchievement(new Achievement("Joey Suicide", "Kill yourself with your own joey", 25, "", false));
        achievementHandler.registerAchievement(new Achievement("Death Ray", "Kill an opponent with the laser cannon", 25, "", false));
        achievementHandler.registerAchievement(new Achievement("Tarzan Kill", "Kill an opponent with a projectile you fired while swinging on a rope", 25, "", false));
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
            return new GameMetadata("KrumGame", name, games.get(name).getPlayerNames(), games.get(name).joinable);
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
