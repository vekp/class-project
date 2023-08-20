package minigames.client.gameshow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MemoryGame {

    public static void main(String[] args) {
   // File folder = new File("/src/main/resources/images/memory_game_pics");
    File folder=  new File("C:\\Users\\Admin\\OneDrive - University of New England\\Documents\\Jenifer\\Masters in IT\\COSC220\\Assignment 3 - Gameshow\\classproject\\javaprojects\\client\\src\\main\\resources\\images\\memory_game_pics");
    File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
        if (files != null && files.length > 0) {
            // Create a list of image files
            List<File> imageFiles = new ArrayList<>();
            for (File file : files) {
                imageFiles.add(file);
            }

            // Choose a random image from the list
            int randomIndex = (int) (Math.random() * imageFiles.size());
            File randomImage = imageFiles.get(randomIndex);
            File randomImage1 = imageFiles.get(randomIndex);
            File randomImage2 = imageFiles.get(randomIndex);
            File randomImage3 = imageFiles.get(randomIndex);
            File randomImage4 = imageFiles.get(randomIndex);
            File randomImage5 = imageFiles.get(randomIndex);
            File randomImage6 = imageFiles.get(randomIndex);

            // Now, you have the randomly chosen image (randomImage)
            System.out.println("Randomly chosen image: " + randomImage.getName());
        } else {
            System.out.println("No image files found in the folder.");
        }

        System.out.println("test");
    }

}


