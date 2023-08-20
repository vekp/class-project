package minigames.server.shufflingsystem;

import java.util.Random;
import java.util.ArrayList;

public class ShufflingFramework<T> {

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


    //Shuffle an array of items with default random parameters
    public static <T> void shuffle(T[] items) {
        for (int i = items.length - 1; i > 0; i--) {
            // Adding +1 to i because we DO want to potentially swap an item with itself.
            // Otherwise this would be an implementation of Satollo's algorithm.
            int swap = random.nextInt(i + 1);
            T temp = items[i];
            items[i] = items[swap];
            items[swap] = temp;
        }
    }

    //Shuffle an arraylist of items, please provide an instance of random
    public static <T> void shuffle(ArrayList<T> items, Random random) {
        for (int i = items.size() - 1; i > 0; i--) {
            // Adding +1 to i because we DO want to potentially swap an item with itself.
            // Otherwise this would be an implementation of Satollo's algorithm.
            int swap = random.nextInt(i + 1);
            T temp = items.get(i);
            items.set(i, items.get(swap));
            items.set(swap, temp);
        }
    }

    //Shuffle an arraylist of items with default instance of random. 
    public static <T> void shuffle(ArrayList<T> items) {
        for (int i = items.size() - 1; i > 0; i--) {
            // Adding +1 to i because we DO want to potentially swap an item with itself.
            // Otherwise this would be an implementation of Satollo's algorithm.
            int swap = random.nextInt(i + 1);
            T temp = items.get(i);
            items.set(i, items.get(swap));
            items.set(swap, temp);
        }
    }

}
