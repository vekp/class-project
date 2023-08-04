package achievements;

import javax.net.ssl.HandshakeCompletedListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AchievementRegister {

    //Each game server gets 1 handler, to store and track unlocks for their achievements
    final Map<String, AchievementHandler> handlers = new HashMap<>();

    //get or create a handler for a game server.
    public AchievementHandler getHandler(String gameID) {
        if (!handlers.containsKey(gameID)) {
            handlers.put(gameID, new AchievementHandler(gameID));
        }
        return handlers.get(gameID);
    }

    public ArrayList<AchievementHandler> getAllHandlers() {
        ArrayList<AchievementHandler> result = new ArrayList<>();
        for (String id : handlers.keySet()) {
            result.add(handlers.get(id));
        }
        return result;
    }
}
