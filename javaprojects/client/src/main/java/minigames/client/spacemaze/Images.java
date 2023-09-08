package minigames.client.spacemaze;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton class to provide all client side classes access to images
 *
 * @author Andrew McKenzie
 *
 * @resource This singleton class structure was based off https://www.geeksforgeeks.org/singleton-class-java/
 */
public class Images {

    // Static Singleton variable to restrict this class to one instance
    private static Images imagesInstance = null;

    // Logger
    private static final Logger logger = LogManager.getLogger(Images.class);

    // Chest image
    private static BufferedImage chestImage;

    // Start image
    private static BufferedImage startImage;

    // Wormhole image
    private static BufferedImage wormHoleImage;

    // Player images
    private static BufferedImage playerImageUp;
    private static BufferedImage playerImageDown;
    private static BufferedImage playerImageRight;
    private static BufferedImage playerImageLeft;

    // Wall images
    private static BufferedImage wallImage0;
    private static BufferedImage wallImage1;
    private static BufferedImage wallImage2;
    private static BufferedImage wallImage3;

    // Key images
    private static BufferedImage keyImage0;
    private static BufferedImage keyImage1;

    // Locked exit images
    private static BufferedImage lockedImage0;
    private static BufferedImage lockedImage1;
    private static BufferedImage lockedImage2;

    // Unlocked exit images
    private static BufferedImage unlockedImage0;
    private static BufferedImage unlockedImage1;
    private static BufferedImage unlockedImage2;

    // Bomb images
    private static BufferedImage bombImage0;
    private static BufferedImage bombImage1;
    private static BufferedImage bombImage2;

    //Title Images
    private static BufferedImage titleImage1;
    private static BufferedImage titleImage2;
    private static BufferedImage titleImage3;
    private static BufferedImage titleImage4;
    private static BufferedImage titleImage5;

    // HashMap for storing all the images
    private static HashMap<String, BufferedImage> singleImages;
    private static HashMap<Integer, BufferedImage> unlockedImages;
    private static HashMap<Integer, BufferedImage> lockedImages;
    private static HashMap<String, BufferedImage> playerImages;
    private static HashMap<Integer, BufferedImage> wallImages;
    private static HashMap<Integer, BufferedImage> bombImages;
    private static HashMap<Integer, BufferedImage> keyImages;
    private static HashMap<Integer, BufferedImage> titleImages;

    /**
     * Constructor - private to restrict the object creation
     * to this class itself.
     */
    private Images () {
        loadImages();
    }

    /**
     * Method to create one and only one instance of
     * this class
     * Synchronized keyword to restrict access to a single thread at a time
     * @return the single instance of the Images class
     *
     * Method taken from https://www.geeksforgeeks.org/singleton-class-java/
     */
    public static synchronized Images getInstance() {
        if (imagesInstance == null)
            imagesInstance = new Images();

        return imagesInstance;
    }

    /**
     * Method to load all images, to be called on game set up
     */
    private void loadImages() {

        singleImages = new HashMap<String, BufferedImage>();
        unlockedImages = new HashMap<Integer, BufferedImage>();
        lockedImages = new HashMap<Integer, BufferedImage>();
        playerImages = new HashMap<String, BufferedImage>();
        wallImages = new HashMap<Integer, BufferedImage>();
        bombImages = new HashMap<Integer, BufferedImage>();
        keyImages = new HashMap<Integer, BufferedImage>();
        titleImages = new HashMap<Integer, BufferedImage>();

        try {
            chestImage = ImageIO.read(getClass().getResource("/images/spacemaze/chest1.png"));
            singleImages.put("chestImage", chestImage);

            startImage = ImageIO.read(getClass().getResource("/images/spacemaze/startNoB1.png"));
            singleImages.put("startImage", startImage);

            wormHoleImage = ImageIO.read(getClass().getResource("/images/spacemaze/star1.png"));
            singleImages.put("wormHoleImage", wormHoleImage);

            loadBombImages();
            loadKeyImages();
            loadPlayerImages();
            loadWallImages();
            loadLockedExitImages();
            loadUnlockedExitImages();
            loadTitleImages();

        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to get the size of the private HashMaps
     * @param hashMap name of the HashMap
     * @return Int of the size of the HashMap
     */
    public static int getSize(String hashMap) {

        int size = 0;

        switch(hashMap) {
            case "unlockedImages" -> size = unlockedImages.size();
            case "lockedImages" -> size = lockedImages.size();
            case "keyImages" -> size = keyImages.size();
            case "bombImages" -> size = bombImages.size();
            case "wallImages" -> size = wallImages.size();
        }

        return size;
    }

    /**
     * Method for getting a player image based on direction
     * @param direction A string of the players direction
     * @return The requested buffered image
     */
    public static BufferedImage getPlayerImage(String direction) {

        BufferedImage image = null;

        try {
            image = playerImages.get(direction);
            if (image == null) {
                logger.error("Player Image was not found in HashMap");
            }
        } catch (NullPointerException e) {
            logger.error("Player Images Hashmap is null: {} ", e);
        }
        return image;
    }

    /**
     * Method to load the player images, key is the direction as a string.
     */
    private void loadPlayerImages(){
        try {
            playerImageUp = ImageIO.read(getClass().getResource("/images/spacemaze/spaceShip2aUp.png"));
            playerImageDown = ImageIO.read(getClass().getResource("/images/spacemaze/spaceShip2aDown.png"));
            playerImageRight = ImageIO.read(getClass().getResource("/images/spacemaze/spaceShip2aRight.png"));
            playerImageLeft = ImageIO.read(getClass().getResource("/images/spacemaze/spaceShip2aLeft.png"));
            playerImages.put("Up", playerImageUp);
            playerImages.put("Down", playerImageDown);
            playerImages.put("Right", playerImageRight);
            playerImages.put("Left", playerImageLeft);
        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Getter for a image with multiple variants
     * @param type String for the category of image
     * @param index and Integer for the variant of the image
     * @return the requested buffered image
     */
    public static BufferedImage getImage(String type, Integer index) {

        BufferedImage image = null;

        HashMap<Integer, BufferedImage> imageType = null;

        switch(type) {
            case "unlockedImages" -> imageType = unlockedImages;
            case "lockedImages" -> imageType = lockedImages;
            case "keyImages" -> imageType = keyImages;
            case "bombImages" -> imageType = bombImages;
            case "wallImages" -> imageType = wallImages;
        }

        try {
            image = imageType.get(index);
            if (image == null) {
                logger.error("Image was not found in HashMap");
            }
        } catch (NullPointerException e) {
            logger.error("Hashmap is null: {} ", e);
        }
        return image;
    }

    /**
     * Overloading the getImage method for the images with a single variant only
     * @param type String of the image required
     * @return a bufferedImage of the requested image
     */
    public static BufferedImage getImage(String type) {

        BufferedImage image = null;

        try {
            image = singleImages.get(type);
            if (image == null) {
                logger.error("Image was not found in HashMap");
            }
        } catch (NullPointerException e) {
            logger.error("Hashmap is null: {} ", e);
        }
        return image;
    }

    /**
     * Method to load the bomb images
     */
    private void loadBombImages(){
        try {
            bombImage0 = ImageIO.read(getClass().getResource("/images/spacemaze/bomb1a.png"));
            bombImage1 = ImageIO.read(getClass().getResource("/images/spacemaze/bomb1b.png"));
            bombImage2 = ImageIO.read(getClass().getResource("/images/spacemaze/bomb1c.png"));
            bombImages.put(0, bombImage0);
            bombImages.put(1, bombImage1);
            bombImages.put(2, bombImage2);
        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to load the key images
     */
    private void loadKeyImages(){
        try {
            keyImage0 = ImageIO.read(getClass().getResource("/images/spacemaze/KeyNoB1a.png"));
            keyImage1 = ImageIO.read(getClass().getResource("/images/spacemaze/keyNoB1.png"));
            keyImages.put(0, keyImage0);
            keyImages.put(1, keyImage1);
        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to load the wall images
     */
    private void loadWallImages(){
        try {
            wallImage0 = ImageIO.read(getClass().getResource("/images/spacemaze/asteriodNoB1.png"));
            wallImage1 = ImageIO.read(getClass().getResource("/images/spacemaze/asteriodNoB2.png"));
            wallImage2 = ImageIO.read(getClass().getResource("/images/spacemaze/asteriodNoB3.png"));
            wallImage3 = ImageIO.read(getClass().getResource("/images/spacemaze/asteriodNoB4.png"));
            wallImages.put(0, wallImage0);
            wallImages.put(1, wallImage1);
            wallImages.put(2, wallImage2);
            wallImages.put(3, wallImage3);
        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to load the locked exit images
     */
    private void loadLockedExitImages(){
        try {
            lockedImage0 = ImageIO.read(getClass().getResource("/images/spacemaze/LockedExitNoB1.png"));
            lockedImage1 = ImageIO.read(getClass().getResource("/images/spacemaze/LockedExitNoB2.png"));
            lockedImage2 = ImageIO.read(getClass().getResource("/images/spacemaze/LockedExitNoB3.png"));
            lockedImages.put(0, lockedImage0);
            lockedImages.put(1, lockedImage1);
            lockedImages.put(2, lockedImage2);
        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to load the unlocked exit images
     */
    private void loadUnlockedExitImages(){
        try {
            unlockedImage0 = ImageIO.read(getClass().getResource("/images/spacemaze/UnlockedExitNoB1.png"));
            unlockedImage1 = ImageIO.read(getClass().getResource("/images/spacemaze/UnlockedExitNoB2.png"));
            unlockedImage2 = ImageIO.read(getClass().getResource("/images/spacemaze/UnlockedExitNoB3.png"));
            unlockedImages.put(0, unlockedImage0);
            unlockedImages.put(1, unlockedImage1);
            unlockedImages.put(2, unlockedImage2);
        } catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to load Menu Title Images.
     */
    public void loadTitleImages(){
        try{
            titleImage1 = ImageIO.read(getClass().getResource("/images/spacemaze/Title1.png"));
            titleImage2 = ImageIO.read(getClass().getResource("/images/spacemaze/Title2a.png"));
            titleImage3 = ImageIO.read(getClass().getResource("/images/spacemaze/Title2b.png"));
            titleImage4 = ImageIO.read(getClass().getResource("/images/spacemaze/Title2c.png"));
            titleImage5 = ImageIO.read(getClass().getResource("/images/spacemaze/Title2d.png"));
            titleImages.put(0, titleImage1);
            titleImages.put(1, titleImage2);
            titleImages.put(2, titleImage3);
            titleImages.put(3, titleImage4);
            titleImages.put(4, titleImage5);

            for (int i=0; i<titleImages.size(); i++){
                BufferedImage resizedImage = resizeImage(titleImages.get(i), 590, 190);
                titleImages.put(i, resizedImage);
            }

        }
        catch (IOException e) {
            logger.error("Image loading failed: {} ", e);
        }
    }

    /**
     * Method to Resize BufferedImages to target Height and width
     */
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight){
        BufferedImage resizedImage = new BufferedImage(590, 190, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

     /**
     * Method to that returns a hashmap with Buffered Images for main menu title animation.
     */
    public static HashMap<Integer, BufferedImage> getImageHashMap(){
        return titleImages;
    }
}