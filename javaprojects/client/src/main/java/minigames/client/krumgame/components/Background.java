package minigames.client.krumgame.components;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import minigames.client.krumgame.KrumHelpers;


/**
 * This is Background class. It is used to store 
 * the background image and its alpha raster.
 * 
 */

public class Background{
    private BufferedImage image;
    private WritableRaster alphaRaster;

    public Background(String imageFile){
        initializeBackground(imageFile);
    }

    private void initializeBackground(String imageFile){
        image = KrumHelpers.readSprite(imageFile);
        alphaRaster = image.getAlphaRaster();
    }

    public BufferedImage getImage(){
        return image;
    }

    public WritableRaster getAlphaRaster(){
        return alphaRaster;
    }
}