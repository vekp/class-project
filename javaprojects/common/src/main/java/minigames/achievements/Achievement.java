package minigames.achievements;

public record Achievement(
        String name, //Achievement ID / Name - Must be unique per game server
        String description, //Description - shown to players (describe how to unlock, or add flavour text)
        String type, //Indicates type of achievement, to determine what images to present to user
        boolean hidden   //Hidden/Secret achievements do not show up in UI lists until unlocked.
) {
}
