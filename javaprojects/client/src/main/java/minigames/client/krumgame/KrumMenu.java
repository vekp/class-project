package minigames.client.krumgame;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class KrumMenu {
    final String[] screenNames = {"title", "achievements", "instructions"};
    BufferedImage activeScreen;
    HashMap<String, BufferedImage> screens;

    public BufferedImage getScreen(String name) {
        return screens.get(name);
    }

    public KrumMenu() {
        screens = new HashMap<String, BufferedImage>();
        for (String n : screenNames) {
            screens.put(n, KrumHelpers.readSprite("TitlePages/" + n + ".png"));
        }
    }   

}
