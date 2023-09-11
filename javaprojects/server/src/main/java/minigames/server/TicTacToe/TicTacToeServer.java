package minigames.server.tictactoe;

import io.vertx.core.Vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

public class TicTacToeServer extends AbstractVerticle {

    // Constants
    private static final int PORT = 8080;

    // Variables
    private TicTacToeGame game;
    private HttpServer server;

    public TicTacToeServer() {
        this.game = new TicTacToeGame("TicTacToe", "Player1");
    }

    @Override
    public void start(Promise<Void> startPromise) {
        initializeServer(startPromise);
    }

    // Initialize the server with routes and start listening
    private void initializeServer(Promise<Void> startPromise) {
        server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        
        // Define the routes
        router.post("/move").handler(this::handleMove);
        router.get("/boardState").handler(this::handleBoardState);

        server.requestHandler(router).listen(PORT, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port " + PORT);
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    // Handler for /move endpoint
    private void handleMove(RoutingContext context) {
        // Get move details from the request body
        JsonObject requestBody = context.getBodyAsJson();
        int row = requestBody.getInteger("row");
        int col = requestBody.getInteger("col");

        // Make the move and get result
        String result = game.makeMove(row, col);

        // Send the response back
        context.response()
               .putHeader("content-type", "application/json")
               .end(new JsonObject().put("result", result).encode());
    }

    // Handler for /boardState endpoint
    private void handleBoardState(RoutingContext context) {
        char[][] board = game.getBoard();
        JsonObject boardState = new JsonObject();
        
        for (int i = 0; i < board.length; i++) {
            boardState.put("row" + i, new JsonObject().put("cols", board[i]));
        }

        // Send the board state as response
        context.response()
               .putHeader("content-type", "application/json")
               .end(boardState.encode());
    }

    // Main method to run the server
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TicTacToeServer());
    }
}
