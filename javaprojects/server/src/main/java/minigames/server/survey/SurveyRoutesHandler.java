package minigames.server;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.*;

import java.util.ArrayList;
import java.util.List;

public class SurveyRoutesHandler {
    //Establish connection to DB
    private MongoDB mongoDB = new MongoDB();

    public void setupRoutes(Router router) {
        router.post("/survey/sendSurveyData").handler(this::handleSendSurveyData);
        router.get("/survey/tableData").handler(this::getAllSurveyData);
        router.post("/survey/registerGame").handler(this::registerGame);
        router.post("/survey/getSummary").handler(this::getSurveySummary);
    }

    // Save Submission from Survey
    private void handleSendSurveyData(RoutingContext ctx) {
        JsonObject jsonData = ctx.getBodyAsJson();
    
        if (jsonData != null) {
            String gameIdStr = jsonData.getString("game_id");
            ObjectId gameId = new ObjectId(gameIdStr);
            
            Document document = Document.parse(jsonData.encode());
            document.put("game_id", gameId);
    
            // Insert the Document into MongoDB
            String confirmation = mongoDB.insertDocument("feedback", document);
    
            ctx.response().putHeader("content-type", "application/json")
                    .end(confirmation);
        } else {
            ctx.response()
                    .setStatusCode(400)
                    .end("Invalid JSON data.");
        }
    }

    // Register new Game for Survey Responses
    private void registerGame(RoutingContext ctx) {
        JsonObject jsonData = ctx.getBodyAsJson();

        String nameParamExpected = "game_name";
        String conflictGameName = jsonData.getString(nameParamExpected);
        String conflictGameID = "";

        JSONArray gameArray = mongoDB.getAllDocuments("games");

        // Check Params
        Boolean isParamsValid = jsonData != null && jsonData.size() == 1 && jsonData.containsKey("game_name");
        if (!isParamsValid) {
            ctx.response()
                .setStatusCode(400)
                .end("Sorry, only one parameter named 'game_name' is accepted.");
            return;
        }

        Document document = Document.parse(jsonData.encode());
        // Check if games collection does not contain nameParamExpected = "Muddle"
        boolean gameNameNotMuddle = true;
        for (Object gameObj : gameArray) {
            if (gameObj instanceof JSONObject) {
                JSONObject gameJson = (JSONObject) gameObj;
                Object gameName = gameJson.get(nameParamExpected);
                conflictGameID = gameJson.get("_id").toString();
                if (gameName != null && gameName.equals(conflictGameName)) {
                    gameNameNotMuddle = false;
                    break; 
                }
            }
        }
    
        if (gameNameNotMuddle) {
            String confirmation = mongoDB.insertDocument("games", document);
            ctx.response().putHeader("content-type", "application/json")
                .end(confirmation);
        } else {
            ctx.response()
                .setStatusCode(400)
                .end("A game with the name '" + conflictGameName + "' already Exists with ID: " + conflictGameID + ".");
        }
    }

    // Gets submissions data from survey submissions
    private void getAllSurveyData(RoutingContext ctx) {
        String tableName = ctx.request().getParam("table");
        String dataTest = mongoDB.getAllDocuments(tableName).toJSONString();
        
        if (dataTest != null) {          
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(dataTest);
        } else {
            ctx.response()
               .setStatusCode(400) 
               .end("No data read from the file. Error: "+ dataTest);
        }
    };

    /*
     * Function to return a summary of all survey responses for a specific game ID
     */
    private void getSurveySummary(RoutingContext ctx) {
        JsonObject jsonData = ctx.getBodyAsJson();
    
        if (jsonData != null) {
            String gameId = jsonData.getString("game_id");

            String dataTest = mongoDB.getAllDocumentsByGameId("feedback", gameId).toJSONString();

            if (dataTest != null) {
                JsonObject averageRatings = SurveyHelperFunctions.calculateAverageRatings(dataTest);
                JsonObject summarisedFeedback = SurveyHelperFunctions.getFeedbackText(dataTest);

                JsonObject combinedData = new JsonObject();
                combinedData.mergeIn(averageRatings);
                combinedData.mergeIn(summarisedFeedback);

                ctx.response()
                    .putHeader("content-type", "application/json")
                    .end(combinedData.toString());
            } else {
                ctx.response()
                .setStatusCode(400) 
                .end("No data read from the file. Error: " + dataTest);
            }
        } else {
            ctx.response()
                    .setStatusCode(400)
                    .end("Invalid JSON data.");
        }        
    };
}
