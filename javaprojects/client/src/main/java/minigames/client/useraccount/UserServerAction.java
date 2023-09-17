package minigames.client.useraccount;

import minigames.client.MinigameNetworkClient;
import io.vertx.core.Future;


// outlines the structure of the data in the JSON

public class UserServerAction {
    
    MinigameNetworkClient client;
    String userName;

    // constructor
    public UserServerAction(MinigameNetworkClient userClient, String user) {
        this.client = userClient;
        this.userName = user;
    }

    // sends username to server, receives username back from server as a reference to the updated player variable server side.
    public void userNameSend() {
        this.client.login(this.userName);
    }

    // get username from the server
    public String getUserName() {
       return this.userName;
    }
}