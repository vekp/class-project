package minigames.client.krumgame;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/*
 * Functions to be used in more than one class
 */

public class KrumHelpers {
    public static double distanceBetween(double ax, double ay, double bx, double by) {
        return Math.sqrt((ax - bx)*(ax-bx)+(ay-by)*(ay-by));
    }

    /**
     * @return angle in radians of the line from (ax,ay) to (bx,by)
     */
    public static double angleBetween(double ax, double ay, double bx, double by) {
        return Math.atan2(ay - by, bx - ax);
    }
    
    public static double[] addVectors(double dirA, double magA, double dirB, double magB) {        
        double xa = Math.cos(dirA) * magA;
        double ya = Math.sin(dirA) * magA;
        double xb = Math.cos(dirB) * magB;
        double yb = Math.sin(dirB) * magB;
        xa += xb;
        ya += yb;
        double dirR = Math.atan2(ya, xa);
        double magR = Math.sqrt(xa*xa + ya*ya);
        double result[] = {dirR, magR};
        return result;
    }
    /**
     * This method reads an image file if fails to read then
     * exit the program. 
     * 
     * @params: String fileName: image file name
     * @return: BufferedImage
     */
    public static BufferedImage readSprite(String fileName){               
        try{
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream(KrumC.imgDir + fileName);
            return ImageIO.read(is);
        }catch (IOException e){
            System.out.println("error reading sprite image" + KrumC.imgDir + fileName);
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

}
