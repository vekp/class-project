package minigames.client.krumgame;

import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class KrumTurn {
    ArrayList<KrumInputFrame> frames;
    KrumGameState startState;
    KrumGameState endState;
    BufferedImage background;
    KrumTurn(KrumPlayer[] players, BufferedImage background, double windX, double windY) {
        frames = new ArrayList<KrumInputFrame>();
        startState = new KrumGameState(players, background, windX, windY);
        endState = null;
        this.background = background;
    }
    KrumTurn(KrumTurn other) {
        frames = new ArrayList<KrumInputFrame>(other.frames);
        startState = other.startState;
        endState = other.endState;
        background = other.background;
    }
    void addFrame(KrumInputFrame f) {
        frames.add(f);
    }
    void clear() {
        frames.clear();
    }
}
