package minigames.client.useraccount;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.io.IOException;

// A nice big messy class for updating the file

public class FileHandler {
        private String path;
        private String username;
        private String pin;
        private File file;

        FileHandler(String username){
                this.username = username;
                this.path = "src/main/java/minigames/client/useraccount/" + this.username + ".json";
                this.file = new File(this.path);
                this.pin = pin;
        }

    public void generateUser(String username, String pin){
        // split username at @ sign and just return the first part of the string .
            System.out.println("Welcome " + this.username);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file, false))) {
                StringBuilder jsonString = new StringBuilder("{"); // Start the JSON array
                String userJson = String.format("\"username\": \"%s\", \"pin\": \"%s\"", this.username, this.pin);
                String sessionJson = String.format(new Session(true).returnSession());
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


    public ArrayList<String> listOfUsernames() {
           //Creating a File object for directory
           ArrayList<String> files = new ArrayList<String>();
           File directoryPath = new File("src/main/java/minigames/client/useraccount/users/");
           //List of all files and directories
           String contents[] = directoryPath.list();
           System.out.println("List of files and directories in the specified directory:");
           for(int i=0; i<contents.length; i++) {
            files.add(contents[i].split(".json")[0]);
        }
        return files;
    }

    public ArrayList<ArrayList<String>> games(String gameName){
        // two dimensional array of string
        
        ArrayList<ArrayList<String>> games = new ArrayList<ArrayList<String>>();
        File directoryPath = new File("src/main/java/minigames/client/useraccount");
        String contents[] = directoryPath.list();
        ObjectMapper objectMapper = new ObjectMapper();
        if (contents != null) {
            for (String fileName : contents) {
                try {
                    // Construct the full file path
                    File file = new File(directoryPath, fileName);
    
                    // Read JSON data from the file
                    JsonNode rootNode = objectMapper.readTree(file);
    
                    // Check if the JSON structure matches your expected structure
                    if (rootNode.isObject() && rootNode.has("session")) {
                        ArrayNode sessionArray = (ArrayNode) rootNode.get("session");
    
                        // Iterate through sessions to find game names
                        for (JsonNode session : sessionArray) {
                            if (session.has("games")) {
                                ArrayNode gamesArray = (ArrayNode) session.get("games");
                                // Iterate through games to extract game names
                                for (JsonNode game : gamesArray) {
                                    if (game.has("name")) {                                        
                                        String gameNames = game.get("name").asText();
                                        if(gameNames.contains(gameName)){
                                            ArrayList<String> gameInfo = new ArrayList<>();
                                            gameInfo.add(gameName);
                                            gameInfo.add(game.get("score").toString());
                                            gameInfo.add(fileName.split("\\.json")[0]); // Removing ".json" extension
                                            games.add(gameInfo);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return games;
    }

    public void addGame (String gameName, String value) {
        try{
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            String jsonFile = new String(fileBytes, StandardCharsets.UTF_8);
            FileManager fileManager = new FileManager(this.path, jsonFile);
            fileManager.updateGame(gameName, value);
            String sessionJson = fileManager.sessionListToJson();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file, false))){
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
        catch (IOException e){
            System.out.println("User game not added been written to the file.");
            e.printStackTrace();
        }
    }

    public void addSession(File file, String path, String username){        
        try{
            System.out.println("Adding session line 138");
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            String jsonFile = new String(fileBytes, StandardCharsets.UTF_8);
            FileManager fileManager = new FileManager(path, jsonFile);
        fileManager.addSession();
        String sessionJson = fileManager.sessionListToJson();
        System.out.println(sessionJson);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))){
            System.out.println("Adding session line 145");
                StringBuilder jsonString = new StringBuilder("{"); // Start the JSON array
                String userJson = String.format("\"username\": \"%s\", \"pin\": \"%s\"", username, "1234");
                jsonString.append(userJson).append(",\"session\":"); // Add user JSON to the array
                jsonString.append(sessionJson); // Add session JSON to the array
                jsonString.append("}"); // End the JSON array
                System.out.println("User account information has been written to the file.");
                // Write the content to the file
                bufferedWriter.write(jsonString.toString());
        } catch (IOException ex) {
            System.out.println("Adding session line 155");
                ex.printStackTrace();
        }
        }
        catch(IOException e){
            System.out.println("Adding session line 160");
            e.printStackTrace();
        }      
    }
     
    }




