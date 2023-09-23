
package minigames.client.gameshow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.vertx.core.json.JsonObject;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import minigames.client.gameshow.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

public class ImageGuesserTests {

  @Mock
  public ImageGuesser imageGuesser;

  @Mock
  private JTextField guessField;

  @Mock
  private GameTimer gameTimer;

  @Mock
  public GameShow gs;

  @Mock
  private Font pixelFont;

  @Mock
  private GameShowUI gameShowUI;

  @Mock
  private JPanel mockedGameContainer;

  @Mock
  private JPanel mockedOutcomeContainer;

  @Mock
  private JPanel mockedInputPanel;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    gs.outcomeContainer = mockedOutcomeContainer;
    gs.inputPanel = mockedInputPanel;
    gs.gameTimer = mock(GameTimer.class);
    gs.round = 1;
    gameShowUI.pixelFont = mock(Font.class);
    gameShowUI.gameContainer = mockedGameContainer;
  }

  @Test
  public void testClearGameContainer() {

    imageGuesser.clearGameContainer();

    // Verify that the necessary methods on the mocked container were called
    verify(mockedGameContainer).removeAll();
    verify(mockedGameContainer).validate();
    verify(mockedGameContainer).repaint();
  }

  @Test
  public void testShowWholeImage() {
    // Set up the test environment
    GridPanel gridPanel = mock(GridPanel.class);
    when(gridPanel.isCellVisible(1, 1)).thenReturn(false);

    // Set the gridPanel in the ImageGuesser class
    ImageGuesser.gridPanel = gridPanel;

    ImageGuesser.showWholeImage();

    // Verify that setFadeCell was called for each cell
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 10; y++) {
        verify(gridPanel).setFadeCell(x, y);
      }
    }
  }

  @Test
  void testLoadAndDisplayImage() {
    ImageGuesser.loadAndDisplayImage(
      "gameshow_image_2.jpg"
    );

    verify(mockedGameContainer)
      .add(imageGuesser.gridPanel, BorderLayout.CENTER);
  }

  @Test
  public void testValidateAndRepaintGameContainer() {
    imageGuesser.validateAndRepaintGameContainer();
    verify(mockedGameContainer).validate();
    verify(mockedGameContainer).repaint();
  }

  @Test
  public void testClearOutcomeContainer() {
    imageGuesser.clearOutcomeContainer(gs);
    verify(mockedOutcomeContainer).removeAll();
    verify(mockedOutcomeContainer).validate();
    verify(mockedOutcomeContainer).repaint();
  }

  @Test
  public void testDisplayTryAgainMessage() {
    when(GameShowUI.pixelFont.deriveFont(15f)).thenReturn(pixelFont);

    ImageGuesser.displayTryAgainMessage(gs);

    // Verify that the JLabel was added to the JPanel with the correct text and font
    verify(mockedOutcomeContainer)
      .add(
        argThat(label -> {
          if (label instanceof JLabel) {
            JLabel tryAgainLabel = (JLabel) label;
            return (
              "That's not quite right :( Try again!".equals(
                  tryAgainLabel.getText()
                ) &&
              pixelFont == tryAgainLabel.getFont()
            );
          }
          return false;
        }),
        eq(BorderLayout.CENTER)
      );
  }

  @Test
  void testHandleCorrectGuess() {
    when(gs.gameTimer.calculateScore()).thenReturn(42);

    imageGuesser.handleCorrectGuess(gs);

    verify(gs.gameTimer).stop();

    // Verify that JLabel and JButton were created and added
    ArgumentCaptor<JLabel> congratsLabelCaptor = ArgumentCaptor.forClass(
      JLabel.class
    );
    ArgumentCaptor<JButton> nextRoundButtonCaptor = ArgumentCaptor.forClass(
      JButton.class
    );
    verify(mockedOutcomeContainer)
      .add(congratsLabelCaptor.capture(), eq(BorderLayout.CENTER));
    verify(mockedOutcomeContainer)
      .add(nextRoundButtonCaptor.capture(), eq(BorderLayout.PAGE_END));

    JLabel capturedCongratsLabel = congratsLabelCaptor.getValue();
    JButton capturedNextRoundButton = nextRoundButtonCaptor.getValue();

    assertEquals(
      "Congratulations! You Win :)",
      capturedCongratsLabel.getText()
    );
    assertEquals("Next round ->", capturedNextRoundButton.getText());
  }

  @Test
  void testValidateAndRepaintInputPanel() {
        imageGuesser.validateAndRepaintInputPanel(gs);
        verify(mockedInputPanel).validate();
        verify(mockedInputPanel).repaint();
    }

  @Test
  void testSendGuess() {
    String guess = "testGuess";
    int round = gs.round;
    when(gs.gameTimer.calculateScore()).thenReturn(100);

    imageGuesser.sendGuess(gs, guess);
    // Verify that the expected command was sent to gameShow
    verify(gs)
      .sendCommand(
        argThat(jsonObject ->
          jsonObject.getString("command").equals("guess") &&
          jsonObject.getString("game").equals("ImageGuesser") &&
          jsonObject.getString("guess").equals(guess) &&
          jsonObject.getInteger("round").equals(round) &&
          jsonObject.getInteger("score").equals(gs.gameTimer.calculateScore())
        )
      );
  }
}
