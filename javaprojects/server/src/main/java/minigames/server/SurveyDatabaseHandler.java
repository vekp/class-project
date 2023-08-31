package minigames.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import io.vertx.core.json.JsonObject;

public class SurveyDatabaseHandler {
    private JSONArray feedbackArray = new JSONArray();

    public void saveToSurveyDatabase(JsonObject jsonData) {
        // JSON object to store the feedback data
        JSONObject feedbackObject = new JSONObject();
        feedbackObject.put("user_id", jsonData.getLong("user_id")); // Replace with real user ID
        feedbackObject.put("timestamp", getCurrentTimestamp());
        feedbackObject.put("feedback_text", jsonData.getString("feedback_text"));
        feedbackObject.put("ui_rating", jsonData.getString("ui_rating"));
        feedbackObject.put("enjoyment_rating", jsonData.getString("enjoyment_rating"));
        feedbackObject.put("functionality_rating", jsonData.getString("functionality_rating"));

        feedbackArray.add(feedbackObject);

        // Save JSON object to a local file
        saveFeedbackToJsonFile(feedbackArray);
    }

    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private void saveFeedbackToJsonFile(JSONArray feedbackArray) {
        try (FileWriter fileWriter = new FileWriter("feedback.json", false)) {
            // Append feedback to existing JSON file, otherwise create new one
            fileWriter.write(feedbackArray.toJSONString());
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace(); // Add proper exception handling
        }
    }
}
