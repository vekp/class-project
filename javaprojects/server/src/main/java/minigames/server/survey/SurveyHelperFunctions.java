package minigames.server;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.DecimalFormat;

public class SurveyHelperFunctions {

    public static JsonObject calculateAverageRatings(String jsonData) {
        JsonArray dataArray = new JsonArray(jsonData);
        JsonObject averageRatings = new JsonObject();
        int totalCount = dataArray.size();
    
        // Create a DecimalFormat object to format the average values
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
    
        for (int i = 0; i < totalCount; i++) {
            JsonObject entry = dataArray.getJsonObject(i);
            for (String key : entry.fieldNames()) {
                if (key.endsWith("_rating")) {
                    double currentValue = entry.getDouble(key);
                    averageRatings.put(key, averageRatings.containsKey(key) ?
                            Double.parseDouble(decimalFormat.format(averageRatings.getDouble(key) + currentValue)) :
                            Double.parseDouble(decimalFormat.format(currentValue)));
                }
            }
        }
    
        // Calculate the overall average rating
        double overallValue = 0.0;
        for (String key : averageRatings.fieldNames()) {
            double averageValue = averageRatings.getDouble(key) / totalCount;
            overallValue += averageValue;
            double averageValueDecimal = Double.parseDouble(decimalFormat.format(averageValue));
            averageRatings.put(key, averageValueDecimal + "/5");
        }
    
        // Calculate the overall average rating and add it to the JsonObject
        double overallAverage = overallValue / averageRatings.fieldNames().size();
        double overallAverageDecimal = Double.parseDouble(decimalFormat.format(overallAverage));
        averageRatings.put("overall_rating", overallAverageDecimal + "/5");
    
        return averageRatings;
    }

    public static JsonObject getFeedbackText(String jsonData) {
        JsonArray dataArray = new JsonArray(jsonData);
        StringBuilder feedbackStringBuilder = new StringBuilder();
    
        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject entry = dataArray.getJsonObject(i);
            String feedbackText = entry.getString("feedback_text");
            feedbackStringBuilder.append((i + 1) + ". ").append(feedbackText).append("\n");
        }
    
        JsonObject resultObject = new JsonObject();
        resultObject.put("feedback", feedbackStringBuilder.toString().trim());
    
        return resultObject;
    }
    
}
