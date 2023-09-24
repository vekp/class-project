package minigames.client.useraccount;
import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;


public class Session {
    @JsonProperty("timestamp")
    private String timestamp = new Date().toString();
    
    @JsonProperty("id")
    private String id = UUID.randomUUID().toString();
    
    @JsonProperty("isActive")
    private boolean isActive = false;

    @JsonProperty("games")
    private ArrayList<Game> games = new ArrayList<Game>();

    // an empty constructor function to allow the object to be created without any parameters.
    public Session(){
    }

    public Session(boolean isActive){
        this.isActive = isActive;
    }


    public void setInActive() {
        this.isActive = false;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getID() {
        return this.id;
    }


    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void addGame(String gameName, String value) {
        Game game = new Game(gameName, value);
        this.games.add(game);
    }

    public ArrayList<Game> getGames() {
        return this.games;
    }

    public String returnSession() {
        String userJson = String.format(" {\"timestamp\": \"%s\", \"id\": \"%s\", \"isActive\": \"%s\"}",
                this.timestamp, this.id, this.isActive);
        return userJson;
    }
}







