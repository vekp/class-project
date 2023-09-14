package minigames.client.krumgame;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 * A projectile that bounces off the level, and explodes after its timer runs 
 * out.
 */
public class KrumGrenade extends KrumProjectile {
    final double KNOCKBACK_DISTANCE = 60;
    final double KNOCKBACK_POWER = 7.5;
    final int EXPLOSION_RADIUS = 40;
    final int MAX_DAMAGE = 50;
    long explosionTick; 
    boolean hasBounced = false;
    KrumGrenade(int xpos, int ypos, double xvel, double yvel, int seconds, BufferedImage sprite, WritableRaster ground, long tick) {
        super(xpos, ypos, xvel, yvel, sprite, ground);        
        knockbackDistance = KNOCKBACK_DISTANCE;
        knockbackPower = KNOCKBACK_POWER;
        radius = (int)(sprite.getWidth() / 2);        
        explosionRadius = EXPLOSION_RADIUS;    
        explosionTick = tick + (long)seconds * KrumC.TARGET_FRAMERATE;
        maxDamage = MAX_DAMAGE;
        damageRadius = explosionRadius + 20;
        hasBounced = false;
    }
    @Override
    void update(double windX, double windY) {
        if (collisionCheck()) 
            System.out.println("colliding at start");
        yvel += KrumC.GRAVITY;
        xvel *= KrumC.AIR_RES_FACTOR;
        yvel *= KrumC.AIR_RES_FACTOR;
        this.x += xvel;
        this.y += yvel;
        ArrayList<int[]> collisionPoints = new ArrayList<int[]>();
        if (collisionCheck()) {
            hasBounced = true;
            int inc = Math.abs(Math.max((int)xvel, (int)yvel));
            inc++;
            int i = inc;
            while(collisionCheck() && i > 0) {
                x -= xvel / inc;
                y -= yvel / inc;
                i--;
            }
            x += xvel / inc;
            y += yvel / inc;
            double e[] = {0};
            for (i = (int)x - radius; i <= x + radius; i++) {
                if (i < 0) continue;
                if (i >= ground.getWidth()) break;
                int xr = i - (int)x;
                for (int j = (int)y - radius; j <= y + radius; j++) {
                    int yr = j - (int)y;                
                    if (j < 0) continue;
                    if (j >= ground.getHeight()) break;
                    if (java.lang.Math.sqrt(xr * xr + yr * yr) <= radius) {
                        if(ground.getPixel(i, j, e)[0] > KrumC.OPACITY_THRESHOLD) {
                            collisionPoints.add(new int[]{i, j});
                        }
                    }
                }
            }
            double xt = 0;
            double yt = 0;
            if (collisionPoints.size() == 0) {
                System.out.println("no collision points");                
                return;
            }
            for (int[] p : collisionPoints) {
                xt += p[0];
                yt += p[1];
            }            
            double xav = xt / collisionPoints.size();
            double yav = yt / collisionPoints.size();
            double revVelAngle = Math.atan2(yvel, -xvel);
            double bounceAngle = KrumHelpers.angleBetween(xav, yav, (int)x, (int)y);
            double velMag = Math.sqrt(xvel * xvel + yvel * yvel) * KrumC.GRENADE_BOUNCE_FACTOR;
            bounceAngle += (bounceAngle - revVelAngle);
            KrumSound.playSound("metal");
            x -= xvel / inc;
            y -= yvel / inc;
            xvel = Math.cos(bounceAngle) * velMag;
            yvel = Math.sin(bounceAngle) * -velMag;
        }
    }
    /**
     * 
     * @param tick  Value of updateCount in KrumGame
     * @return      True if it's time to explode
     */
    boolean timerCheck(long tick) {
        return tick >= explosionTick;
    }
}
