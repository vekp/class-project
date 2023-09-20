package minigames.client.snake;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Represents the main gameplay area for the Snake game, including
 * the game grid, status labels (e.g., score, lives, time), and game control buttons.
 */
public class GamePlayPanel extends JPanel implements ActionListener {

    // UI Components
    private final BackgroundContainer backgroundContainer;
    private final MainMenuPanel.PanelSwitcher panelSwitcher;
    private JPanel gamePlayArea;
    private JLabel scoreKeyLabel;
    private JLabel scoreValueLabel;
    private JLabel livesKeyLabel;
    private JLabel livesValueLabel;
    private JLabel timeKeyLabel;
    private JLabel timeValueLabel;
    private JLabel levelKeyLabel;
    private JLabel levelValueLabel;
    private JButton playAgainButton;
    private JButton resumeGameButton;
    private JButton exitButton;
    private JLabel[][] gridLabels;

    // Game Logic
    private final GameLogic gameLogic;
    private GameBoard gameBoard;

    // Timers
    private Timer gameLoopTimer;

    /**
     * Constructor for the GamePlayPanel.
     *
     * @param panelSwitcher The panel switcher for navigation.
     * @param gameLogic     The game logic handling gameplay mechanics.
     */
    public GamePlayPanel(MainMenuPanel.PanelSwitcher panelSwitcher, GameLogic gameLogic) {
        this.setFocusable(true);
        this.requestFocusInWindow();
        gameLoopTimer = new Timer(GameConstants.GAME_LOOP_DELAY, this);

        this.panelSwitcher = panelSwitcher;
        this.gameLogic = gameLogic;

        setLayout(null);
        backgroundContainer = new BackgroundContainer();
        backgroundContainer.setLayout(null);
        add(backgroundContainer);

        initializeGameInterface();

        repaint();

        // Add listeners
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                GamePlayPanel.this.requestFocusInWindow();
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        this.addMouseListener(new MouseAdapter() {
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
                GameConstants.DEFAULT_BORDER_COLOR);
        gamePlayArea.setBorder(border);
        gamePlayArea.setPreferredSize(new Dimension(GameConstants.GAME_PLAY_WIDTH, GameConstants.GAME_PLAY_HEIGHT));
        gamePlayArea.setBounds(0, 0, GameConstants.GAME_PLAY_WIDTH, GameConstants.GAME_PLAY_HEIGHT);
        backgroundContainer.add(gamePlayArea);

        // Set up game board and grid
        gameBoard = new GameBoard(GameConstants.GAME_PLAY_WIDTH / GameConstants.SQUARE_SIZE,
                                  GameConstants.GAME_PLAY_HEIGHT / GameConstants.SQUARE_SIZE);
        this.gameLogic.setGameBoard(gameBoard);
        gridLabels = new JLabel[gameBoard.getWidth()][gameBoard.getHeight()];

        int offsetX = gamePlayArea.getX();
        int offsetY = gamePlayArea.getY();

        for (int x = 0; x < gameBoard.getWidth(); x++) {
            for (int y = 0; y < gameBoard.getHeight(); y++) {
                JLabel label = new JLabel();
                label.setOpaque(false); // So background color is visible

                int labelX = offsetX + (x * GameConstants.SQUARE_SIZE);
                int labelY = offsetY + (y * GameConstants.SQUARE_SIZE);

                label.setBounds(labelX, labelY, GameConstants.SQUARE_SIZE, GameConstants.SQUARE_SIZE);
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
                System.out.println("UP");
                break;
            case KeyEvent.VK_DOWN:
                gameLogic.setDirection(Direction.DOWN);
                System.out.println("DOWN");
                break;
            case KeyEvent.VK_LEFT:
                gameLogic.setDirection(Direction.LEFT);
                System.out.println("LEFT");
                break;
            case KeyEvent.VK_RIGHT:
                gameLogic.setDirection(Direction.RIGHT);
                System.out.println("RIGHT");
                break;
            case KeyEvent.VK_SPACE:
                if (gameLogic.isGamePaused()) {
                    gameLogic.setGamePaused(false);
                    MultimediaManager.playBackgroundSound(GameConstants.GAME_PLAY_MUSIC);
                } else {
                    gameLogic.setGamePaused(true);
                    MultimediaManager.playBackgroundSound(GameConstants.GAME_PAUSE_MUSIC);

                }
                break;
        }
    }

    /**
     * Sets up the game status labels including score, lives, time, and level.
     */
    private void setupLabels() {
        // Score Key Label
        scoreKeyLabel = createLabel("SCORE", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(scoreKeyLabel, GameConstants.SCORE_TEXT_POSITION_X, GameConstants.STATUS_LABEL_KEYS_Y, GameConstants.LABEL_WIDTH, GameConstants.STANDARD_LABEL_HEIGHT);

        // Score Value Label
        scoreValueLabel = createLabel("0", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(scoreValueLabel, GameConstants.SCORE_TEXT_POSITION_X, GameConstants.STATUS_LABEL_VALUES_Y, GameConstants.LABEL_WIDTH, GameConstants.VALUE_LABEL_HEIGHT);

        // Lives Key Label
        livesKeyLabel = createLabel("LIVES", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(livesKeyLabel, GameConstants.LIVES_TEXT_POSITION_X, GameConstants.STATUS_LABEL_KEYS_Y, GameConstants.LABEL_WIDTH, GameConstants.STANDARD_LABEL_HEIGHT);

        // Lives Value Label
        livesValueLabel = createLabel("0", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(livesValueLabel, GameConstants.LIVES_TEXT_POSITION_X, GameConstants.STATUS_LABEL_VALUES_Y, GameConstants.LABEL_WIDTH, GameConstants.VALUE_LABEL_HEIGHT);

        // Time Key Label
        timeKeyLabel = createLabel("TIME", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(timeKeyLabel, GameConstants.TIME_TEXT_POSITION_X, GameConstants.STATUS_LABEL_KEYS_Y, GameConstants.LABEL_WIDTH, GameConstants.STANDARD_LABEL_HEIGHT);

        // Time Value Label
        timeValueLabel = createLabel("00:00", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(timeValueLabel, GameConstants.TIME_TEXT_POSITION_X, GameConstants.STATUS_LABEL_VALUES_Y, GameConstants.LABEL_WIDTH, GameConstants.VALUE_LABEL_HEIGHT);

        // Level Key Label
        levelKeyLabel = createLabel("LEVEL", GameConstants.SCORE_KEY_TEXT_COLOR, GameConstants.SCORE_TEXT_KEY_FONT);
        setBackgroundAndPosition(levelKeyLabel, GameConstants.LEVEL_TEXT_POSITION_X, GameConstants.STATUS_LABEL_KEYS_Y, GameConstants.LABEL_WIDTH, GameConstants.STANDARD_LABEL_HEIGHT);

        // Level Value Label
        levelValueLabel = createLabel("0", GameConstants.SCORE_VALUE_TEXT_COLOR, GameConstants.SCORE_TEXT_VALUE_FONT);
        setBackgroundAndPosition(levelValueLabel, GameConstants.LEVEL_TEXT_POSITION_X, GameConstants.STATUS_LABEL_VALUES_Y, GameConstants.LABEL_WIDTH, GameConstants.VALUE_LABEL_HEIGHT);
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
     * @param label    The label to be adjusted.
     * @param positionX The X position.
     * @param positionY The Y position.
     * @param width    The width of the label.
     * @param height   The height of the label.
     */
    private void setBackgroundAndPosition(JLabel label, int positionX, int positionY, int width, int height) {
        label.setBounds(positionX, positionY, width, height);
    }

    /**
     * Sets up the exit button functionality utilizing the UIHelper's method for return button.
     */
    private void setupExitButton() {
            UIHelper.setupReturnButton(
                    panelSwitcher, backgroundContainer, 0, 0, GameConstants.EXIT_GAME,
                    GameConstants.MENU_MUSIC);
    }

    /**
     * Adjusts the layout of the game play area based on component dimensions.
     */
    @Override
    public void doLayout() {
        super.doLayout();
        int x = (this.getWidth() - gamePlayArea.getWidth()) / 2;
        gamePlayArea.setBounds(x, GameConstants.GAME_PLAY_AREA_Y, gamePlayArea.getWidth(), gamePlayArea.getHeight());
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
        MultimediaManager.playBackgroundSound("Menu");
    }

    /**
     * Overrides the paintComponent method to render game over and game paused graphics.
     *
     * @param g The graphics context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameOver(g);
        gamePaused(g);
    }

    /**
     * Handles action events, typically fired by timers or other periodic tasks.
     * In this context, it's used to periodically update the game status, render
     * the updated game area, and refresh the game visuals.
     *
     * @param e The action event details.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        gameLogic.gameLoop();
        updateGameStatus(gameLogic.getTimeSeconds(), gameLogic.getLives(), gameLogic.getScore(),
                         gameLogic.getLevel());
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
                gridLabels[x][y].setOpaque(true);
                switch (type) {
                    case SNAKE:
                        gridLabels[x][y].setBackground(Color.BLACK);
                        break;
                    case APPLE:
                        gridLabels[x][y].setBackground(Color.RED);
                        break;
                    case CHERRY:
                        gridLabels[x][y].setBackground(Color.MAGENTA);
                        break;
                    case ORANGE:
                        gridLabels[x][y].setBackground(Color.ORANGE);
                        break;
                    case WATERMELON:
                        gridLabels[x][y].setBackground(Color.YELLOW);
                        break;
                    case SPOILED_FOOD:
                        gridLabels[x][y].setBackground(Color.BLUE);
                        break;
                    case VACANT:
                        gridLabels[x][y].setOpaque(false);
                        break;
                }
            }
        }
    }

    /**
     * Renders the "Game Over" graphics and controls the visibility of the "Play Again" and "Exit" buttons
     * when the game is over. If the game is not over, it ensures the mentioned buttons remain hidden.
     *
     * @param g The graphics context used for rendering.
     */
    private void gameOver(Graphics g) {
        if (gameLogic.isGameOver()) {
            // Display game over text
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over",
                         (GameConstants.GAME_PLAY_WIDTH - metrics.stringWidth("Game Over")) / 2,
                         GameConstants.GAME_PLAY_HEIGHT / 2);

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
                                                             });
                this.add(playAgainButton);
            }
            playAgainButton.setVisible(true);

            // Initialize and show the "Exit" button if not already present
            if (exitButton == null) {
                exitButton = ButtonFactory.createButton("Exit",
                                                        (GameConstants.GAME_PLAY_WIDTH - 150) / 2,
                                                        (GameConstants.GAME_PLAY_HEIGHT / 2) + 120,
                                                        150, 50,
                                                        e -> System.exit(0));
                this.add(exitButton);
            }
            exitButton.setVisible(true);
        } else {
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
     * when the game is paused. If the game is not paused, it ensures the "Resume Game" button remains hidden.
     *
     * @param g The graphics context used for rendering.
     */
    private void gamePaused(Graphics g) {
        if (gameLogic.isGamePaused()) {
            // Display game paused text
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Paused",
                         (GameConstants.GAME_PLAY_WIDTH - metrics.stringWidth("Game Paused")) / 2,
                         GameConstants.GAME_PLAY_HEIGHT / 2);

            // Initialize and show the "Resume Game" button if not already present
            if (resumeGameButton == null) {
                resumeGameButton = ButtonFactory.createButton("Resume Game",
                                                              (GameConstants.GAME_PLAY_WIDTH - 180) / 2,
                                                              (GameConstants.GAME_PLAY_HEIGHT / 2) + 50,
                                                              180, 50,
                                                              e -> {
                                                                  gameLogic.setGamePaused(false);
                                                                  resumeGameButton.setVisible(false);
                                                              });
                this.add(resumeGameButton);
            }
            resumeGameButton.setVisible(true);
        } else {
            // Hide the "Resume Game" button if the game is not paused
            if (resumeGameButton != null) {
                resumeGameButton.setVisible(false);
            }
        }
    }
}
