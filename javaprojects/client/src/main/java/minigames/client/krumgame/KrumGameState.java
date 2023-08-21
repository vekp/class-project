package minigames.client.krumgame;

import java.util.ArrayList;
import java.awt.image.BufferedImage;

/**
 * Class representing the state of a game at one instant
 */
public class KrumGameState {
    ArrayList<KrumPlayerState> playerStates;
    Object pixelMatrix;
    double windX;
    double windY;
    long startTick;
    long endTick;
    KrumGameState(KrumPlayer[] players, BufferedImage background, double windX, double windY, long tick) {
        playerStates = new ArrayList<KrumPlayerState>();
        for (KrumPlayer p : players) {
            playerStates.add(new KrumPlayerState(p));
        }
        pixelMatrix = background.getAlphaRaster().getDataElements(0,0,background.getWidth(),background.getHeight(), null);
        this.windX = windX;
        this.windY = windY;
        this.startTick = tick;
        this.endTick = tick + KrumC.TURN_TIME_LIMIT_FRAMES;
    }
}
