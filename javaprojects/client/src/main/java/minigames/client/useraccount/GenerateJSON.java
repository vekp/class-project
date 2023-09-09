package minigames.client.useraccount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;




public class GenerateJSON {
    private static String jsonArrayString;
    private static JSONArray users;
    public GenerateJSON() {
        try{
            this.jsonArrayString = readFileToString();
            this.users = new JSONArray(this.jsonArrayString);
            
        }
        catch(IOException e){
            e.printStackTrace();
        }        
    }

    private static String readFileToString() throws IOException {
        try{
            byte[] fileBytes = Files.readAllBytes(Paths.get("src/main/java/minigames/client/useraccount/user_accounts.json"));
        return new String(fileBytes, StandardCharsets.UTF_8);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }        
    }

    public void addUser(String email){
        this.users.push(email);    
    }

    public List<User> getJSONArray(){
        return this.users.getJSONArray();
    }
}

class User {
    private String username;
    public User(){
        // needs to be empty for the JSON parser to work.
    }
    public User(String username){
        this.username = username;
    }    

    public String getUsername(){
        return this.username;
    }
}



class JSONArray {
    private List<User> users;

    public JSONArray(String jsonArrayString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.users = objectMapper.readValue(jsonArrayString, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isJSONValid(String jsonString) {
        try {
            // Attempt to parse the JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(jsonString);
            return true; // Parsing successful, so it's valid JSON
        } catch (Exception e) {
            // Parsing failed, so it's not valid JSON
            return false;
        }
    }

    public void push(String value) {
        User user = new User(value);
        this.users.add(user);
    }

    public List<User> getJSONArray(){   
            List<User> newUserlist = new ArrayList<User>();
            for(User user : this.users){
                User newUser = new User(
                    user.getUsername()
                );
                newUserlist.add(newUser);
            }
        return newUserlist;
    }

}



