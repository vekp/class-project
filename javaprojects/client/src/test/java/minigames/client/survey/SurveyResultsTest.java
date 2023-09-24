package minigames.client.survey;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import minigames.client.MinigameNetworkClient;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.awt.Color;

public class SurveyResultsTest {
    private SurveyResults surveyResults;

    @Mock
    private MinigameNetworkClient mnClient;

    private String gameId = "123"; 

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        surveyResults = new SurveyResults(mnClient, gameId);
    }

    @Test
    public void testReadBackgroundImage() {
        assertNotNull(surveyResults.readBackgroundImage());
    }

    @Test
    public void testLabelColourChange() {
        Color newBackgroundColor = Color.RED;
        Color newForegroundColor = Color.WHITE;

        surveyResults.labelColourChange(newBackgroundColor, newForegroundColor);

        assertEquals(newBackgroundColor, surveyResults.getBackground());
        assertEquals(newForegroundColor, surveyResults.getForeground());
    }

    @Test
    public void testPanelColourChange() {
        Color newBackgroundColor = Color.GREEN;
        Color newForegroundColor = Color.BLUE;

        surveyResults.panelColourChange(newBackgroundColor, newForegroundColor);

        assertEquals(newBackgroundColor, surveyResults.getBackground());
        assertEquals(newForegroundColor, surveyResults.getForeground());
    }
}
