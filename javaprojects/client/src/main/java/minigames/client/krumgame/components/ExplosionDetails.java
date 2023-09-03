package minigames.client.krumgame.components;

import java.lang.Math;
import java.util.ArrayList;
import java.awt.image.WritableRaster;

import minigames.client.krumgame.KrumC;

/**
 * This is ExplosionDetails class. It is used to store 
 * the explosion details.
 * 
 */


public class ExplosionDetails {
    int x;
    int y;
    long endFrame;
    int radius;
    public static ArrayList<ExplosionDetails> explosions = new ArrayList<ExplosionDetails>();
    public ExplosionDetails(int x,int y, long f, int r){
        this.x = x;
        this.y = y;
        this.endFrame = f;
        this.radius = r;
    }


    /**
     * Call this to explode part of the level.
     * 
     * @param x x-coordinate of the centre of the explosion
     * @param y y-coordinate of the centre of the explosion
     */
    public static void explode(int x, int y, int explosionRadius, int resX, int resY, 
        WritableRaster alphaRaster, long updateCount) {

        System.out.println("Ex " + explosionRadius);
        setPixels(alphaRaster, x, y, explosionRadius, resX, resY);
        ExplosionDetails newex = new ExplosionDetails(x, y, updateCount + 10, explosionRadius);
        explosions.add(newex);
        
    }

    public static void setPixels(WritableRaster alphaRaster, int x, int y, int explosionRadius, int resX, int resY){
        double z[] = {0};
        double empty[] = null;
        for (int i = -(explosionRadius); i < explosionRadius; i++) {
            if (i + x >= resX) break;
            if (i + x < 0) continue;
            for (int j = -(explosionRadius); j < explosionRadius; j++) {
                if (j + y < 0) continue;
                if (j + y >= resY) break;
                if (java.lang.Math.sqrt(i * i + j * j) <= explosionRadius) {
                    if (alphaRaster.getPixel(i + x, j + y, empty)[0] != KrumC.INDESTRUCTIBLE_OPACITY)
                        alphaRaster.setPixel(i + x, j + y, z);
                }                    
            }
        }
    }

    public int getXCoords() {
        return x;
    }

    public int  getYCoords() {
        return y;
    }

    public long getEndFrame() {
        return endFrame;
    }

    public int getRadius() {
        return radius;
    }

}
