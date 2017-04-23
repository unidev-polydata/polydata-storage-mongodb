package com.unidev.polydata.changes.tags;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.polydata.MongodbStorage;
import com.unidev.polydata.changes.MongodbChange;
import org.bson.Document;

public class TagsCountIndex extends MongodbChange {

    public TagsCountIndex() {
        super(100L, "tag-count-index");
    }

    @Override
    public void execute(ChangeContext changeContext) {

        MongoClient mongoClient = fetchMongoClient(changeContext);
        String database = fetchDatabase(changeContext);

        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(MongodbStorage.TAGS_COLLECTION);
        Document index = new Document();
        index.put("count", 1);
        collection.createIndex(index);
    }
}
