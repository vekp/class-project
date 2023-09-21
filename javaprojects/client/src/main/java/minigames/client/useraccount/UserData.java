package minigames.client.useraccount;

import minigames.client.useraccount.Session.*;
import minigames.client.MinigameNetworkClient;

import java.util.List;

// outlines the structure of the data in the JSON

public class UserData {
    private String username;
    private String pin;
    private List<Session> session;
    private UserAccountSchema schema;
    // public MinigameNetworkClient userClient;
    // could have another List of achievements here

    // Getters and setters for username, pin, and session

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<Session> getSession() {
        return this.session;
    }

    public void setSession(List<Session> session) {
        this.session = session;
    }
}