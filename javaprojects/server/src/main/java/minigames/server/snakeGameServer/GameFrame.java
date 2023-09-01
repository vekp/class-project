import javax.swing.JFrame;

// Frame class that holds the game panel
public class GameFrame extends JFrame {

    // Constructor
    GameFrame() {
        setTitle("Snake"); // Set the title of the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set close operation
        setResizable(false); // Make the frame not resizable

        // Create a new instance of the GameDisplay class (the game panel)
        GameDisplay gameDisplay = new GameDisplay();
        add(gameDisplay); // Add the game panel to the frame

        pack(); // Pack the components in the frame
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Make the frame visible
    }
}




