package minigames.client.useraccount;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SessionTest {

    @Test
    void testDefaultConstructor() {
        Session session = new Session();

        // Verify that the default constructor sets isActive to false
        assertFalse(session.getIsActive());
    }

    @Test
    void testParameterizedConstructor() {
        Session session = new Session(true);

        // Verify that the parameterized constructor sets isActive to true
        assertTrue(session.getIsActive());
    }

    @Test
    void testSetAndGetTimestamp() {
        Session session = new Session();
        String timestamp = "2023-09-16T12:34:56";

        session.setTimestamp(timestamp);

        // Verify that setTimestamp and getTimestamp work correctly
        assertEquals(timestamp, session.getTimestamp());
    }

    @Test
    void testSetAndGetIsActive() {
        Session session = new Session();
        boolean isActive = true;

        session.setIsActive(isActive);

        // Verify that setIsActive and getIsActive work correctly
        assertEquals(isActive, session.getIsActive());
    }

    @Test
    void testAddAndGetGames() {
        Session session = new Session();
        String gameName = "TestGame";
        String value = "100";

        session.addGame(gameName, value);

        // Verify that addGame and getGames work correctly
        assertEquals(1, session.getGames().size());
        assertEquals(gameName, session.getGames().get(0).getName());
    }


}