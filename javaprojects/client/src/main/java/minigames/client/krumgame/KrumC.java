package minigames.client.krumgame;


/**
 * Static constants for use in other classes
 */
public class KrumC {

    static final String projectDir = System.getProperty("user.dir");

    static final String imgDir = projectDir + "/src/main/java/minigames/client/krumgame/";

    static final int TARGET_FRAMERATE = 60;
    static final long TARGET_FRAMETIME = 1000000000 / TARGET_FRAMERATE; // nanoseconds

    // pixels less opaque than this are ignored in collision and hit detection (0.0 = transparent, 1.0 = opaque)
    static final double OPACITY_THRESHOLD = 0.7;

    static final double GRAVITY = 0.08; // downward acceleration in pixels per frame
    static final double AIR_RES_FACTOR = 0.99; // movement in air of projectiles and players is multiplied by this factor every frame
    static final double ROPE_RES_FACTOR = 0.95;

    static final double JUMP_ANGLE = Math.PI/3.0; // angle of spacebar jump (flipped in the x-axis when facing left)
    static final double JUMP_ANGLE_TWO = Math.PI/2; // angle of backspace jump

    // player sprite pixels outside this rectangle are ignored in movement collision detection (but not ignored in projectile hit detection)
    // todo: different values for each sprite
    // this one is designed for the kangaroo, so that his tail doesn't get in the way too much
    static final int HITBOX_X_S = 11;
    static final int HITBOX_X_F = 33;
    static final int HITBOX_Y_S = 1;
    static final int HITBOX_Y_F = 37;

    // with these values set to 1 and 6, a player covers 1 horizontal pixel per frame and can climb a maximum of 6 vertical pixels per frame
    static final double WALK_SPEED = 1; // pixels per frame
    static final int WALK_CLIMB = 6; // height climbable in one step

    // projectile starting distance: distance from centre of player sprite (todo: make this origin of gun rather than centre of player) that the projectile spawns
    static final int psd = 10;

    // projectie radius in pixels. todo: different values for different projectiles
    static final int PROJ_RADIUS = 8; 

    // player is invulnerable to its own projectiles for this many nanoseconds
    // used so that projectiles can overlap with the player when spawning (but will still register a hit when you actually do hit yourself)
    static final long SHOT_INVULNERABILITY_TIME = 500000000;

    // dimensions of our drawable area
    static final int RES_X = 800;
    static final int RES_Y = 600;

    static final double ROPE_SPEED = 10;
    static final double ROPE_KEY_ACCEL = 0.06;
    static final double ROPE_LENGTH_SPEED = 1.2;
    //static final double ROPE_LENGTH_ACCEL_FACTOR = 1;

    static final double GRENADE_BOUNCE_FACTOR = 0.95;
}
