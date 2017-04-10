package com.unidev.polydata;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Optional;

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

    public Optional<PolyInfo> polyInfo(String poly) {
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(POLY_COLLECTION);
        Document document = collection.find(eq("_id", poly)).first();
        return document != null ? Optional.of(new PolyInfo(document)) : Optional.empty();
    }

    public PolyInfo savePolyInfo(PolyInfo polyInfo) {
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(POLY_COLLECTION);
        Document document = new Document();
        document.putAll(polyInfo);

        collection.insertOne(document);

        return polyInfo;
    }




}
