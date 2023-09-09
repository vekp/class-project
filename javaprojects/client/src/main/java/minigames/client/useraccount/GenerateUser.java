package minigames.client.useraccount;
import minigames.client.useraccount.User.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateUser {
    public void generateUsers(List<User> userList) {
        String fileName = "src/main/java/minigames/client/useraccount/user_accounts.json";
        File file = new File(fileName);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
            StringBuilder jsonString = new StringBuilder("["); // Start the JSON array
            for (User user : userList) {
                // Assuming User has a getUsername() method, adjust the key as needed
                String userJson = String.format("{\"username\": \"%s\"}", user.getUsername());
                jsonString.append(userJson).append(", "); // Add user JSON to the array
            }
            // Remove the trailing ", " after the last element
            if (userList.size() > 0) {
                jsonString.delete(jsonString.length() - 2, jsonString.length());
            }
            jsonString.append("]"); // End the JSON array
            
            System.out.println(jsonString.toString() + ": newContent");
            System.out.println("User account information has been written to the file.");
            
            // Write the content to the file
            bufferedWriter.write(jsonString.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
