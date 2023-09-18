package minigames.client.useraccount;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;

// mostly used to add the new session and also update all old sessions to false

public class FileManager {
    private ObjectMapper objectMapper;
    private List<Session> sessions;
    private String jsonFile;

    public FileManager(String path, String jsonFile) {
        this.jsonFile = jsonFile;
        this.objectMapper = new ObjectMapper();
        this.sessions = parseSessionFromJson(this.jsonFile);
    }

    // adds a new session to the session array in the JSON 
    public void addSession() {
        // Set isActive to false for all existing sessions
        for (Session session : sessions) {
            session.setInActive();
        }
        // Create and add a new session this one should be true as it is the active session
        Session session = new Session(true);
        this.sessions.add(session);
    }

    // returns the session array from the json file
    private List<Session> parseSessionFromJson(String json) {
        try {
            UserData userData = objectMapper.readValue(json, TypeFactory.defaultInstance().constructType(UserData.class));
            List<Session> sessionList = userData.getSession();
            return sessionList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void updateGame (String gameName, String value) {
        // add the game to the active session
        for (Session session : sessions){
            boolean isActive = session.getIsActive();
            if (session.getIsActive() == true){     
                session.addGame(gameName, value);
            }
        }
    }


    // move the try catch into it's own method away from the constructor
    public String sessionListToJson() {
        try {
            return objectMapper.writeValueAsString(this.sessions);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    } 
