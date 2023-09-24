package minigames.client.survey;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import minigames.client.MinigameNetworkClient;

public class SurveyTest {
    private Survey survey;
    private MinigameNetworkClient mnClient;
    private String gameId = "123";
    private String gameName = "Test Game";

    @BeforeEach
    public void setUp() {
        mnClient = Mockito.mock(MinigameNetworkClient.class);
        survey = new Survey(mnClient, gameId, gameName);
    }

    @Test
    public void testIsValidTextWithValidText() {
        String validText = "This is valid text";
        assertTrue(survey.isValidText(validText));
    }

    @Test
    public void testIsValidTextWithInvalidText() {
        String invalidText = "Invalid text with DROP TABLE";
        assertFalse(survey.isValidText(invalidText));
    }

    @Test
    public void testSanitiseText() {
        String inputText = "Unsafe <script>alert('Hello')</script> text";
        String expectedSanitisedText = "Unsafe  text";
        String sanitisedText = survey.sanitiseText(inputText);
        assertEquals(expectedSanitisedText, sanitisedText);
    }

    @Test
    public void testGetSelectedRadioButtonValue() {
        survey.uiRatingThree.setSelected(true);
        String selectedValue = survey.getSelectedRadioButtonValue(survey.uiRatingButtonGroup);
        assertEquals("3", selectedValue);
    }
}