package minigames.server;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class SurveyRoutesHandler {
    
    private String connectionString = "mongodb+srv://cosc220:WxsFWGOiEuilbtkj@cluster0.sk5iqrw.mongodb.net/?retryWrites=true&w=majority";
    private String databaseName = "une-db";
    private MongoDB mongoDB = new MongoDB(connectionString, databaseName);
    
    public void setupRoutes(Router router) {
        router.post("/sendSurveyData").handler(this::handleSendSurveyData);
        router.get("/surveyData").handler(this::getAllSurveyData);
    }

    // Save Submission from Survey
    private void handleSendSurveyData(RoutingContext ctx) {
        JsonObject jsonData = ctx.getBodyAsJson();
        SurveyDatabaseHandler databaseHandler = new SurveyDatabaseHandler();

        if (jsonData != null) {
            Document document = Document.parse(jsonData.encode());

            // Insert the Document into MongoDB
            String confirmation =  mongoDB.insertDocument("feedback", document);
            // databaseHandler.saveToSurveyDatabase(jsonData);
            ctx.response().putHeader("content-type", "application/json")
            .end(confirmation);
        } else {
            ctx.response()
                .setStatusCode(400)
                .end("Invalid JSON data.");
        }
    }

    // Gets submissions data from survey submissions
    private void getAllSurveyData(RoutingContext ctx) {
        // SurveyDatabaseHandler databaseHandler = new SurveyDatabaseHandler();
        // String jsonData = databaseHandler.getFeedbackData().toJSONString();

        String dataTest = mongoDB.getAllDocuments("feedback").toJSONString();
        
        if (dataTest != null) {          
            // String jsonData = feedbackData.toJSONString();      
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(dataTest);
        } else {
            ctx.response()
               .setStatusCode(400) 
               .end("No data read from the file.");
        }
    };
}
