package minigames.client.achievementui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AchievementCollection {
    private final List<AchievementPresenter> achievements;
    public AchievementCollection (Collection<AchievementPresenter> achievements) {
        this.achievements = new ArrayList<>(achievements);
    }

    //TODO: function/s that return jPanel with details of each achievement in the collection
}
