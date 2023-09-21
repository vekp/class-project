package minigames.client;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * This file contains the code for any requests that need to be made to the server for
 * any 'survey' related data.
 * 
 * The purpose of this file is to keep the bulk of the request logic and code out of the
 * MinigameNetworkClient.java making it more readable.
 */
public class SurveyServerRequestService {
    private static final Logger logger = LogManager.getLogger(SurveyServerRequestService.class);

    private final WebClient webClient;
    private final int port;
    private final String host;

    public SurveyServerRequestService(WebClient webClient, int port, String host) {
        this.webClient = webClient;
        this.port = port;
        this.host = host;
    }

    /*
    *   Sends a JSON object of survey responses to the server for saving to a database
    */
    public Future<HttpResponse<Buffer>> sendSurveyData(JsonObject surveyData) {
        return webClient.post(port, host, "/survey/sendSurveyData")
                .sendJson(surveyData)
                .onSuccess((resp) -> {
                    logger.info("Survey data sent successfully.");
                })
                .onFailure((resp) -> {
                    logger.error("Failed to send survey data: {}", resp.getMessage());
                });
    }

    public Future<String> getSurveyResultSummary(String gameId) {
        JsonObject gameIdJson = new JsonObject().put("game_id", gameId);
        return webClient.post(port, host, "/survey/getSummary")
                .sendJson(gameIdJson)
                .onSuccess((resp) -> {
                    logger.info("Survey data received successfully.");
                })
                .map((resp) -> resp.bodyAsString())
                .onFailure((resp) -> {
                    logger.error("Failed to retrieve survey data: {}", resp.getMessage());
                });
    }

    public Future<String> getAllGames() {
        return webClient.get(port, host, "/survey/tableData?table=games")
                .send()
                .onSuccess((resp) -> {
                    logger.info("Survey data received successfully.");
                })
                .map((resp) -> resp.bodyAsString())
                .onFailure((resp) -> {
                    logger.error("Failed to retrieve survey data: {}", resp.getMessage());
                });
    }
}
