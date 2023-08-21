package minigames.client.krumgame;

/*
 * The state of all player inputs at an update tick
 */

public class KrumInputFrame {
    int activePlayer;
    long frameCount;
    boolean shoot;
    long shotPower;
    double shootAimAngle;
    boolean shootGrenade;
    long grenadePower;
    double grenadeAimAngle;
    boolean shootRope;
    double ropeAimAngle;
    boolean leftKeyDown;
    boolean rightKeyDown;
    boolean detachRope;
    boolean jump;
    long jumpPower;
    int jumpType;
    boolean enterKeyDown;
    boolean upArrowKeyDown;
    boolean downArrowKeyDown;
    KrumInputFrame(){

    }
}
