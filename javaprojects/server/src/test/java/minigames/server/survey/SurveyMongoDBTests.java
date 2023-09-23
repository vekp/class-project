package minigames.server;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bson.Document;
import org.json.simple.*;
import org.json.simple.parser.*;
// import org.bson.types.ObjectId;

public class SurveyMongoDBTests {
    private MongoDB mongoDB;

    @BeforeEach
    void setUp() {
        mongoDB = new MongoDB();
    }

    @Test
    void testInsertDocument() {
        // Create a test document
        Document testDocument = new Document("game_id", "64fec6296849f97cdc19f017")
                .append("ui_rating", 3)
                .append("enjoyment_rating", 4)
                .append("functionality_rating", 5)
                .append("difficulty_rating", 1)
                .append("feedback_text", "Test text 123");

        // Insert the document into MongoDB
        JSONArray result = mongoDB.insertDocument("feedback", testDocument);

        // Check if the insertion was successful
        assertNotNull(result);
        assertEquals(1, result.size());

        // Retrieve the inserted document from MongoDB
        JSONArray retrievedDocuments = mongoDB.getAllDocuments("feedback");

        // Assert that the retrieved document matches the test document
        assertNotNull(retrievedDocuments);
        JSONObject retrievedDocument = (JSONObject) retrievedDocuments.get(0);
        // assertEquals(3, retrievedDocument.get("ui_rating"));
        // assertEquals(4, retrievedDocument.get("enjoyment_rating"));
        // assertEquals(5, retrievedDocument.get("functionality_rating"));
        // assertEquals(1, retrievedDocument.get("difficulty_rating"));
        // assertEquals("Test text 123", retrievedDocument.get("feedback_text"));

        String returnedDataIDString = (String) retrievedDocument.get("_id");

        boolean isSuccessfullyDeleted = mongoDB.deleteDocument("feedback", returnedDataIDString);
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------- IS THIS DELETED SUCCESSFULLY?" + isSuccessfullyDeleted);
        assertTrue(isSuccessfullyDeleted);
    }

}
