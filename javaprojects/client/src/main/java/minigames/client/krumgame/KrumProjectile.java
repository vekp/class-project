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
    static final int PROJ_RADIUS = 8;
    static final double GRAVITY = 0.05;
    static final double AIR_RES_FACTOR = 0.99;
    final double OPACITY_THRESHOLD = 0.4;
    KrumProjectile(int xpos, int ypos, double xvel, double yvel) {
        this.x = xpos;
        this.y = ypos;
        this.xvel = xvel;
        this.yvel = yvel;
        exploding = false;   
        File spriteFile = new File("client/src/main/java/minigames/client/krumgame/carrot_s.png");
        //System.out.println(spriteFile.canRead());
        try {
            sprite = ImageIO.read(spriteFile);
        }
        catch (IOException e) {
            System.out.println("error reading sprite image");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("NEW PROJ: " + x + ", " + y + ", vel " + xvel + ", " + yvel);
    }
    void draw(Graphics2D g) {
        g.drawImage(sprite, null, (int)x - PROJ_RADIUS, (int)y - PROJ_RADIUS);
    }
    void update(double windX, double windY) {
        xvel += windX;
        yvel += windY;
        yvel += GRAVITY;
        xvel *= AIR_RES_FACTOR;
        yvel *= AIR_RES_FACTOR;
        this.x += xvel;
        this.y += yvel;
        //System.out.println("px: " + x + ", py: " + y);
    }
    boolean collisionCheck(WritableRaster ground) {
        double z[] = {0};
        //System.out.println(x + ", " + y);
        if (y >= ground.getHeight()) return true;
        for (int i = (int)x - PROJ_RADIUS; i < x + PROJ_RADIUS; i++) {
            if (i < 0) continue;
            if (i >= ground.getWidth()) break;
            int xr = i - (int)x;
            for (int j = (int)y - PROJ_RADIUS; j < y + PROJ_RADIUS; j++) {
                int yr = j - (int)y;                
                if (j < 0) continue;
                if (j >= ground.getHeight()) break;
                if (java.lang.Math.sqrt(xr * xr + yr * yr) <= PROJ_RADIUS) {
                    //System.out.println(ground.getPixel(i, j, z));
                    if(ground.getPixel(i, j, z)[0] > OPACITY_THRESHOLD) {
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
            if (x + PROJ_RADIUS >= p.xpos && x - PROJ_RADIUS <= p.xpos + p.sprite.getWidth() && y + PROJ_RADIUS >= p.ypos && y - PROJ_RADIUS <= p.ypos + p.sprite.getHeight()) {
                for (int i = (int)x - PROJ_RADIUS; i < x + PROJ_RADIUS; i++) {
                    if (i < (int)p.xpos) continue;
                    if (i >= (int)p.xpos + p.sprite.getWidth()) break;
                    int xr = i - (int)x;
                    for (int j = (int)y - PROJ_RADIUS; j < y + PROJ_RADIUS; j++) {
                        int yr = j - (int)y;                
                        if (j < (int)p.ypos) continue;
                        if (j >= (int)p.ypos + p.sprite.getHeight()) break;
                        if (java.lang.Math.sqrt(xr * xr + yr * yr) <= PROJ_RADIUS) {
                            //System.out.println(ground.getPixel(i, j, z));
                            if(p.alphaRaster.getPixel(i - (int)p.xpos, j - (int)p.ypos, z)[0] > OPACITY_THRESHOLD) {
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
