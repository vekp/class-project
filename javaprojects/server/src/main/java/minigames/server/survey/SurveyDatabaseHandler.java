package minigames.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import io.vertx.core.json.JsonObject;

public class SurveyDatabaseHandler {
    private JSONArray feedbackArray = new JSONArray();
    private static final String JSON_FILE_PATH = "feedback.json";

    // Saves jsonData object to feedback.json file
    public void saveToSurveyDatabase(JsonObject jsonData) {
        JSONObject feedbackObject = new JSONObject();
        feedbackObject.put("user_id", jsonData.getLong("user_id")); // Replace with real user ID
        feedbackObject.put("timestamp", getCurrentTimestamp());
        feedbackObject.put("feedback_text", jsonData.getString("feedback_text"));
        feedbackObject.put("ui_rating", jsonData.getString("ui_rating"));
        feedbackObject.put("enjoyment_rating", jsonData.getString("enjoyment_rating"));
        feedbackObject.put("functionality_rating", jsonData.getString("functionality_rating"));

        // Read existing JSON data from the file 
        readExistingFeedbackData();

        feedbackArray.add(feedbackObject);

        // Save updated JSON array back to the file
        saveFeedbackToJsonFile();
    }

    public JSONArray getFeedbackData() {
        readExistingFeedbackData();
        return feedbackArray;
    }

    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private void readExistingFeedbackData() {
        try {
            if (Files.exists(Paths.get(JSON_FILE_PATH))) {
                String jsonContent = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                JSONParser jsonParser = new JSONParser();
                Object parsedObject = jsonParser.parse(jsonContent);

                if (parsedObject instanceof JSONArray) {
                    feedbackArray = (JSONArray) parsedObject;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace(); 
        }
    }

    private void saveFeedbackToJsonFile() {
        try (FileWriter fileWriter = new FileWriter(JSON_FILE_PATH, false)) {
            fileWriter.write(feedbackArray.toJSONString());
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
