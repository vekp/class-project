package minigames.client.krumgame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteGenerator {
    public static void main(String[] args) {
        String filePath = "quotes.txt";

        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            if (!lines.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(lines.size());
                String randomLine = lines.get(randomIndex);
                System.out.println("Random Line: " + randomLine);
            } else {
                System.out.println("The file is empty.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    

