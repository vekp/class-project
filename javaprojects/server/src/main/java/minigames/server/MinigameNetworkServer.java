package minigames.server;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.*;

import minigames.achievements.*;
import minigames.server.achievements.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;


public class MinigameNetworkServer {

    /**
     * A logger for logging output
     */
    private static final Logger logger = LogManager.getLogger(MinigameNetworkServer.class);

    Vertx vertx;
    HttpServer server;
    Router router;

    public MinigameNetworkServer(Vertx vertx) {
        this.vertx = vertx;
        this.server = vertx.createHttpServer();
        this.router = Router.router(vertx);
    }

    /**
     * Starts the server on the given port
     */
    public void start(int port) {
        router.route()
                .handler(CorsHandler.create().allowedMethod(HttpMethod.POST))
                .handler(BodyHandler.create());

        /* Force CORS to respond that everything is ok
        router.options().handler((ctx) -> {
          ctx.response().putHeader("Acces-Control-Allow-Origin", "*");
          ctx.response().end("");
        });*/

        // A basic ping route to check if there is contact
        router.get("/ping").handler((ctx) -> {
            ctx.response().end("pong");
        });

        // Gets the list of game servers for this client mediaFileName
        router.get("/gameServers/:clientType").respond((ctx) -> {
            String clientStr = ctx.pathParam("clientType");
            ClientType ct = ClientType.valueOf(clientStr);
            List<GameServer> servers = Main.gameRegistry.getGamesForPlatform(ct);

            /** Vertx/Jackson should turn this into a JSON list, because we're just outputing a simple List<record> */
            return Future.succeededFuture(servers.stream().map((gs) -> gs.getDetails()).toList());
        });

        // Gets the list of game servers for this client mediaFileName
        router.get("/games/:gameServer").respond((ctx) -> {
            String serverName = ctx.pathParam("gameServer");
            GameServer gs = Main.gameRegistry.getGameServer(serverName);
            GameMetadata[] games = gs.getGamesInProgress();

            /** Vertx/Jackson should turn this into a JSON list, because we're just outputing a simple List<record> */
            return Future.succeededFuture(Arrays.asList(games));
        });

        // Respond to request from AchievementUI client
        router.get("/achievement/:player").handler((ctx) -> {
            String playerID = ctx.pathParam("player");

            List<GameAchievementState> gameStates = new ArrayList<>();
            //we check all the currently registered servers. Their class acts as their Achievement database key.
            //For any servers that have achievements, we need 2 lists, one of achievements the player has unlocked, and
            //one for the remaining (yet to be earned) achievements
            for (GameServer server : Main.gameRegistry.getAllGameServers()) {
                System.out.println("Class is = " + server.getClass());
                AchievementHandler handler = new AchievementHandler(server.getClass());
                List<Achievement> gameAchievements = handler.getAllAchievements();
                if (gameAchievements.size() == 0) continue;

                List<Achievement> unlocked = new ArrayList<>();
                List<Achievement> locked = new ArrayList<>();
                //hidden achievements that have not yet been unlocked should be shown at the END of the list of locked
                //achievements
                List<Achievement> hiddenLocked = new ArrayList<>();
                //the list used depends on whether the player has this achievement or not
                for (Achievement current : gameAchievements) {
                    if (handler.playerHasEarnedAchievement(playerID, current.name())) {
                        unlocked.add(current);
                    } else {
                        //If player hasn't unlocked it, and it's a hidden achievement, we do not send the 'real'
                        //achievement but instead make a dummy 'hidden' achievement - keeps it secret but still shows
                        // the player that there is something to unlock
                        if (current.hidden()) {
                            Achievement hiddenAchievement = new Achievement(current.name(), "This is a secret " +
                                    "achievement, play the game to unlock it", 0, "", true);
                            hiddenLocked.add(hiddenAchievement);
                        } else {
                            locked.add(current);
                        }
                    }
                }
                //once we are done sorting through achievments, add any hidden locked achievements to the end of the
                // locked list to show up at the bottom.
                locked.addAll(hiddenLocked);
                GameAchievementState state = new GameAchievementState(server.getDetails().name(), unlocked, locked);
                gameStates.add(state);
            }
            //prepare a record package to convert to JSON for sending to client
            PlayerAchievementRecord record = new PlayerAchievementRecord(playerID, gameStates);
            ctx.response().end(record.toJSON());
        });


        // Respond to request from game client for achievements
        router.get("/achievement/:player/:gameID").handler((ctx) -> {
            String playerID = ctx.pathParam("player");
            String gameID = ctx.pathParam("gameID");

            //we check all the currently registered servers. Their class acts as their Achievement database key.
            //For any servers that have achievements, we need 2 lists, one of achievements the player has unlocked, and
            //one for the remaining (yet to be earned) achievements
            for (GameServer server : Main.gameRegistry.getAllGameServers()) {
                if (!server.getDetails().name().equals(gameID)) continue;
                System.out.println("Class is = " + server.getClass());
                AchievementHandler handler = new AchievementHandler(server.getClass());
                List<Achievement> gameAchievements = handler.getAllAchievements();
                if (gameAchievements.size() == 0) continue;

                List<Achievement> unlocked = new ArrayList<>();
                List<Achievement> locked = new ArrayList<>();
                //hidden achievements that have not yet been unlocked should be shown at the END of the list of locked
                //achievements
                List<Achievement> hiddenLocked = new ArrayList<>();
                //the list used depends on whether the player has this achievement or not
                for (Achievement current : gameAchievements) {
                    if (handler.playerHasEarnedAchievement(playerID, current.name())) {
                        unlocked.add(current);
                    } else {
                        //If player hasn't unlocked it, and it's a hidden achievement, we do not send the 'real'
                        //achievement but instead make a dummy 'hidden' achievement - keeps it secret but still shows
                        // the player that there is something to unlock
                        if (current.hidden()) {
                            Achievement hiddenAchievement = new Achievement(current.name(), "This is a secret " +
                                    "achievement, play the game to unlock it", 0, "", true);
                            hiddenLocked.add(hiddenAchievement);
                        } else {
                            locked.add(current);
                        }
                    }
                }
                //once we are done sorting through achievments, add any hidden locked achievements to the end of the
                // locked list to show up at the bottom.
                locked.addAll(hiddenLocked);
                GameAchievementState state = new GameAchievementState(server.getDetails().name(), unlocked, locked);
                ctx.response().end(state.toJSON());
            }
        });



        //respond to request to get the most recently unlocked achievements
        router.get("/achievementUnlocks").respond((ctx) -> {
            List<Achievement> recentUnlocks = AchievementHandler.getRecentUnlocks();
            return Future.succeededFuture(recentUnlocks);
//            if(recentUnlocks.size() == 0){
//                //we have no recent unlocks so just send back null/no data
//                ctx.response().end("");
//            } else {
//                StringBuilder sb = new StringBuilder();
//                for (Achievement current : recentUnlocks) {
//                    sb.append("," + current.toJSON());
//                }
//                ctx.response().end(sb.toString());
//            }
        });

        //todo this will need to pull from the user login system when implemented
        router.get("/playerList").handler((ctx) -> {
            StringBuilder sb = new StringBuilder();
            for (String player : Main.players) {
                sb.append(player);
                sb.append(",");
            }
            ctx.response().end(sb.toString());
        });


        // Starts a new game on the server
        router.post("/newGame/:gameServer").respond((ctx) -> {
            String serverName = ctx.pathParam("gameServer");
            GameServer gs = Main.gameRegistry.getGameServer(serverName);

            String playerName = ctx.body().asString();
            //if the player list didn't already have this name, add it
            //todo add to player account feature when implemented
            Main.players.add(playerName);

            /*
             * executeBlocking moves this onto a background thread
             */
            Future<RenderingPackage> resp = vertx.executeBlocking((promise) -> gs.newGame(playerName).onSuccess((r) -> {
                logger.info("package {}", r);
                promise.complete(r);
            }));
            return resp;
        });

        // Starts a new game on the server
        router.post("/joinGame/:gameServer/:game").respond((ctx) -> {
            String serverName = ctx.pathParam("gameServer");
            String gameName = ctx.pathParam("game");
            GameServer gs = Main.gameRegistry.getGameServer(serverName);

            String playerName = ctx.body().asString();

            //if the player list didn't already have this name, add it
            //todo add to player account feature when implemented
            Main.players.add(playerName);
            /*
             * executeBlocking moves this onto a background thread
             */
            Future<RenderingPackage> resp = vertx.executeBlocking((promise) -> gs.joinGame(gameName, playerName).onSuccess((r) -> {
                logger.info("package {}", r);
                promise.complete(r);
            }));
            return resp;
        });

        // Sends a command package to a game on the server
        router.post("/command").respond((ctx) -> {
            JsonObject data = ctx.body().asJsonObject();
            CommandPackage cp = CommandPackage.fromJson(data);

            GameServer gs = Main.gameRegistry.getGameServer(cp.gameServer());

            /*
             * executeBlocking moves this onto a background thread
             */
            Future<RenderingPackage> resp = vertx.executeBlocking((promise) -> gs.callGame(cp).onSuccess((r) -> {
                logger.info("package {}", r);
                promise.complete(r);
            }));
            return resp;
        });

        server.requestHandler(router).listen(port, (http) -> {
            if (http.succeeded()) {
                logger.info("Server started on {}", port);
            } else {
                logger.error("Server failed to start");
            }
        });
    }
}
