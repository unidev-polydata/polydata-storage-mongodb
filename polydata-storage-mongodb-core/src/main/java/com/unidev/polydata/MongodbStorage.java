package com.unidev.polydata;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;


/**
 * Storage for mongodb records
 */
public class MongodbStorage {

    public static final String POLY_COLLECTION = "polys";

    private MongoClient mongoClient;
    private String database;

    public MongodbStorage(MongoClient mongoClient, String database) {

        this.mongoClient = mongoClient;
        this.database = database;
    }

    public PolyInfo polyInfo(String poly) {
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(POLY_COLLECTION);
        Document document = collection.find(eq("_id", poly)).first();
        return new PolyInfo(document);
    }
}
