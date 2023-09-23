package minigames.server;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bson.Document;
import org.json.simple.*;
import org.json.simple.parser.*;

public class SurveyMongoDBTests {
    private MongoDB mongoDB;

    @BeforeEach
    void setUp() {
        mongoDB = new MongoDB();
    }

    @Test
    void testInsertDocument() {
    // Create a test document
    Document testDocument = new Document("name", "TestUser")
            .append("score", 100);

    // Insert the document into MongoDB
    JSONArray result = mongoDB.insertDocument("testCollection", testDocument);

    // Check if the insertion was successful
    assertNotNull(result);
    assertEquals(1, result.size());

    // Retrieve the inserted document from MongoDB
    JSONArray retrievedDocuments = mongoDB.getAllDocuments("testCollection");

    // Assert that the retrieved document matches the test document
    assertNotNull(retrievedDocuments);
    assertEquals(1, retrievedDocuments.size());
    JSONObject retrievedDocument = (JSONObject) retrievedDocuments.get(0);
    assertEquals("TestUser", retrievedDocument.get("name"));
    assertEquals(100L, retrievedDocument.get("score"));
}

}
