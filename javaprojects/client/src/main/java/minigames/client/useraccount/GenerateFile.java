package minigames.client.useraccount;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class GenerateFile {
    public void generateFile(String content) {
        String fileName = "src/main/java/minigames/client/useraccount/user_accounts.json";
        File file = new File(fileName);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            // Write the content to the file
            bufferedWriter.write("\n" + content);
            System.out.println("User account information has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}