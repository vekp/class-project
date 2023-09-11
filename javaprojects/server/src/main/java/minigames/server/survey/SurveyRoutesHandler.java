package minigames.server;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class SurveyRoutesHandler {
    public void setupRoutes(Router router) {
        router.post("/sendSurveyData").handler(this::handleSendSurveyData);
        router.get("/surveyData").handler(this::getAllSurveyData);
    }

    // Save Submission from Survey
    private void handleSendSurveyData(RoutingContext ctx) {
        JsonObject jsonData = ctx.getBodyAsJson();
        SurveyDatabaseHandler databaseHandler = new SurveyDatabaseHandler();

        if (jsonData != null) {
            databaseHandler.saveToSurveyDatabase(jsonData);
            ctx.response().end("JSON data received and logged.");
        } else {
            ctx.response()
                .setStatusCode(400)
                .end("Invalid JSON data.");
        }
    }

    // Gets submissions data from survey submissions
    private void getAllSurveyData(RoutingContext ctx) {
        SurveyDatabaseHandler databaseHandler = new SurveyDatabaseHandler();
        String jsonData = databaseHandler.getFeedbackData().toJSONString();
        
        if (jsonData != null) {          
            // String jsonData = feedbackData.toJSONString();      
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(jsonData);
        } else {
            ctx.response()
               .setStatusCode(400) 
               .end("No data read from the file.");
        }
    };
}
