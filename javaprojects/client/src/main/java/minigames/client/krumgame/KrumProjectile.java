package minigames.client.krumgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.awt.image.WritableRaster;


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
    KrumProjectile(int xpos, int ypos, double xvel, double yvel, BufferedImage sprite, WritableRaster ground) {
        this.x = xpos;
        this.y = ypos;
        this.xvel = xvel;
        this.yvel = yvel;
        exploding = false;   
        knockbackDistance = 40;
        knockbackPower = 5;
        this.sprite = sprite;
        radius = KrumC.PROJ_RADIUS;
        this.ground = ground;
        explosionRadius = 20;
    }
    void draw(Graphics2D g) {
        g.drawImage(sprite, null, (int)x - radius, (int)y - radius);
    }
    void update(double windX, double windY) {
        xvel += windX;
        yvel += windY;
        yvel += KrumC.GRAVITY;
        xvel *= KrumC.AIR_RES_FACTOR;
        yvel *= KrumC.AIR_RES_FACTOR;
        this.x += xvel;
        this.y += yvel;
    }
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
    int playerCollisionCheck(KrumPlayer players[]){
        int n = -1;
        double z[] = {0};
        for (KrumPlayer p : players) {            
            n++;
            if (System.nanoTime() - p.lastShotTime < KrumC.SHOT_INVULNERABILITY_TIME) continue;
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
