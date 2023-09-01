package minigames.krumgame;

import java.lang.reflect.Field;

import io.vertx.core.json.JsonObject;

/*
 * The state of all player inputs at an update tick
 */

public class KrumInputFrame {
    public int activePlayer;
    public long frameCount;
    public boolean shoot;
    public long shotPower;
    public double shootAimAngle;
    public boolean shootGrenade;
    public long grenadePower;
    public double grenadeAimAngle;
    public boolean shootRope;
    public double ropeAimAngle;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean detachRope;
    public boolean jump;
    public long jumpPower;
    public int jumpType;
    public boolean enterKeyDown;
    public boolean upArrowKeyDown;
    public boolean downArrowKeyDown;
    public int spriteIndex;
    public double lastAimAngle;
    public boolean facingRight;
    public boolean fireBlowtorch;
    public double blowtorchAimAngle;
    public boolean punch;
    public KrumInputFrame() {

    }
    public KrumInputFrame(JsonObject j) {
        try {
            for (Field f : getClass().getDeclaredFields())
                f.set(this, j.getValue(f.getName()));
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
        }
        
    }
    public JsonObject getJson() {
        JsonObject j = new JsonObject();
        try {
            for (Field f : getClass().getDeclaredFields())
                j.put(f.getName(), f.get(this));           
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
        }
        return j;
    }
}
