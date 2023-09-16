package minigames.client.snake;

public class HelpMenuPanel extends BasePanel{


//    TODO: Convert this to the panel logic and pass in all of the setup
    //    /**
//     * Displays a modal dialog with instructions on how to play the Snake game.
//     *
//     * This method creates and shows a JDialog that contains a list of instructions
//     * for playing the game. The instructions are:
//     * 1. Using arrow keys to control the snake's direction.
//     * 2. Eating fruits to grow longer and earn points.
//     * 3. Avoiding collisions with walls or the snake's own body.
//     * 4. Using the spacebar to toggle pause.
//     *
//     * The dialog is centered relative to the provided parent component and
//     * has a fixed size. It uses a combination of JPanel and JLabel to present
//     * the instructions in a formatted manner.
//     *
//     * @param parent The parent component relative to which the dialog is displayed.
//     */
//    private static void showHowToPlayDialog(Component parent) {
//        // Create the dialog and set properties
//        JDialog howToPlayDialog = new JDialog();
//        howToPlayDialog.setSize(600, 350);
//        howToPlayDialog.setResizable(false);
//        howToPlayDialog.setLocationRelativeTo(parent);
//
//        // Create and set up the instructions panel
//        JPanel instructionsPanel = new JPanel();
//        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
//        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        // Create and set up the title label
//        JLabel title = new JLabel("How to Play");
//        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 0));
//        title.setFont(new Font("Arial", Font.BOLD, 24));
//        title.setForeground(new Color(234, 108, 55));
//
//        // List of instructions
//        String[] instructions = {
//                "1. Use the arrow keys to control the direction of the snake.",
//                "2. Eat the fruit to grow longer and earn points.",
//                "3. Avoid running into the walls or yourself.",
//                "4. Press the spacebar to pause and unpause the game."
//        };
//
//        // Add the title to the instructions panel
//        instructionsPanel.add(title);
//
//        // Add each instruction to the instructions panel
//        for (String instruction : instructions) {
//            JLabel instructionLabel = new JLabel(instruction);
//            instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//            instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
//            instructionLabel.setForeground(new Color(40, 190, 92));
//            instructionsPanel.add(instructionLabel);
//        }
//
//        // Set the background color of the instructions panel
//        instructionsPanel.setBackground(new Color(0, 0, 0));
//
//        // Add the instructions panel to the dialog
//        howToPlayDialog.add(instructionsPanel);
//
//        // Display the dialog
//        howToPlayDialog.setVisible(true);
//    }
}
