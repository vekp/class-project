package minigames.utils;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** A class with static methods to be used everywhere */
public class Tools {

    private static final Logger logger = LogManager.getLogger(Tools.class);

    /**
     * String to title case
     *
     * @param input string lower case
     * @return String to title case
     */
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    /**
     * Generates a random name made of an adjective and an animal. 
     *
     */
    public static String generateRandomName() {
        // defaul name
        String randomName = "Allaska";

        // load the files
        Path adjectiveListFile = Path.of("../common/src/main/utils/adjective.txt");
        Path nounListFile = Path.of("../common/src/main/utils/noun.txt");
        Path animalsListFile = Path.of("../common/src/main/utils/animals.txt");

        List<String> animalsList = new ArrayList<>();
        List<String> adjectivesList = new ArrayList<>();
        List<String> nounList = new ArrayList<>();

        try {
            animalsList = Files.readAllLines(animalsListFile);
            adjectivesList = Files.readAllLines(adjectiveListFile);
            nounList = Files.readAllLines(nounListFile);
        } catch (IOException e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }

        if (animalsList.size() > 0 && adjectivesList.size() > 0 && nounList.size() > 0) {
            Random r = new Random();
            String animal =
                    toTitleCase(animalsList.get(r.nextInt(animalsList.size()))).replace(" ", "_");
            String adjective =
                    toTitleCase(adjectivesList.get(r.nextInt(adjectivesList.size())))
                            .replace(" ", "_");
            String noun = toTitleCase(nounList.get(r.nextInt(nounList.size())).replace(" ", "_"));
            // changed from r.nextInt(int, int) to just int to deal with errors
            randomName = adjective + "_" + animal + "_" + noun;
        }
        return randomName;
    }

    /**
     * Scales an image to size
     *
     * @param bufferedImage image to be scaled
     * @param width int size
     * @param height int size
     * @return BufferedImage scaled
     * 
     */
    public static BufferedImage scaleImage(BufferedImage bufferedImage, int width, int height)
            throws NullPointerException {
        BufferedImage scaledImage = new BufferedImage(width, height, bufferedImage.getType());
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(bufferedImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return scaledImage;
    }

    /**
     * Rotates an image
     *
     * @param bufferedImage BufferedImage to be rotated
     * @param radians int number of degrees to rotate. E.g.: 45
     * @return BufferedImage image rotated
     *
     */
    public static BufferedImage rotateImage(BufferedImage bufferedImage, int radians) {
        double rotationRequired = Math.toRadians(radians);
        double locationX = bufferedImage.getWidth() / 2;
        double locationY = bufferedImage.getHeight() / 2;
        AffineTransform tx =
                AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(bufferedImage, null);
    }

    /**
     * Count the number of files of a type in a directory.
     *
     * @param dirPath String the path of dir to be checked
     * @param fileType String the file type E.g.: png
     * @return int the number of files
     */
    public static int countFilesInDir(String dirPath, String fileType) {
        File f = new File(dirPath);
        int count = 0;
        for (File file : f.listFiles()) {
            if (file.isFile()
                    && (!file.getName().contains(" ")) // Bug fix with duplicated images
                    && (fileType == null
                            || file.getName()
                                    .toLowerCase()
                                    .endsWith("." + fileType.toLowerCase()))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count the total number of files in a dir
     * @param dirPath String the dir path
     * @return int the number of all files in a dir
     */
    public static int countFilesInDir(String dirPath) {
        return countFilesInDir(dirPath, null);
    }

    /**
     * Helper function that returns 2d array in string. Helps during debugging.
     *
     * @param arrayvar array
     * @return String formatted array
     */
    public static String array2dToString(int[][] arrayvar) {
        String printString = "";
        int rows = arrayvar.length;
        int columns = arrayvar[0].length;

        // Print mapTileNum
        printString = "\n" + "Array print: \n";
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {
                printString += arrayvar[j][i] + ",";
            }
            printString += "\n";
        }

        printString += "\n";

        return printString;
    }

    /**
     * Get a URI from path, OS independet.
     *
     * @param filePath
     * @return URI file
     */
    public static URI getFileURI(String filePath) {
        // replace %20 (space char)
        filePath = filePath.replace("%20", " ");
        URL fileUrl = ClassLoader.getSystemResource(filePath);
        URI fileUri = null;
        try {
            // Check if resource was found
            if (fileUrl == null) throw new NullPointerException();

            // Converts to URI using Paths (OS independent)
            fileUri = Paths.get(fileUrl.toURI()).toUri();
        } catch (URISyntaxException | NullPointerException e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }
        return fileUri;
    }

    /**
     * Get Rectangle object from comma separated string
     * @param rectString
     * @return Rectangle
     */
    public static Rectangle getRectangleFromString(String rectString) {

        String[] rectSplit = rectString.split(",");
        Rectangle r =
                new Rectangle(
                        Integer.parseInt(rectSplit[0]),
                        Integer.parseInt(rectSplit[1]),
                        Integer.parseInt(rectSplit[2]),
                        Integer.parseInt(rectSplit[3]));
        return r;
    }

    /**
     * Get Point object from comma separated string
     *
     * @param pointString
     * @return Rectangle
     */
    public static Point getPointFromString(String pointString) {

        String[] pointSplit = pointString.split(",");
        Point p = new Point(Integer.parseInt(pointSplit[0]), Integer.parseInt(pointSplit[1]));
        return p;
    }

    /**
     * Check if two rectangles are overlapping on a 2D pane
     * @param r1 Rectangle
     * @param r2 Rectangle
     * @return boolean true if they are overlaping, false otherwise
     */
    public static boolean isRectanglesOverlapping(Rectangle r1, Rectangle r2) {

        if ((r1.getX() < r2.getX() + r2.getWidth()
                && r1.getX() + r1.getWidth() > r2.getX()
                && r1.getY() < r2.getY() + r2.getHeight()
                && r1.getHeight() + r1.getY() > r2.getY())) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param iconPath Specify the path to the icon file
     * @return An ImageIcon object
     */
    public static ImageIcon getImageIcon(String iconPath) {
        ImageIcon icon;
        try {
            icon = new ImageIcon(ImageIO.read(new File(Tools.getFileURI(iconPath))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return icon;
    }
}
