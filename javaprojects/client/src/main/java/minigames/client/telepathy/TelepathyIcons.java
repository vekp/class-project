package minigames.client.telepathy;

import minigames.utilities.MinigameUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.awt.Point;


/**
 * A class of methods used to load and handle implementation of UI icons.
 */

public class TelepathyIcons{

    // Logger
   private static final Logger logger = LogManager.getLogger(TelepathyIcons.class);

   private static final File path = new File("src/main/resources/telepathyicons");


   /**
    * A method to load and sort icons from the Resources Directory
    */
    
    public static File[] loadIcons(File path) {

        // method follows documentation from https://www.tutorialspoint.com/how-to-list-all-files-only-from-a-directory-using-java  
       
        File[] list = null;

        FileFilter fileFilter = new FileFilter(){
            public boolean accept(File dir){
                if (dir.isFile()){
                    return true;
                }else{
                    return false;
                }
            }
        };

        try{
            list = path.listFiles(fileFilter);
            Arrays.sort(list);
        } catch (NullPointerException e){
            logger.error("Image loading failed: {} ", e);
        } finally {
            return list;
        }

    }


    /**
    * A method to add all icons to an ArrayList
    * @return iconList ArrayList
    */
     public static ArrayList<ImageIcon> allIcons() {
        
        ArrayList<ImageIcon> iconList = new ArrayList<>();
        File[] list = loadIcons(path);

        for(File fileName: list){
            // utilises team Intelligits scaledImage method
            ImageIcon icon = MinigameUtils.scaledImage(fileName.toString(), 40);
            iconList.add(icon);
        }
        
        return iconList;

     }


    /**
    * A method to create a list of all coordinates on the board; utilises Point class.
    * @return coordinates ArrayList
    */


    public static ArrayList<Point> allCoordinates(JButton[][] buttonGrid){

        ArrayList<Point> coordinates = new ArrayList<>();

        for (int row = 0; row < buttonGrid.length; row++) {
            for (int col = 0; col < buttonGrid.length; col++) {
                coordinates.add(new Point(col, row));
            }
        }
        return coordinates;

    }


    /**
    * A method that maps ordered icons to coordinates on the board; utilises Point class
    * @return mappedIcons Map<Point, ImageIcon>
    */

    public static Map<Point, ImageIcon> mappedIcons(ArrayList<Point> coordinates, ArrayList<ImageIcon> icons){

        Map<Point, ImageIcon> mappedIcons = new HashMap<>();

        for (int i = 0; i < icons.size(); i++){
            mappedIcons.put(coordinates.get(i), icons.get(i));
        }

    return mappedIcons;

    }

}













    





    













































