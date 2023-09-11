package minigames.client.telepathy;

import minigames.utilities.MinigameUtils;

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
 * A class to load and sort icons as a HashMap
 */



public final class TelepathyIcons{


    /*String[] list = {"RedClubs", "OrangeSpades", "RedHeart", "BlueClubs", "GreenStar", "OrangeClubs", "GreenClubs", "CyanStar", "PinkMoon", 
    "GreenSpades", "OrangeCircle", "RedExclamations", "OrangeHeart", "GreenCircle", "GreenDiamond", "YellowSpade", "YellowHeart", "MagentaMoon",
    "GreyDiamond", "RedHeart", "GreenQuestions", "GreyMoon", "RedHeart", "YellowDiamond", "GreyStar", "GreyHeart", "PinkHeart",
    "MagentaDiamond", "GreyClubs", "YellowSpade", "RedExclamations", "RedCircle", "GreyHeart", "CyanCircle", "MagentaSpade", "OrangeCircle",
    "PinkSpade", "OrangeStar", "RedStar", "OrangeHeart", "CyanClubs", "PinkHeart", "YellowSpade", "YellowExclamations", "BlueSpades",
    "GreenHeart", "GreyDiamonds", "BlueSpades", "MagentaCircle", "BlueSpade", "MagentaCircle", "GreyClubs", "OrangeSpade", "CyanSpade", "OrangeHeart", "BlueHeart",
    "OrangeExclamations", "CyanSpade", "MagentaCircle", "PinkSpade", "GreenCircle", "PinkSpade", "GreenCircle", "BlueCircle", "GreyMoon", "MagentaExclamations", "RedMoon",
    "GreyExclamations", "CyanCircle", "BlueHeart", "RedDiamond", "YellowSpade", "BlueMoon", "CyanCircle", "RedQuestions", "GreyExclamations",
    "GreyMoon", "GreenExclamations", "MagentaDiamond", "GreyStar", "YellowMoon", "MagentaExclamations", "RedClubs", "PinkClubs", "YellowDiamond"}; */

   
    
    public static ArrayList<ImageIcon> loadIcons(){

         ArrayList<ImageIcon> iconList = new ArrayList<>();

         File path = new File("src/main/resources/telepathyicons");

         FileFilter fileFilter = new FileFilter(){
            public boolean accept(File dir){
                if (dir.isFile()){
                    return true;
                }else{
                    return false;
                }
            }
         };

         File[] list = path.listFiles(fileFilter);

         for(File fileName: list){
            ImageIcon icon = MinigameUtils.scaledImage(fileName.toString(), 40);
            iconList.add(icon);

        }

        return iconList;

    }


    public static ArrayList<Point> allCoordinates(JButton[][] buttonGrid){

        ArrayList<Point> coordinates = new ArrayList<>();

        for (int row = 0; row < buttonGrid.length; row++) {
            for (int col = 0; col < buttonGrid.length; col++) {
                coordinates.add(new Point(col, row));
            }
        }
        return coordinates;

    }


    public static Map<Point, ImageIcon> mappedIcons(ArrayList<Point> coordinates, ArrayList<ImageIcon> icons){

        Map<Point, ImageIcon> mappedIcons = new HashMap<>();

        for (int i = 0; i < icons.size(); i++){
            mappedIcons.put(coordinates.get(i), icons.get(i));
        }



    return mappedIcons;

    }

    




}













    





    













































