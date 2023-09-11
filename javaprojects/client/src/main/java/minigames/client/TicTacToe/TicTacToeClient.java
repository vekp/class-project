package minigames.client.tictactoe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The TicTacToeClient class provides an interface to play the Tic Tac Toe game 
 * by connecting to the TicTacToeServer.
 */
public class TicTacToeClient {

    // Hostname and port for the Tic Tac Toe server.
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;

    // Socket for communication and its associated readers and writers.
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Connects to the Tic Tac Toe server.
     */
    public TicTacToeClient() {
        try {
            socket = new Socket(HOSTNAME, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Failed to connect to the server.");
            e.printStackTrace();
        }
    }

    /**
     * Main loop to play the game.
     */
    public void play() {
        try {
            String serverResponse;

            // Display the initial game state.
            System.out.println(in.readLine());

            while (true) {
                // Display the current game state and any server messages.
                while ((serverResponse = in.readLine()) != null) {
                    if (serverResponse.equals("YOUR_TURN")) {
                        break;
                    }
                    System.out.println(serverResponse);
                }

                // Get the player's move.
                System.out.println("Enter your move (row and column separated by space, e.g., '1 2' for row 1 and column 2):");
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput = userIn.readLine();
                out.println(userInput);

                // Check if the game has ended.
                serverResponse = in.readLine();
                if (serverResponse.equals("GAME_OVER")) {
                    System.out.println(in.readLine()); // Display the final state and reason for game over.
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while playing the game.");
            e.printStackTrace();
        }
    }

    /**
     * Close the client's resources.
     */
    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error occurred while closing the client.");
            e.printStackTrace();
        }
    }

    /**
     * The main entry point for the TicTacToeClient.
     */
    public static void main(String[] args) {
        TicTacToeClient client = new TicTacToeClient();
        client.play();
        client.close();
    }
}
