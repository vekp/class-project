package minigames.client.snake;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents the main gameplay area for the Snake game, including
 * the game grid, status labels (e.g., score, lives, time), and game control buttons.
 */
public class GamePlayPanel extends JPanel implements ActionListener {

    // UI Components
    private final BackgroundContainer backgroundContainer;
    private final MainMenuPanel.PanelSwitcher panelSwitcher;
    private JPanel gamePlayArea;
    private JLabel scoreValueLabel;
    private JLabel livesValueLabel;
    private JLabel timeValueLabel;
    private JLabel levelValueLabel;
    private JButton playAgainButton;
    private JButton resumeGameButton;
    private JButton exitButton;
    private JLabel[][] gridLabels;

    // Game Logic
    private final GameLogic gameLogic;
    private GameBoard gameBoard;

    // Timers
    private final Timer gameLoopTimer;

    /**
     * Constructor for the GamePlayPanel.
     *
     * @param panelSwitcher The panel switcher for navigation.
     * @param gameLogic     The game logic handling gameplay mechanics.
     */
    public GamePlayPanel(MainMenuPanel.PanelSwitcher panelSwitcher, GameLogic gameLogic) {
        // Initialize UI components and event listeners
        this.setFocusable(true);
        this.requestFocusInWindow();
        gameLoopTimer = new Timer(GameConstants.GAME_LOOP_DELAY, this);
        this.panelSwitcher = panelSwitcher;
        this.gameLogic = gameLogic;

        // Set layout and add background container
        setLayout(null);
        backgroundContainer = new BackgroundContainer();
        backgroundContainer.setLayout(null);
        add(backgroundContainer);

        // Initialize the game interface
        initializeGameInterface();

        // Repaint the panel
        repaint();

        // Add listeners
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                GamePlayPanel.this.requestFocusInWindow();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GamePlayPanel.this.requestFocusInWindow();
            }
        });
    }

    /**
     * Initializes the game interface by setting up game area, labels, and buttons.
     */
    private void initializeGameInterface() {
        setupGamePlayArea();
        setupLabels();
        setupExitButton();
    }

    /**
     * Sets up the main game area where the snake moves and food appears.
     */
    private void setupGamePlayArea() {
        gamePlayArea = new JPanel();
        gamePlayArea.setLayout(null);
        gamePlayArea.setOpaque(false);

        // Set the game area border
        Border border = BorderFactory.createMatteBorder(
                GameConstants.BORDER_THICKNESS, GameConstants.BORDER_THICKNESS,
                GameConstants.BORDER_THICKNESS, GameConstants.BORDER_THICKNESS,
                GameConstants.DEFAULT_BORDER_COLOR
                                                       );
        gamePlayArea.setBorder(border);
        gamePlayArea.setPreferredSize(
                new Dimension(GameConstants.GAME_PLAY_WIDTH, GameConstants.GAME_PLAY_HEIGHT));
        gamePlayArea.setBounds(0, 0, GameConstants.GAME_PLAY_WIDTH, GameConstants.GAME_PLAY_HEIGHT);
        backgroundContainer.add(gamePlayArea);

        // Set up game board and grid
        gameBoard = new GameBoard(
                GameConstants.GAME_PLAY_WIDTH / GameConstants.SQUARE_SIZE,
                GameConstants.GAME_PLAY_HEIGHT / GameConstants.SQUARE_SIZE
        );
        this.gameLogic.setGameBoard(gameBoard);
        gridLabels = new JLabel[gameBoard.getWidth()][gameBoard.getHeight()];

        int offsetX =
                gamePlayArea.getX() + ((gamePlayArea.getWidth() % GameConstants.SQUARE_SIZE) / 2);
        int offsetY =
                gamePlayArea.getY() + ((gamePlayArea.getHeight() % GameConstants.SQUARE_SIZE) / 2);

        for (int x = 0; x < gameBoard.getWidth(); x++) {
            for (int y = 0; y < gameBoard.getHeight(); y++) {
                JLabel label = new JLabel();
                label.setOpaque(false); // So background color is visible

                int labelX = offsetX + (x * GameConstants.SQUARE_SIZE);
                int labelY = offsetY + (y * GameConstants.SQUARE_SIZE);

                label.setBounds(
                        labelX, labelY, GameConstants.SQUARE_SIZE, GameConstants.SQUARE_SIZE);
                gamePlayArea.add(label);
                gridLabels[x][y] = label;
            }
        }

        repaint();
    }

    /**
     * Handles the key presses for game controls.
     *
     * @param e The key event triggered.
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                gameLogic.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                gameLogic.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                gameLogic.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                gameLogic.setDirection(Direction.RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                if (gameLogic.isGamePaused()) {
                    gameLogic.setGamePaused(false);
                    MultimediaManager.playBackgroundSound(MusicChoice.GAME_PLAY_MUSIC);
                }
                else {
                    gameLogic.setGamePaused(true);
                    MultimediaManager.playBackgroundSound(MusicChoice.GAME_PAUSE_MUSIC);

                }
                break;
        }
    }

    /**
     * Sets up the game status labels including score, lives, time, and level.
     */
    private void setupLabels() {
        // Score Key Label
        JLabel scoreKeyLabel = createLabel(
                "SCORE", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(scoreKeyLabel, GameConstants.SCORE_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_KEYS_Y,
                                 GameConstants.STANDARD_LABEL_HEIGHT
                                );

        // Score Value Label
        scoreValueLabel = createLabel(
                "0", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(scoreValueLabel, GameConstants.SCORE_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_VALUES_Y,
                                 GameConstants.VALUE_LABEL_HEIGHT
                                );

        // Lives Key Label
        JLabel livesKeyLabel = createLabel(
                "LIVES", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(livesKeyLabel, GameConstants.LIVES_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_KEYS_Y,
                                 GameConstants.STANDARD_LABEL_HEIGHT
                                );

        // Lives Value Label
        livesValueLabel = createLabel(
                "0", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(livesValueLabel, GameConstants.LIVES_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_VALUES_Y,
                                 GameConstants.VALUE_LABEL_HEIGHT
                                );

        // Time Key Label
        JLabel timeKeyLabel = createLabel(
                "TIME", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(timeKeyLabel, GameConstants.TIME_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_KEYS_Y,
                                 GameConstants.STANDARD_LABEL_HEIGHT
                                );

        // Time Value Label
        timeValueLabel = createLabel(
                "00:00", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(timeValueLabel, GameConstants.TIME_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_VALUES_Y,
                                 GameConstants.VALUE_LABEL_HEIGHT
                                );

        // Level Key Label
        JLabel levelKeyLabel = createLabel(
                "LEVEL", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(levelKeyLabel, GameConstants.LEVEL_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_KEYS_Y,
                                 GameConstants.STANDARD_LABEL_HEIGHT
                                );

        // Level Value Label
        levelValueLabel = createLabel(
                "0", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(levelValueLabel, GameConstants.LEVEL_TEXT_POSITION_X,
                                 GameConstants.STATUS_LABEL_VALUES_Y,
                                 GameConstants.VALUE_LABEL_HEIGHT
                                );
    }

    /**
     * Helper method to create and return a standardized JLabel.
     *
     * @param text  The text to be displayed on the label.
     * @param color The text color of the label.
     * @param font  The font of the label.
     * @return A JLabel object.
     */
    private JLabel createLabel(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundContainer.add(label);
        return label;
    }

    /**
     * Helper method to set position and size for a JLabel.
     *
     * @param label     The label to be adjusted.
     * @param positionX The X position.
     * @param positionY The Y position.
     * @param height    The height of the label.
     */
    private void setBackgroundAndPosition(JLabel label, int positionX, int positionY, int height) {
        label.setBounds(positionX, positionY, GameConstants.LABEL_WIDTH, height);
    }

    /**
     * Sets up the exit button functionality utilizing the UIHelper's method for return button.
     */
    private void setupExitButton() {
        ButtonFactory.setupReturnButton(
                panelSwitcher, backgroundContainer, 0, 0, GameConstants.EXIT_GAME,
                MusicChoice.MENU_MUSIC
                                       );
    }

    /**
     * Adjusts the layout of the game play area based on component dimensions.
     */
    @Override
    public void doLayout() {
        super.doLayout();
        int x = (this.getWidth() - gamePlayArea.getWidth()) / 2;
        gamePlayArea.setBounds(
                x, GameConstants.GAME_PLAY_AREA_Y, gamePlayArea.getWidth(),
                gamePlayArea.getHeight()
                              );
    }

    /**
     * Updates the game status labels including time, lives left, score, and level.
     *
     * @param timeSeconds Number of seconds elapsed in the game.
     * @param livesLeft   Number of player lives remaining.
     * @param score       Current player score.
     * @param level       Current game level.
     */
    public void updateGameStatus(int timeSeconds, int livesLeft, int score, int level) {
        // Formatting and updating the time label
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timeValueLabel.setText(timeString);

        // Updating other game status labels
        livesValueLabel.setText(String.valueOf(livesLeft));
        scoreValueLabel.setText(String.valueOf(score));
        levelValueLabel.setText(String.valueOf(level));
    }

    /**
     * Starts the game by initiating game logic and starting the game loop timer.
     */
    public void startGame() {
        gameLogic.startGame();
        gameLoopTimer.start();
        MultimediaManager.playBackgroundSound(MusicChoice.GAME_PLAY_MUSIC);
    }

    /**
     * Overrides the paintComponent method to render game over and game paused graphics.
     *
     * @param g The graphics context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Render game over or game paused screens
        gameOver(g);
        gamePaused(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameLogic.gameLoop();
        updateGameStatus(gameLogic.getTimeSeconds(), gameLogic.getLives(), gameLogic.getScore(),
                         gameLogic.getLevel()
                        );

        // Check if the delay has changed in gameLogic
        int newDelay = gameLogic.getGameLoopDelay();  // Using the getter from your gameLogic class
        if (gameLoopTimer.getDelay() != newDelay) {
            gameLoopTimer.setDelay(newDelay);
            gameLoopTimer.restart();
        }

        updateGamePlayArea();
        repaint();
    }

    /**
     * Updates the game board visuals based on the current state of the game.
     * Labels within the grid are set to opaque when assigned a color, ensuring
     * they do not show a default white background. If no item is present at a
     * particular cell, the label is made non-opaque.
     */
    public void updateGamePlayArea() {
        for (int x = 0; x < gameBoard.getWidth(); x++) {
            for (int y = 0; y < gameBoard.getHeight(); y++) {
                ItemType type = gameBoard.getItemTypeAt(x, y);
                JLabel label = gridLabels[x][y];
                int labelWidth = label.getWidth();
                int labelHeight = label.getHeight();

                switch (type) {
                    case SNAKE -> {
                        label.setOpaque(true);
                        label.setBackground(Color.BLACK);
                        label.setIcon(null); // Remove icon if any
                    }
                    case APPLE, CHERRY, ORANGE, WATERMELON, SPOILED_FOOD -> {
                        ImageResource resource;
                        if (type == ItemType.APPLE) {
                            resource = MultimediaManager.getAppleResource();
                        }
                        else if (type == ItemType.CHERRY) {
                            resource = MultimediaManager.getCherryResource();
                        }
                        else if (type == ItemType.ORANGE) {
                            resource = MultimediaManager.getOrangeResource();
                        }
                        else if (type == ItemType.WATERMELON){
                            resource = MultimediaManager.getWatermelonResource();
                        }
                        else {
                            resource = MultimediaManager.getBadFruitResource();
                        }
                        ImageIcon originalIcon = resource.getImageResource();
                        Image originalImage = originalIcon.getImage();
                        Image scaledImage = originalImage.getScaledInstance(
                                labelWidth, labelHeight, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);
                        label.setIcon(scaledIcon);
                    }
                    case VACANT -> {
                        label.setOpaque(false);
                        label.setIcon(null); // Remove icon if any
                    }
                }
            }
        }
    }

    /**
     * Renders the "Game Over" graphics and controls the visibility of the "Play Again" and
     * "Exit" buttons
     * when the game is over. If the game is not over, it ensures the mentioned buttons remain
     * hidden.
     *
     * @param g The graphics context used for rendering.
     */
    private void gameOver(Graphics g) {
        if (gameLogic.isGameOver()) {
            // Display game over text
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(
                    "Game Over",
                    (GameConstants.GAME_PLAY_WIDTH - metrics.stringWidth("Game Over")) / 2,
                    GameConstants.GAME_PLAY_HEIGHT / 2
                        );

            // Initialize and show the "Play Again" button if not already present
            if (playAgainButton == null) {
                playAgainButton = ButtonFactory.createButton("Play Again",
                                                             (GameConstants.GAME_PLAY_WIDTH - 150) / 2,
                                                             (GameConstants.GAME_PLAY_HEIGHT / 2) + 50,
                                                             150, 50,
                                                             e -> {
                                                                 gameLogic.resetGame();
                                                                 playAgainButton.setVisible(false);
                                                                 exitButton.setVisible(false);
                                                             }
                                                            );
                this.add(playAgainButton);
            }
            playAgainButton.setVisible(true);

            // Initialize and show the "Exit" button if not already present
            if (exitButton == null) {
                exitButton = ButtonFactory.createButton("Exit",
                                                        (GameConstants.GAME_PLAY_WIDTH - 150) / 2,
                                                        (GameConstants.GAME_PLAY_HEIGHT / 2) + 120,
                                                        150, 50,
                                                        e -> System.exit(0)
                                                       );
                this.add(exitButton);
            }
            exitButton.setVisible(true);
        }
        else {
            // Hide the "Play Again" and "Exit" buttons if the game is not over
            if (playAgainButton != null) {
                playAgainButton.setVisible(false);
            }
            if (exitButton != null) {
                exitButton.setVisible(false);
            }
        }
    }

    /**
     * Renders the "Game Paused" graphics and controls the visibility of the "Resume Game" button
     * when the game is paused. If the game is not paused, it ensures the "Resume Game" button
     * remains hidden.
     *
     * @param g The graphics context used for rendering.
     */
    private void gamePaused(Graphics g) {
        if (gameLogic.isGamePaused()) {
            // Display game paused text
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(
                    "Game Paused",
                    (GameConstants.GAME_PLAY_WIDTH - metrics.stringWidth("Game Paused")) / 2,
                    GameConstants.GAME_PLAY_HEIGHT / 2
                        );

            // Initialize and show the "Resume Game" button if not already present
            if (resumeGameButton == null) {
                resumeGameButton = ButtonFactory.createButton("Resume Game",
                                                              (GameConstants.GAME_PLAY_WIDTH - 180) / 2,
                                                              (GameConstants.GAME_PLAY_HEIGHT / 2) + 50,
                                                              180, 50,
                                                              e -> {
                                                                  gameLogic.setGamePaused(false);
                                                                  resumeGameButton.setVisible(
                                                                          false);
                                                              }
                                                             );
                this.add(resumeGameButton);
            }
            resumeGameButton.setVisible(true);
        }
        else {
            // Hide the "Resume Game" button if the game is not paused
            if (resumeGameButton != null) {
                resumeGameButton.setVisible(false);
            }
        }
    }
}
