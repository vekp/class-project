package minigames.client.krumgame.components;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import minigames.client.krumgame.KrumHelpers;


/**
 * This is Background class. It is used to store 
 * the background image and its alpha raster.
 * 
 */

// Using static methods as background image is being 
// processed as alphaRaster in other classes.
public class Background{
    private static BufferedImage image;
    private static WritableRaster alphaRaster;

    public static void initializeBackground(String imageFile){
        image = KrumHelpers.readSprite(imageFile);
        alphaRaster = image.getAlphaRaster();
    }

    public static BufferedImage getImage(){
        return image;
    }

    public static WritableRaster getAlphaRaster(){
        return alphaRaster;
    }
}