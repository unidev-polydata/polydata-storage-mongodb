package com.unidev.polydata;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.polydata.domain.BasicPoly;
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

    public MongoCollection<Document> fetchCollection(String poly) {
        return mongoClient.getDatabase(database).getCollection(poly);
    }

    public BasicPoly save(String poly, BasicPoly basicPoly) {
        MongoCollection<Document> collection = fetchCollection(poly);
        Document document = new Document(basicPoly);
        collection.insertOne(document);
        return basicPoly;
    }

    public Optional<BasicPoly> fetchPoly(String poly, String id) {
        return fetchRawPoly(poly, id);
    }

    protected Optional<BasicPoly> fetchRawPoly(String poly, String id) {
        MongoCollection<Document> collection = fetchCollection(poly);
        Document document = collection.find(eq("_id", id)).first();
        if (document == null) {
            return Optional.empty();
        }
        BasicPoly basicPoly = new BasicPoly();
        basicPoly.putAll(document);
        return Optional.of(basicPoly);
    }


    public MongoCollection<Document> fetchTagsCollection(String poly) {
        return mongoClient.getDatabase(database).getCollection(poly + ".tags");
    }

    public MongoCollection<Document> fetchTagIndexCollection(String poly, String tagIndex) {
        return mongoClient.getDatabase(database).getCollection(poly + ".tagindex." + tagIndex);
    }


}
