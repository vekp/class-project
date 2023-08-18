package minigames.client.krumgame;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class KrumGrenade extends KrumProjectile {
    long explosionTime;
    KrumGrenade(int xpos, int ypos, double xvel, double yvel, int seconds, BufferedImage sprite, WritableRaster ground) {
        super(xpos, ypos, xvel, yvel, sprite, ground);        
        knockbackDistance = 60;
        knockbackPower = 7.5;
        radius = (int)(sprite.getWidth() / 2);        
        explosionRadius = 30;    
        System.out.println(System.nanoTime());
        explosionTime = System.nanoTime() + (long)seconds * 1000000000;    
        System.out.println(explosionTime);
        System.out.println(seconds);
        System.out.println("r: " + radius);
    }
    @Override
    void update(double windX, double windY) {
        yvel += KrumC.GRAVITY;
        xvel *= KrumC.AIR_RES_FACTOR;
        yvel *= KrumC.AIR_RES_FACTOR;
        this.x += xvel;
        this.y += yvel;
        ArrayList<int[]> collisionPoints = new ArrayList<int[]>();
        if (collisionCheck()) {
            int inc = (int)Math.max(xvel, yvel);
            if (inc == 0) inc = 1;
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
                //System.out.println("collision point: " + p[0] + ", " + p[1]);
                xt += p[0];
                yt += p[1];
            }            
            double xav = xt / collisionPoints.size();
            double yav = yt / collisionPoints.size();
            //System.out.println("av: " + xav + ", " + yav + "; " + (xav - x) + ", " + (yav - y));
            double revVelAngle = Math.atan2(yvel, -xvel);
            double bounceAngle = KrumHelpers.angleBetween(xav, yav, (int)x, (int)y);
            //System.out.println("reflecting around " + bounceAngle);
            double velMag = Math.sqrt(xvel * xvel + yvel * yvel) * KrumC.GRENADE_BOUNCE_FACTOR;
            bounceAngle += (bounceAngle - revVelAngle);
            x -= xvel / inc;
            y -= yvel / inc;
            xvel = Math.cos(bounceAngle) * velMag;
            yvel = Math.sin(bounceAngle) * -velMag;
            //System.out.println("ga " + bounceAngle + ", " + velAngle + ", " + (bounceAngle - velAngle));
            //System.out.println("gren: " + x + ", " + y);
        }
    }
    boolean timerCheck() {
        //System.out.println("timertest: " + System.nanoTime());
        return System.nanoTime() >= explosionTime;
    }
}
