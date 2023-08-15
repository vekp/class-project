package minigames.client.krumgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;

public class KrumProjectile {
    double x;
    double y;
    double xvel;
    double yvel;
    boolean exploding;
    BufferedImage sprite;
    KrumProjectile(int xpos, int ypos, double xvel, double yvel) {
        this.x = xpos;
        this.y = ypos;
        this.xvel = xvel;
        this.yvel = yvel;
        exploding = false;   
        File spriteFile = new File(KrumC.imgDir + "carrot_s.png");
        try {
            sprite = ImageIO.read(spriteFile);
        }
        catch (IOException e) {
            System.out.println("error reading sprite image");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    void draw(Graphics2D g) {
        g.drawImage(sprite, null, (int)x - KrumC.PROJ_RADIUS, (int)y - KrumC.PROJ_RADIUS);
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
    boolean collisionCheck(WritableRaster ground) {
        double z[] = {0};
        if (y >= ground.getHeight()) return true;
        for (int i = (int)x - KrumC.PROJ_RADIUS; i < x + KrumC.PROJ_RADIUS; i++) {
            if (i < 0) continue;
            if (i >= ground.getWidth()) break;
            int xr = i - (int)x;
            for (int j = (int)y - KrumC.PROJ_RADIUS; j < y + KrumC.PROJ_RADIUS; j++) {
                int yr = j - (int)y;                
                if (j < 0) continue;
                if (j >= ground.getHeight()) break;
                if (java.lang.Math.sqrt(xr * xr + yr * yr) <= KrumC.PROJ_RADIUS) {
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
            if (x + KrumC.PROJ_RADIUS >= p.xpos && x - KrumC.PROJ_RADIUS <= p.xpos + p.sprite.getWidth() && y + KrumC.PROJ_RADIUS >= p.ypos && y - KrumC.PROJ_RADIUS <= p.ypos + p.sprite.getHeight()) {
                for (int i = (int)x - KrumC.PROJ_RADIUS; i < x + KrumC.PROJ_RADIUS; i++) {
                    if (i < (int)p.xpos) continue;
                    if (i >= (int)p.xpos + p.sprite.getWidth()) break;
                    int xr = i - (int)x;
                    for (int j = (int)y - KrumC.PROJ_RADIUS; j < y + KrumC.PROJ_RADIUS; j++) {
                        int yr = j - (int)y;                
                        if (j < (int)p.ypos) continue;
                        if (j >= (int)p.ypos + p.sprite.getHeight()) break;
                        if (java.lang.Math.sqrt(xr * xr + yr * yr) <= KrumC.PROJ_RADIUS) {
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
