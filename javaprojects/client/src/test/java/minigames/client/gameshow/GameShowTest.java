package minigames.client.gameshow;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.vertx.core.json.JsonObject;

import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.MinigameNetworkClientWindow;

import minigames.rendering.GameMetadata;

public class GameShowTest {

    @Mock
    private MinigameNetworkClient mockMnClient;

    @Mock
    private GameMetadata mockGameMetadata;

    @Mock
    private MinigameNetworkClientWindow mockMnWindow;

    @Mock
    private WordScramble wordScramble;

    @Mock
    private ImageGuesser imageGuesser;


    private GameShow gameShow;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameShow = new GameShow();
    }

    @Test
    public void testLoad() {
        gameShow.load(mockMnClient, mockGameMetadata, "TestPlayer");

        when(mockMnClient.getMainWindow()).thenReturn(mockMnWindow);

        // Verify that the load method interacts with mnClient and sets the player name
        verify(mockMnClient).getMainWindow();
        verify(mockMnWindow).addCenter(any());
        verify(mockMnWindow).pack();
        assertEquals("TestPlayer", gameShow.player);
    }

    @Test
    public void testSendCommand() {
        gameShow.load(mockMnClient, mockGameMetadata, "TestPlayer");

        JsonObject commandJson = new JsonObject().put("command", "testCommand");
        gameShow.sendCommand(commandJson);

        // Verify that sendCommand sends the JSON object to mnClient
        verify(mockMnClient).send(any());
    }

    @Test
    public void testExecute_startGame() {
        JsonObject commandJson = new JsonObject().put("command", "startGame").put("game", "ImageGuesser");

        gameShow.execute(mockGameMetadata, commandJson);
        Mockito.verify(gameShow).startGame(commandJson);
    }

    @Test
    public void testExecute_nextRound() {
        JsonObject commandJson = new JsonObject().put("command", "nextRound");

        gameShow.execute(mockGameMetadata, commandJson);
        Mockito.verify(gameShow).startNextRound(commandJson);
    }

    @Test
    public void testExecute_guessOutcome() {
        JsonObject commandJson = new JsonObject().put("command", "guessOutcome");

        gameShow.execute(mockGameMetadata, commandJson);
        Mockito.verify(gameShow).processGuessOutcome(commandJson);
    }

    @Test
    public void testExecute_ready() {
        JsonObject commandJson = new JsonObject().put("command", "ready");

        gameShow.execute(mockGameMetadata, commandJson);
        Mockito.verify(gameShow).logReadyState(commandJson);
    }

    @Test
    public void testStartNextRound() {
        JsonObject commandJson = new JsonObject()
            .put("command", "nextRound")
            .put("game", "ImageGuesser")
            .put("round", 1);

        gameShow.startNextRound(commandJson);

        // Add assertions to verify the state of the gameShow object after starting the next round
        assertEquals(1, gameShow.round);
        assertNotNull(gameShow.gameTimer);
    }

    @Test
    public void testStartGame() {
        JsonObject commandJson = new JsonObject()
            .put("command", "startGame")
            .put("game", "ImageGuesser")
            .put("imageFilePath", "testImagePath");

        gameShow.startGame(commandJson);

        // Add assertions to verify the state of the gameShow object after starting the game
        assertNotNull(gameShow.gameTimer);
        assertTrue(gameShow.started);
    }


    @Test
    public void testCloseGame() {
        gameShow.closeGame();

        assertFalse(gameShow.isActive);
    }

}
