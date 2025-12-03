package org.tyler.passprotector.passprotector;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseAccess {


    private static String connectionString = "";
    private static final String DBName = "password-protector";
    private static final String CollectionName = "filePasswordData";

    // Connects to database and returns boolean for if connection happened
    public static boolean connectDB() {
        // Read for connection string
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectionString = props.getProperty("MONGO_URI");

        // Connect
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase(DBName);
                database.runCommand(new Document("ping", 1));

                // Check if DB Exists
                boolean exists = false;
                for (String name : mongoClient.listDatabaseNames()) {
                    if (name.equals(DBName)) {
                        exists = true;
                        break;
                    }
                }

                // Step 3: If not, create by writing a document
                if (!exists) {
                    MongoCollection<Document> col =
                            database.getCollection(CollectionName);
                    col.insertOne(new Document("initialized", true));
                }

                return true; // Successfully connected = true
            } catch (MongoException e) {
                //e.printStackTrace();
                return false; // Unsuccessful at connecting = false
            }
        }
    }

    // Adds file password data to DB
    public static void newFileDataInsert(String fileName, String filePW, String salt, String iv) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoCollection<Document> coll = mongoClient.getDatabase(DBName).getCollection(CollectionName);

            // Hash Password before storage
            filePW = BCrypt.hashpw(filePW, BCrypt.gensalt());

            Document doc = new Document("fileName", fileName).append("password", filePW).append("salt", salt).append("iv", iv);

            coll.insertOne(doc);
        }
    }

    // Deletes a file's password data by name from DB
    public static void deleteFileData(String fileName) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoCollection<Document> coll = mongoClient.getDatabase(DBName).getCollection(CollectionName);

            Document doc = getFileDataByName(fileName);

            coll.deleteOne(doc);
        }
    }

    // Searches for file & retrieves data based on name
    public static Document getFileDataByName(String fileName) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoCollection<Document> coll = mongoClient.getDatabase(DBName).getCollection(CollectionName);

            Bson filter = new Document("fileName", fileName);

            return coll.find(filter).first();
        }
    }

    // Renames file with new name by searching with original file name
    public static void updateFileName(String oldFileName, String newFileName) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoCollection<Document> coll = mongoClient.getDatabase(DBName).getCollection(CollectionName);

            // updateOne(<filter>, <part of doc>);
            coll.updateOne(new Document("fileName", oldFileName), new Document("$set", new Document("fileName", newFileName)));
        }
    }

    // Replaces file password information by searching with original file name
    public static void updateFilePassword(String fileName, String filePW, String salt, String iv) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoCollection<Document> coll = mongoClient.getDatabase(DBName).getCollection(CollectionName);

            // Hash Password before storage
            filePW = BCrypt.hashpw(filePW, BCrypt.gensalt());

            Document newDoc = new Document("fileName", fileName).append("password", filePW).append("salt", salt).append("iv", iv);
            // replaceOne(<filter>, <doc>);
            coll.replaceOne(new Document("fileName", fileName), newDoc);
        }
    }


    // Checks if given password and stored hashed password are the same
    public static boolean verifyPassword(String fileName, String filePW) {
        Document fileData = getFileDataByName(fileName);
        String dbFilePW = (String) fileData.get("password");

        return BCrypt.checkpw(filePW, dbFilePW);
    }
}



