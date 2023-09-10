package minigames.client.useraccount;
import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Session {
    @JsonProperty("timestamp")
    private String timestamp = new Date().toString();
    
    @JsonProperty("id")
    private String id = UUID.randomUUID().toString();
    
    @JsonProperty("isActive")
    private String isActive = "false";

    // an empty constructor function to allow the object to be created without any parameters.
    public Session(){
    }

    public Session(String isActive){
        this.isActive = isActive;
    }


    public void setInActive() {
        this.isActive = "false";
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


    public String getIsActive() {
        return this.isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String returnSession() {
        String userJson = String.format(" {\"timestamp\": \"%s\", \"id\": \"%s\", \"isActive\": \"%s\"}",
                this.timestamp, this.id, this.isActive);
        return userJson;
    }
}







