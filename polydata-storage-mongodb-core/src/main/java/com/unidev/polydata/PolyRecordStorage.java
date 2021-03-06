package com.unidev.polydata;

import static com.mongodb.client.model.Filters.in;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.unidev.changesexecutor.core.ChangesCore;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.polydata.changes.MongoChangesResultStorage;
import com.unidev.polydata.changes.MongodbChange;
import com.unidev.polydata.changes.generic.DateIndex;
import com.unidev.polydata.changes.generic.TagsIndex;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.bson.Document;

/**
 * Storage for save polydata records.
 */
public class PolyRecordStorage extends AbstractPolyStorage {

    public PolyRecordStorage(MongoClient mongoClient, String mongoDatabase) {
        super(mongoClient, mongoDatabase);
    }

    public MongoCollection<Document> fetchCollection(String poly) {
        return mongoClient.getDatabase(mongoDatabase).getCollection(poly);
    }

    public BasicPoly save(String poly, BasicPoly basicPoly) {
        MongoCollection<Document> collection = fetchCollection(poly);
        return save(collection, basicPoly);
    }

    public Optional<PolyRecord> fetchPoly(String poly, String id) {
        return fetchPoly(poly, id, PolyRecord::new);
    }

    public Optional<PolyRecord> fetchPoly(String poly, String id,
        Function<Document, PolyRecord> mappingFunction) {
        MongoCollection<Document> collection = fetchCollection(poly);
        Optional<Document> document = fetchRawDocument(collection, id);
        return document.map(mappingFunction::apply);
    }

    public Map<String, PolyRecord> fetchPoly(String poly, Collection<String> id) {
        return fetchPoly(poly, id, PolyRecord::new);
    }

    public Map<String, PolyRecord> fetchPoly(String poly, Collection<String> id,
        Function<Document, PolyRecord> mappingFunction) {
        Map<String, PolyRecord> result = new HashMap<>();
        MongoCollection<Document> collection = fetchCollection(poly);
        FindIterable<Document> documents = collection.find(in("_id", id));
        for (Document document : documents) {
            PolyRecord polyRecord = mappingFunction.apply(document);
            result.put(polyRecord._id(), polyRecord);
        }
        return result;
    }

    public boolean existPoly(String poly, String id) {
        MongoCollection<Document> collection = fetchCollection(poly);
        return exist(collection, id);
    }

    public boolean removePoly(String poly, String id) {
        MongoCollection<Document> collection = fetchCollection(poly);
        return removePoly(collection, id);
    }

    protected void migrate(String poly) {
        MongoChangesResultStorage mongoChangesResultStorage = new MongoChangesResultStorage(
            mongoClient,
            mongoDatabase, poly + ".changes");
        ChangesCore changesCore = new ChangesCore(mongoChangesResultStorage);
        changesCore.addChange(new DateIndex());
        changesCore.addChange(new TagsIndex());

        ChangeContext changeContext = new ChangeContext();
        changeContext.put(MongodbChange.MONGO_CLIENT_KEY, mongoClient);
        changeContext.put(MongodbChange.DATABASE_KEY, mongoDatabase);
        changeContext.put(MongodbChange.COLLECTION_KEY, fetchCollection(poly));

        changesCore.executeChanges(changeContext);
    }

}
