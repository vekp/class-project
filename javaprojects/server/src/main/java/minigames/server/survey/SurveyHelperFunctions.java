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

        for (String key : averageRatings.fieldNames()) {
            double averageValue = averageRatings.getDouble(key) / totalCount;
            averageRatings.put(key, Double.parseDouble(decimalFormat.format(averageValue)));
        }

        return averageRatings;
    }
}
