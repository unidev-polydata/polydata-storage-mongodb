package com.unidev.polydata;


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
    private final TagIndexStorage tagIndexStorage;
    private MongoClient mongoClient;
    private String database;

    public MongodbStorage(MongoClient mongoClient, String database) {
        this.mongoClient = mongoClient;
        this.database = database;

        this.polyInfoStorage = new PolyInfoStorage(mongoClient, database);
        this.polyRecordStorage = new PolyRecordStorage(mongoClient, database);
        this.tagStorage = new TagStorage(mongoClient, database);
        this.tagIndexStorage = new TagIndexStorage(mongoClient, database);
    }

    public void migrate(String poly) {
        polyInfoStorage.migrate(poly);
        polyRecordStorage.migrate(poly);
        tagStorage.migrate(poly);
        tagIndexStorage.migrate(poly);
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
            TagIndexStorage tagIndexStorage = getTagIndexStorage();
            polyRecord.fetchTags().forEach(tag -> {
                BasicPoly tagIndexRecord = BasicPoly.newPoly();
                tagIndexRecord._id(polyRecord._id());
                tagIndexRecord.put(TAG_KEY, tag);
                tagIndexStorage.addPolyIndex(poly, tag._id(), tagIndexRecord);
            });
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

        TagIndexStorage tagIndexStorage = getTagIndexStorage();
        dbPolyRecord.fetchTags().forEach(tag -> {
            tagIndexStorage.removePolyIndex(poly, tag._id(), dbPolyRecord._id());
        });
    }

    public Collection<PolyRecord> fetchRecords(String poly, PolyQuery polyQuery) {
        return fetchRecords(poly, polyQuery, PolyRecord::new);
    }

    public Collection<PolyRecord> fetchRecords(String poly, PolyQuery polyQuery,
        Function<Document, PolyRecord> mappingFunction) {
        if (polyQuery.getTag() == null) {
            MongoCollection<Document> collection = getPolyRecordStorage().fetchCollection(poly);
            FindIterable<Document> result = collection.find()
                .sort(new BasicDBObject().append(MongodbStorage.DATE_KEY, -1)).skip(
                    polyQuery.getPage() * polyQuery.getItemPerPage())
                .limit(polyQuery.getItemPerPage());
            List<PolyRecord> list = new ArrayList<>();
            result.iterator()
                .forEachRemaining(document -> list.add(mappingFunction.apply(document)));
            return list;
        } else {
            MongoCollection<Document> collection = getTagIndexStorage()
                .fetchCollection(poly, polyQuery.getTag());
            FindIterable<Document> result = collection.find()
                .sort(new BasicDBObject().append(MongodbStorage.DATE_KEY, -1)).skip(
                    polyQuery.getPage() * polyQuery.getItemPerPage())
                .limit(polyQuery.getItemPerPage());
            List<String> ids = new ArrayList<>();
            result.iterator().forEachRemaining(document -> ids.add(document.get("_id") + ""));
            return getPolyRecordStorage().fetchPoly(poly, ids, mappingFunction).values();
        }
    }

    public long countRecords(String poly, PolyQuery polyQuery) {
        if (polyQuery.getTag() == null) {
            MongoCollection<Document> collection = getPolyRecordStorage().fetchCollection(poly);
            return collection.count();
        } else {
            MongoCollection<Document> collection = getTagIndexStorage()
                .fetchCollection(poly, polyQuery.getTag());
            return collection.count();
        }
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

    public TagIndexStorage getTagIndexStorage() {
        return tagIndexStorage;
    }
}
