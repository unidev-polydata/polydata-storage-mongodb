package com.unidev.polydata.changes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.unidev.changesexecutor.core.ChangeResultStorage;
import com.unidev.changesexecutor.model.ChangeExecutionResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Mongodb storage for results of changes execution
 */
public class MongoChangesResultStorage implements ChangeResultStorage {

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper() {{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }};

    private MongoClient mongoClient;
    private String database;
    private String collection;

    public MongoChangesResultStorage(MongoClient mongoClient, String database, String collection) {
        this.mongoClient = mongoClient;
        this.database = database;
        this.collection = collection;
    }

    private MongoCollection<Document> fetchStorageCollection() {
        return mongoClient.getDatabase(database).getCollection(collection);
    }

    @Override
    public Collection<ChangeExecutionResult> listResults() {
        MongoCollection<Document> collection = fetchStorageCollection();
        List<ChangeExecutionResult> list = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                ChangeExecutionResult changeExecutionResult = documentToChangeResult(cursor.next());
                list.add(changeExecutionResult);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    @Override
    public boolean existChangeResult(String changeName) {
        MongoCollection<Document> collection = fetchStorageCollection();
        return collection.find(eq("change.changeName", changeName)).first() != null;
    }

    @Override
    public ChangeExecutionResult fetchResult(String changeName) {
        MongoCollection<Document> collection = fetchStorageCollection();
        Document document = collection.find(eq("change.changeName", changeName)).first();
        return documentToChangeResult(document);
    }

    @Override
    public ChangeExecutionResult persistResult(ChangeExecutionResult changeExecutionResult) {
        Document document = changeResultToDocument(changeExecutionResult);
        MongoCollection<Document> collection = fetchStorageCollection();
        collection.insertOne(document);
        return changeExecutionResult;
    }

    private Document changeResultToDocument(ChangeExecutionResult result) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(result);
            return OBJECT_MAPPER.readValue(json, Document.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ChangeExecutionResult documentToChangeResult(Document document) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(document);
            return OBJECT_MAPPER.readValue(json, ChangeExecutionResult.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
