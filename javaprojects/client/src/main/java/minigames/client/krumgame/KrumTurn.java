package minigames.client.krumgame;

import java.util.ArrayList;

import minigames.krumgame.KrumInputFrame;

import java.awt.image.BufferedImage;

/*
 * Class representing a complete turn (for sending between client and server, 
 * and executing with the same results as the original turn).
 */

public class KrumTurn {
    ArrayList<KrumInputFrame> frames;
    KrumGameState startState;
    KrumGameState endState;
    BufferedImage background;
    KrumTurn(KrumPlayer[] players, BufferedImage background, double windX, double windY, long tick, boolean ending, boolean running, int winner, double waterLevel) {
        frames = new ArrayList<KrumInputFrame>();
        startState = new KrumGameState(players, background, windX, windY, tick, ending, running, winner, waterLevel);
        endState = null;
        this.background = background;
    }
    void addFrame(KrumInputFrame f) {
        frames.add(f);
    }
    void clear() {
        frames.clear();
    }
}
