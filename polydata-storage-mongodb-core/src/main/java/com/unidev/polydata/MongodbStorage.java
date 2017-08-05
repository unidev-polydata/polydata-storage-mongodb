package com.unidev.polydata;


import static com.mongodb.client.model.Filters.in;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.unidev.polydata.domain.BasicPoly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.bson.Document;


/**
 * Storage for mongodb records
 */
public class MongodbStorage {

    public static final String TAG_KEY = "tag_id";
    public static final String TAGS_KEY = "tags";
    public static final String TAGS_COLLECTION = "tags";
    public static final String COUNT_KEY = "count";
    public static final String DATE_KEY = "_date";
    public static final String TAG_INDEX_COLLECTION = "tagindex";
    public static final String POLY_COLLECTION = "polys";

    private final PolyInfoStorage polyInfoStorage;
    private final PolyRecordStorage polyRecordStorage;
    private final TagStorage tagStorage;
    private MongoClient mongoClient;
    private String database;

    public MongodbStorage(MongoClient mongoClient, String database) {
        this.mongoClient = mongoClient;
        this.database = database;

        this.polyInfoStorage = new PolyInfoStorage(mongoClient, database);
        this.polyRecordStorage = new PolyRecordStorage(mongoClient, database);
        this.tagStorage = new TagStorage(mongoClient, database);
    }

    public void migrate(String poly) {
        polyInfoStorage.migrate(poly);
        polyRecordStorage.migrate(poly);
        tagStorage.migrate(poly);
    }

    /**
     * Store or update stored poly in polydata storage.
     */
    public void storePoly(String poly, PolyRecord polyRecord) {
        PolyRecordStorage polyRecordStorage = getPolyRecordStorage();
        if (polyRecordStorage.existPoly(poly, polyRecord._id())) {
            MongoCollection<Document> collection = polyRecordStorage.fetchCollection(poly);
            polyRecordStorage.update(collection, polyRecord);
        } else {
            polyRecordStorage.save(poly, polyRecord);
        }
        Collection<BasicPoly> tags = polyRecord.fetchTags();
        if (tags != null) {
            getTagStorage().addTag(poly, tags);
        }
    }

    /**
     * Remove poly from storage.
     */
    public void removePoly(String poly, String polyId) {
        Optional<PolyRecord> polyRecord = getPolyRecordStorage().fetchPoly(poly, polyId);
        if (!polyRecord.isPresent()) {
            return;
        }
        PolyRecord dbPolyRecord = polyRecord.get();
        PolyRecordStorage polyRecordStorage = getPolyRecordStorage();
        polyRecordStorage.removePoly(poly, polyId);
        getTagStorage().removeTag(poly, dbPolyRecord.fetchTags());
    }

    public Collection<PolyRecord> fetchRecords(String poly, PolyQuery polyQuery) {
        return fetchRecords(poly, polyQuery, PolyRecord::new);
    }

    public Collection<PolyRecord> fetchRecords(String poly, PolyQuery polyQuery,
        Function<Document, PolyRecord> mappingFunction) {
        FindIterable<Document> result;
        MongoCollection<Document> collection = getPolyRecordStorage().fetchCollection(poly);
        if (polyQuery.getTag() == null) {
            result = collection.find()
                .sort(new BasicDBObject().append(MongodbStorage.DATE_KEY, -1)).skip(
                    polyQuery.getPage() * polyQuery.getItemPerPage())
                .limit(polyQuery.getItemPerPage());

        } else {
            result = collection.find(in(TAGS_KEY + "._id", polyQuery.getTag()))
                .sort(new BasicDBObject().append(MongodbStorage.DATE_KEY, -1)).skip(
                    polyQuery.getPage() * polyQuery.getItemPerPage())
                .limit(polyQuery.getItemPerPage());
        }
        return processIterator(result, mappingFunction);
    }

    public Collection<PolyRecord> processIterator(FindIterable<Document> result,
        Function<Document, PolyRecord> mappingFunction) {
        List<PolyRecord> list = new ArrayList<>();
        result.iterator()
            .forEachRemaining(document -> list.add(mappingFunction.apply(document)));
        return list;
    }

    public long countRecords(String poly, PolyQuery polyQuery) {
        MongoCollection<Document> collection = getPolyRecordStorage().fetchCollection(poly);
        if (polyQuery.getTag() == null) {
            return collection.count();
        }

        return collection.count(in(TAGS_KEY + "._id", polyQuery.getTag()));
    }


    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public String getDatabase() {
        return database;
    }

    public PolyInfoStorage getPolyInfoStorage() {
        return polyInfoStorage;
    }

    public PolyRecordStorage getPolyRecordStorage() {
        return polyRecordStorage;
    }

    public TagStorage getTagStorage() {
        return tagStorage;
    }
}
