package minigames.server.useraccount;

import io.vertx.core.Future;
import minigames.server.GameServer;
import minigames.server.useraccount.User;

import java.util.HashMap;


/**
 * Holds active User objects.
 */
public class UserServer {

    // stores all current active users in memory
    HashMap<String, User> activeUsers = new HashMap<>();

    public UserServer() {
        this.activeUsers = activeUsers;
    }

    public void addActiveUser(String userName, User user) {
        this.activeUsers.put(userName, user);
    }

}
