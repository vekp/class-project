package minigames.client.krumgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.awt.image.WritableRaster;
import javax.sound.sampled.*;

/**
 * This class represents the projectile fired by the bazooka. 
 * Other projectile types extend this class.
 */
public class KrumProjectile {
    double x;
    double y;
    double xvel;
    double yvel;
    boolean exploding;
    BufferedImage sprite;
    double knockbackPower;
    double knockbackDistance;
    int radius;
    WritableRaster ground;
    int explosionRadius;
    int damageRadius;
    int maxDamage;
    int startX;
    int startY;
    boolean onMoon;
    boolean firedFromRope;
    final static long maxPower = KrumC.PROJECTILES_MAX_POWER;
    KrumProjectile(int xpos, int ypos, double xvel, double yvel, BufferedImage sprite, WritableRaster ground, boolean onMoon, boolean firedFromRope) {
        this.x = xpos;
        this.y = ypos;
        this.startX = xpos;
        this.startY = ypos;
        this.xvel = xvel;
        this.yvel = yvel;
        exploding = false;   
        knockbackDistance = 40;
        knockbackPower = 5;
        this.sprite = sprite;
        radius = KrumC.PROJ_RADIUS;
        this.ground = ground;
        explosionRadius = 20;
        maxDamage = 30;
        damageRadius = explosionRadius + 20;
        this.onMoon = onMoon;
        this.firedFromRope = firedFromRope;
    }
    void draw(Graphics2D g) {
        g.drawImage(sprite, null, (int)x - radius, (int)y - radius);
    }
    void update(double windX, double windY) {
        xvel += windX;
        yvel += windY;
        yvel += onMoon ? KrumC.MOON_GRAVITY : KrumC.GRAVITY;
        xvel *= onMoon ? KrumC.MOON_AIR_RES_FACTOR : KrumC.AIR_RES_FACTOR;
        yvel *= onMoon ? KrumC.MOON_AIR_RES_FACTOR : KrumC.AIR_RES_FACTOR;
        this.x += xvel;
        this.y += yvel;
    }
    /**
     * tests for collision with level
     * @return true if collision
     */
    boolean collisionCheck() {
        double z[] = {0};
        if (y >= ground.getHeight()) return true;
        for (int i = (int)x - radius; i < x + radius; i++) {
            if (i < 0) continue;
            if (i >= ground.getWidth()) break;
            int xr = i - (int)x;
            for (int j = (int)y - radius; j < y + radius; j++) {
                int yr = j - (int)y;                
                if (j < 0) continue;
                if (j >= ground.getHeight()) break;
                if (java.lang.Math.sqrt(xr * xr + yr * yr) <= radius) {
                    if(ground.getPixel(i, j, z)[0] > KrumC.OPACITY_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    double[] centre() {
        return new double[] {x, y};
    }

    /**
     * Tests for collision between projectile and player
     * @param   players
     * @return  index of player collided with, or -1 if no collision
     */
    int playerCollisionCheck(KrumPlayer players[], int shooter){
        int n = -1;
        double z[] = {0};
        for (KrumPlayer p : players) {            
            n++;
            //if (System.nanoTime() - p.lastShotTime < KrumC.SHOT_INVULNERABILITY_TIME) continue;
            if (p.playerIndex == shooter) continue;
            if (x + radius >= p.xpos && x - radius <= p.xpos + p.sprite.getWidth() && y + radius >= p.ypos && y - radius <= p.ypos + p.sprite.getHeight()) {
                for (int i = (int)x - radius; i < x + radius; i++) {
                    if (i < (int)p.xpos) continue;
                    if (i >= (int)p.xpos + p.sprite.getWidth()) break;
                    int xr = i - (int)x;
                    for (int j = (int)y - radius; j < y + radius; j++) {
                        int yr = j - (int)y;                
                        if (j < (int)p.ypos) continue;
                        if (j >= (int)p.ypos + p.sprite.getHeight()) break;
                        if (java.lang.Math.sqrt(xr * xr + yr * yr) <= radius) {
                            if(p.alphaRaster.getPixel(i - (int)p.xpos, j - (int)p.ypos, z)[0] > KrumC.OPACITY_THRESHOLD) {
                                return n;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }
}
