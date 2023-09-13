package minigames.server.gameshow;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 *  A class to implement the server for the Word Scramble game
 */
public class ImageGuesser implements GameShowMiniGame {

   private String imageFileName;
   private Path path;
   private ImageData chosenImage;

   public ImageGuesser() {
        imageFileName = "imageList.json";
        path = Paths.get("src/main/java/minigames/server/gameshow/" + imageFileName);
        List<ImageData> imageList = getImages();
        chooseImage(imageList);
    }

    private static final Logger logger = LogManager.getLogger(ImageGuesser.class);
    Random rand = new Random();
    Vertx vertx = Vertx.vertx();

    // Reads and deserializes the JSON file into a list of ImageData objects
    public List<ImageData> getImages() {
        List<ImageData> imagesList = new ArrayList<>();
        try {
            Buffer buffer = vertx.fileSystem().readFileBlocking(path.toString());
            JsonArray jsonArray = new JsonArray(buffer.toString(StandardCharsets.UTF_8));

            for (Object jsonObject : jsonArray) {
                if (jsonObject instanceof JsonObject) {
                    JsonObject imageObject = (JsonObject) jsonObject;
                    String imageFileName = imageObject.getString("imageFileName");
                    String imageAnswer = imageObject.getString("imageAnswer");
                    imagesList.add(new ImageData(imageFileName, imageAnswer));
                }
            }
        } catch (io.vertx.core.file.FileSystemException e) {
            logger.error("Unable to read images from imageList.json", e);
        }

        logger.info(imagesList.size() + " images loaded from file!");
        return imagesList;
    }


    public String getImageName() {
        return this.chosenImage.getImageAnswer();
    }

    public String getImageFileName() {
        return this.chosenImage.getImageFileName();
    }

    // Randomly chooses a word from a given list `wordsList`
    public void chooseImage(List<ImageData> imagesList) {
        this.chosenImage = imagesList.get(rand.nextInt(imagesList.size()));

        logger.info("The chosen word is {}", this.chosenImage.getImageAnswer());
    }


    // Check if a guess is correct
    public boolean guessIsCorrect(String guess) {
        logger.info("The guessed word is {}", guess);
        logger.info("The actual word is {}", this.chosenImage.getImageAnswer());
        return guess.toLowerCase().equals(this.chosenImage.getImageAnswer().toLowerCase());
    }


    // Inner class to represent image data
    private static class ImageData {
        private String imageFileName;
        private String imageAnswer;

        public ImageData(String imageFileName, String imageAnswer) {
            this.imageFileName = imageFileName;
            this.imageAnswer = imageAnswer;
        }

        public String getImageFileName() {
            return imageFileName;
        }

        public String getImageAnswer() {
            return imageAnswer;
        }
    }
}
