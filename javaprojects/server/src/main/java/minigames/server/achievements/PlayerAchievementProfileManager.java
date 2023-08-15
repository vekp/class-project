package minigames.server.achievements;

import java.util.HashSet;
import java.util.Set;

//todo merge this with user account profiles when implemented
/** Simple wrapper class to act as a 'database' for player profiles, for achievements only
 * This can be replaced by a proper profile manager when user accounts are implemented
 */
public class PlayerAchievementProfileManager {
    private final Set<PlayerAchievementProfile> playerProfiles = new HashSet<>();


    /**
     * Adds a player to the register.
     * @param playerName the unique name of the player
     * @return true if successful, false if not (player already exists - mostly for testing)
     */
    public boolean addPlayer(String playerName){
        PlayerAchievementProfile profile = new PlayerAchievementProfile(playerName);
        if(!playerProfiles.contains(profile)) {
            playerProfiles.add(profile);
            return true;
        }
        return false;
    }

    /**
     * Get a player profile by name. Null if it doesn't have one
     * @param playerName name of the player to search for
     * @return the player's profile, or null if it isn't there
     */
    public PlayerAchievementProfile getPlayer(String playerName){
        PlayerAchievementProfile testProfile = new PlayerAchievementProfile(playerName);
        for (PlayerAchievementProfile profile : playerProfiles) {
            if(profile.equals(testProfile)) return profile;
        }
        return null;
    }
}
