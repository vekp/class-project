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
    boolean ending;
    boolean running;
    int winner;
    double waterLevel;
    KrumGameState(KrumPlayer[] players, BufferedImage background, double windX, double windY, long tick, boolean ending, boolean running, int winner, double waterLevel) {
        playerStates = new ArrayList<KrumPlayerState>();
        for (KrumPlayer p : players) {
            playerStates.add(new KrumPlayerState(p));
        }
        pixelMatrix = background.getAlphaRaster().getDataElements(0,0,background.getWidth(),background.getHeight(), null);
        this.windX = windX;
        this.windY = windY;
        this.startTick = tick;
        this.endTick = tick + KrumC.TURN_TIME_LIMIT_FRAMES;
        this.ending = ending;
        this.running = running;
        this.winner = winner;
        this.waterLevel = waterLevel;
    }
}
