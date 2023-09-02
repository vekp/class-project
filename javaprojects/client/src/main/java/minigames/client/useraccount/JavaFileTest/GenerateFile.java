import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateFile {
    public static void generateFile(String content) {
        // Specify the file path
        String filePath = "user_accounts.json";

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            // Write the content to the file
            bufferedWriter.write(content);

            System.out.println("User account information has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}