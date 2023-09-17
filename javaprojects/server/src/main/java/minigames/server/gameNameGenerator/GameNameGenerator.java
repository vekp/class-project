package minigames.server.gameNameGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * The `GameNameGenerator` class is used to generate random names from categorized words.
 */
public class GameNameGenerator {

    // A map to store categorized words.
    private static HashMap<String, List<String>> categorizedWords = new HashMap<>();

    // A random number generator.
    private static final Random r = new Random();

    // Two category strings provided by the user.
    private String category1;
    private String category2;

    /**
     * Constructor to initialize the `GameNameGenerator` with two categories.
     *
     * @param category1 The first category.
     * @param category2 The second category.
     */
    public GameNameGenerator(String category1, String category2) {
        // Convert category names to lowercase for consistency.
        this.category1 = category1.toLowerCase();
        this.category2 = category2.toLowerCase();
    }

    // Static block to initialize categorizedWords from a file.
    static {
        try {
            // Load a file named "names.csv" from the classpath.
            InputStream inputStream = GameNameGenerator.class.getClassLoader().getResourceAsStream("names.csv");

            if (inputStream == null) {
                throw new IOException("File not found");
            }

            // Read lines from the file.
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            // Populate categorizedWords based on the lines read from the file.
            populateCategorizedWords(lines);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Falling back to default names.");
            // Default categorized word pairs if the file is not found.
            List<String> defaultLines = Arrays.asList("Star,space", "Nebula,space", "Viper,snake", "Python,snake");
            populateCategorizedWords(defaultLines);
        }
    }

    /**
     * Populate the categorizedWords map based on a list of lines containing word-category pairs.
     *
     * @param lines List of lines containing word-category pairs.
     */
    private static void populateCategorizedWords(List<String> lines) {
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                String word = parts[0].trim();
                String category = parts[1].trim().toLowerCase();
                categorizedWords.computeIfAbsent(category, k -> new ArrayList<>()).add(word);
            }
        }
    }

    /**
     * Generate a random name by combining words from the two provided categories.
     *
     * @return A random name as a String.
     */
    public String randomName() {
        List<String> words1 = categorizedWords.getOrDefault(category1, new ArrayList<>());
        List<String> words2 = categorizedWords.getOrDefault(category2, new ArrayList<>());
        if (words1.isEmpty() || words2.isEmpty()) {
            // Return a default name if categories are not found.
            return "Undefined_Category_" + String.format("%06d", r.nextInt(1000000));
        }
        // Select random words from the categories and combine them.
        String word1 = words1.get(r.nextInt(words1.size()));
        String word2 = words2.get(r.nextInt(words2.size()));
        return word1 + "_" + word2;
    }
}
