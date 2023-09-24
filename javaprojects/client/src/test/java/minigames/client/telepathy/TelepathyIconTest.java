package minigames.client.telepathy;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import minigames.client.telepathy.TelepathyIcons;
import minigames.client.telepathy.Telepathy;
import minigames.utilities.MinigameUtils;


import java.io.File;
import java.awt.Point;

import java.util.*;
import javax.swing.*;


/**
 * Unit tests for the TelepathyIcon client class.
 */
public class TelepathyIconTest {


    @DisplayName ("The loadIcons method throws a NullPointerException")
    @Test
    public void loadIconsDedectsNullPointerException(){

        File path = new File("src/main/resources/images/telepathyicons");
        File[] list = TelepathyIcons.loadIcons(path);


        assertThrows(NullPointerException.class, () -> {Arrays.sort(list);
        });

    }


     @DisplayName ("The loadIcons method returns expected number of files")
     @Test
     public void testLoadIcons(){

        File path = new File("src/main/resources/telepathyicons");
        File[] testList = TelepathyIcons.loadIcons(path);
        
        assertTrue(testList.length == 81);
        

    }




    @DisplayName ("The allIcons ArrayList method adds expected number of ImageIcons")
    @Test
    public void testAllIcons(){

        ArrayList<ImageIcon> allIconsTest = TelepathyIcons.allIcons();

        //TODO: test array values
        //ImageIcon icon1 = MinigameUtils.scaledImage("src/main/resources/telepathyicons/1.1RedClubs.png", 40);
        //ImageIcon icon2 = allIconsTest.get(0);
        
        
        assertTrue(allIconsTest.size() == 81);
        //assertEquals(icon1, icon2);

    }



    @DisplayName ("The allCoordinates ArrayList method adds expected number of Points")
    @Test
    public void testAllCoordinates(){

        //TODO: Test array values

        JButton[][] testButtonGrid = new JButton[9][9];
        ArrayList<Point> allCoordinatesTest = TelepathyIcons.allCoordinates(testButtonGrid);
   
        assertTrue(allCoordinatesTest.size() == 81);

    }


    @DisplayName ("The mappedIcon Map method adds expected number of key-value pairs")
    @Test
    public void testMappedIcons(){

        //TODO: Test map values

        JButton[][] testButtonGrid = new JButton[9][9];
        ArrayList<Point> allCoordinatesTest = TelepathyIcons.allCoordinates(testButtonGrid);
        ArrayList<ImageIcon> allIconsTest = TelepathyIcons.allIcons();

        Map<Point, ImageIcon> mappedIconsTest = new HashMap<>();
        mappedIconsTest = TelepathyIcons.mappedIcons(allCoordinatesTest, allIconsTest);

   
        assertTrue(mappedIconsTest.size() == 81);

    }







}