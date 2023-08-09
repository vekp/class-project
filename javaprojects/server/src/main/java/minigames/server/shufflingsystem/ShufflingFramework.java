package minigames.server.shufflingsystem;

import java.util.Random;


public class ShufflingFramework<T> {

    private static Random random = new Random();
        /**
     * This method takes an array of any data type 
     * the fisher-yates in place shuffling algorithm on them. 
     * @param items -> an array of items to be shuffled
     * This method will modify the original array so ensure you have a copy of
     * your original array, or an easy way to create a new unshuffled version
     */
    public static <T> void shuffle(T[] items){
        //We will need to implement a playing card class, I have a basic one if you'd like to look at it Melinda.
        //It's early days so play around with the code, it can definitely be better than what I've done here.
        for(int i = items.length -1; i > 0; i--){
            //Adding +1 to i because we DO want to potentially swap an item with itself.
            //Otherwise this would be an implementation of Satollo's algorithm.
            int swap = random.nextInt(i + 1);
            T temp = items[i];
            items[i] = items[swap];
            items[swap] = temp;
        } 
    }
    
}
