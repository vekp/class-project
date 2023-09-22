package minigames.server.shufflingsystem;

import java.util.Random;
import java.util.List;

public class ShufflingFramework {

    static Random random = new Random();

    // Making the constructor for ShufflingFramework private, it's a utility class
    // and will not need
    // To be instantiated
    private ShufflingFramework() {
    }

    /**
     * This method takes an array of any data type
     * the fisher-yates in place shuffling algorithm on them.
     * 
     * @param items  -> an array of items to be shuffled
     * @param random -> an instance of a Random object | you could create the
     *               default one or pass a seed if you want
     *               to control the randomness or reproduce a particular shuffle
     *               This method will modify the original array so ensure you have a
     *               copy of
     *               your original array, or an easy way to create a new unshuffled
     *               version
     */
    public static <T> void shuffle(T[] items, Random random) {
        if (items.length == 0) {
            throw new IllegalArgumentException("This array is empty and cannot be shuffled");
        }
        // We will need to implement a playing card class, I have a basic one if you'd
        // like to look at it Melinda.
        // It's early days so play around with the code, it can definitely be better
        // than what I've done here.
        for (int i = items.length - 1; i > 0; i--) {
            // Adding +1 to i because we DO want to potentially swap an item with itself.
            // Otherwise this would be an implementation of Satollo's algorithm.
            int swap = random.nextInt(i + 1);
            T temp = items[i];
            items[i] = items[swap];
            items[swap] = temp;
        }
    }

    // Shuffle an array of items with default random parameters
    public static <T> void shuffle(T[] items) {
        if (items.length == 0) {
            throw new IllegalArgumentException("This array is empty and cannot be shuffled");
        }
        for (int i = items.length - 1; i > 0; i--) {
            // Adding +1 to i because we DO want to potentially swap an item with itself.
            // Otherwise this would be an implementation of Satollo's algorithm.
            int swap = random.nextInt(i + 1);
            T temp = items[i];
            items[i] = items[swap];
            items[swap] = temp;
        }
    }

    public static <T> void shuffle(List<T> items, Random random) {
        if (items.size() == 0) {
            throw new IllegalArgumentException("The List is empty and cannot be shuffled");
        }
        for (int i = items.size() - 1; i > 0; i--) {
            int swap = random.nextInt(i + 1);
            T temp = items.get(i);
            items.set(i, items.get(swap));
            items.set(swap, temp);
        }
    }

    // Shuffle a list, now this will work with anything using the list interface.
    public static <T> void shuffle(List<T> items) {
        if (items.size() == 0) {
            throw new IllegalArgumentException("The List is empty and cannot be shuffled");
        }
        Random random = new Random();
        for (int i = items.size() - 1; i > 0; i--) {
            int swap = random.nextInt(i + 1);
            T temp = items.get(i);
            items.set(i, items.get(swap));
            items.set(swap, temp);
        }
    }
    //More work would be required to get this to work on shuffling a queue or stack
    //Doesn't seem like a huge priority, will come back to it if I have time or if other groups request it
}
