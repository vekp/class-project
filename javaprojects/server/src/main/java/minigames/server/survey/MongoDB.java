/***************************************************************************************
*    Much of the following code was sourced from MongoDB Documentation
*    Date Sourced: September 2023
*    Code version: v4.10
*    Availability: https://www.mongodb.com/docs/drivers/java/sync/current/usage-examples/
***************************************************************************************/

package minigames.server;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MongoDB {
    private MongoClient mongoClient;
    private MongoDatabase database;

    // Establish a connection to Mongo database
    public MongoDB() {
        // To see the database and it's relation, download MongoDB Compass UI and paste the Connection String below to connect
        ConnectionString connString = new ConnectionString("mongodb+srv://cosc220:WxsFWGOiEuilbtkj@cluster0.sk5iqrw.mongodb.net/?retryWrites=true&w=majority");
        this.mongoClient = MongoClients.create(connString);
        this.database = mongoClient.getDatabase("une-db");
    }

    // Add new data to Database
    public String insertDocument(String collectionName, Document document) {
        var collection = database.getCollection(collectionName);
        document.append("timestamp", this.getCurrentTimestamp());
        collection.insertOne(document);

        JSONParser jsonParser = new JSONParser();
        JSONObject insertedDocument;

        try {
            insertedDocument = (JSONObject) jsonParser.parse(document.toJson());
        } catch (ParseException e) {
            e.printStackTrace();
            return null; 
        }

        // Remove the object in _id and only leave the _id string
        Object idField = insertedDocument.get("_id");
        if (idField instanceof JSONObject) {
            JSONObject idObject = (JSONObject) idField;
            if (idObject.containsKey("$oid")) {
                insertedDocument.put("_id", idObject.get("$oid"));
            }
        }

        return insertedDocument.toJSONString();
    }

    // Gets all entries in a collection (table)
    public JSONArray getAllDocuments(String collectionName) {
        JSONArray jsonArray = new JSONArray();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // Get all documents in the collection
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(document.toJson());

                    // Remove the object in _id and only leave the _id string
                    Object idField = jsonObject.get("_id");
                    if (idField instanceof JSONObject) {
                        JSONObject idObject = (JSONObject) idField;
                        if (idObject.containsKey("$oid")) {
                            jsonObject.put("_id", idObject.get("$oid"));
                        }
                    }
                    jsonArray.add(jsonObject);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }        

    // Gets all entries in a collection (table)
    public JSONArray getAllDocumentsByGameId(String collectionName, String gameId) {
        JSONArray jsonArray = new JSONArray();
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // Get all documents in the collection
        try (MongoCursor<Document> cursor = collection.find(eq("game_id", new ObjectId(gameId))).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(document.toJson());

                    jsonArray.add(jsonObject);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }  

    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
