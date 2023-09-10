package minigames.client.useraccount;
import minigames.client.useraccount.Session.*;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;

// A nice big messy class for updating the file

public class FileHandler {
        private String path;
        private String username;
        private String pin;
    public void generateUser(String username, String pin){
        // split username at @ sign and just return the first part of the string .
        this.username = username.split("@")[0];
        this.pin = pin;
        this.path = "src/main/java/minigames/client/useraccount/" + this.username + ".json";
        File file = new File(this.path);
            System.out.println("Welcome " + this.username);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
                StringBuilder jsonString = new StringBuilder("{"); // Start the JSON array
                String userJson = String.format("\"username\": \"%s\", \"pin\": \"%s\"", this.username, this.pin);
                String sessionJson = String.format(new Session("true").returnSession());
                jsonString.append(userJson).append(",\"session\":["); // Add user JSON to the array
                jsonString.append(sessionJson); // Add session JSON to the array
                jsonString.append("]}"); // End the JSON array
                System.out.println(jsonString.toString() + ": newContent");
                System.out.println("User account information has been written to the file.");
                // Write the content to the file
                bufferedWriter.write(jsonString.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void addSession(String emailInput, File file, String path, String username){        
        try{
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            String jsonFile = new String(fileBytes, StandardCharsets.UTF_8);
            System.out.println(jsonFile);
            SessionManager sessionManager = new SessionManager(path, jsonFile);
        sessionManager.addSession();
        String sessionJson = sessionManager.sessionListToJson();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))){
                StringBuilder jsonString = new StringBuilder("{"); // Start the JSON array
                String userJson = String.format("\"username\": \"%s\", \"pin\": \"%s\"", username, "1234");
                jsonString.append(userJson).append(",\"session\":"); // Add user JSON to the array
                jsonString.append(sessionJson); // Add session JSON to the array
                jsonString.append("}"); // End the JSON array
                System.out.println("User account information has been written to the file.");
                // Write the content to the file
                bufferedWriter.write(jsonString.toString());
        } catch (IOException ex) {
                ex.printStackTrace();
        }
        }
        catch(IOException e){
            e.printStackTrace();
        }      
    }
     
    }




