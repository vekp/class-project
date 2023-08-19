package minigames.client.krumgame;

import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class KrumGameState {
    ArrayList<KrumPlayerState> playerStates;
    Object pixelMatrix;
    double windX;
    double windY;
    KrumGameState(KrumPlayer[] players, BufferedImage background, double windX, double windY) {
        playerStates = new ArrayList<KrumPlayerState>();
        for (KrumPlayer p : players) {
            playerStates.add(new KrumPlayerState(p));
        }
        pixelMatrix = background.getAlphaRaster().getDataElements(0,0,background.getWidth(),background.getHeight(), null);
        this.windX = windX;
        this.windY = windY;
    }
}
