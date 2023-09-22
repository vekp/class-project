package minigames.hangman;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

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
}
