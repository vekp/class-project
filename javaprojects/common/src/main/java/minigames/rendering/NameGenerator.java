package minigames.rendering;

import java.util.*;

/**
 * Class Name Generator: This is for generating random name for game instances.
 * 
 * Usage: In game server: Set<String> existingNames = games.keySet();
 * String newName = NameGenerator.generateName(existingNames);
 */

public class NameGenerator {

    // Adjectives 
    private static final String[] adjectives = { "adorable", "clever", "eager", "curious", "fierce", "graceful",
            "happy", "inventive", "keen", "elastic", "funny", "optimistic", "great", "trusting", "beautiful" };

    // Scientists 
    private static final String[] scientists = { "einstein", "newton", "curie", "tesla", "edison", "turing", "hawking",
            "hooper", "lovelace", "tyson", "darwin", "avicenna", "galilei", "pasteur", "planck" };

    private static final String chars = "abcdefghijklmnopqrstuvwxyz";
    private static final Random rand = new Random();


    /**
    * This method to generate a random name of two parts
    * first part is an adjective and second part is the surname
    * of a scientist. These two parts are separated by an underscore.
    * if a unique name available, else a random string
    * 
    * @params: Set of strings containing existing names of the game server
    * @return: string name
    */
    public static String generateName(Set<String> existingNames) {
        List<String> availableAdjectives = new ArrayList<>(Arrays.asList(adjectives));
        List<String> availableScientists = new ArrayList<>(Arrays.asList(scientists));

        if (existingNames != null && !existingNames.isEmpty()) {
            for (String name : existingNames) {
                String[] parts = name.split("_");
                if (parts.length == 2) {
                    availableAdjectives.remove(parts[0]);
                    availableScientists.remove(parts[1]);
                }
            }
        }

        if (availableAdjectives.isEmpty() || availableScientists.isEmpty()) {
            return randomName();
        }

        String name = availableAdjectives.get(rand.nextInt(availableAdjectives.size())) + "_"
                + availableScientists.get(rand.nextInt(availableScientists.size()));

        return name;

    }


    /**
    * This is a helper method to generate a random string of 
    * two parts separated by an underscore if no unique name is 
    * available in generateName method.
    * 
    * @params: None
    * @return: a random string 
    */
    private static String randomName() {
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            sb1.append(chars.charAt(rand.nextInt(chars.length())));
            sb2.append(chars.charAt(rand.nextInt(chars.length())));
        }

        return sb1.toString() + "_" + sb2.toString();
    }
}