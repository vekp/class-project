package minigames.client.krumgame;

import minigames.client.krumgame.components.Background;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class KrumLevel {
    int p1x;
    int p1y;
    int p2x;
    int p2y;
    Background background;
    KrumLevel(String filename, String maskFilename, int p1x, int p1y, int p2x, int p2y) {
        this.p1x = p1x;
        this.p1y = p1y;
        this.p2x = p2x;
        this.p2y = p2y;
        background = new Background(filename);
        BufferedImage maskImage = null;
        WritableRaster mask = null;
        if (maskFilename != null) {
            maskImage = KrumHelpers.readSprite(maskFilename);
            mask = maskImage.getAlphaRaster();
            if (mask.getWidth() != background.getImage().getWidth() || mask.getHeight() != background.getImage().getHeight()) {
                System.out.println("indestructibility mask doesn't match level size");
                return;
            }
        }
        double[] empty = null;           
        for (int x = 0; x < background.getImage().getWidth(); x++) {
            for (int y = 0; y < background.getImage().getHeight(); y++) {
                if (background.getAlphaRaster().getPixel(x,y,empty)[0] == KrumC.INDESTRUCTIBLE_OPACITY) {
                    background.getAlphaRaster().setPixel(x,y,new double[] {KrumC.DESTRUCTIBLE_OPACITY});
                }
                if (mask != null) {
                    if (mask.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {                        
                        if (background.getAlphaRaster().getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                            background.getAlphaRaster().setPixel(x,y,new double[] {KrumC.INDESTRUCTIBLE_OPACITY});
                        }
                    }
                }
            }
        }        
    }
}
