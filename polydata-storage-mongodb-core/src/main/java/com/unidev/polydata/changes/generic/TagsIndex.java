package com.unidev.polydata.changes.generic;


import static com.unidev.polydata.MongodbStorage.TAGS_KEY;

import com.mongodb.client.MongoCollection;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.polydata.changes.MongodbChange;
import org.bson.Document;

/**
 * Change for adding index on tag count field for poly tags.
 */
public class TagsIndex extends MongodbChange {

    public TagsIndex() {
        super(102L, "tags-index");
    }

    @Override
    public void execute(ChangeContext changeContext) {
        MongoCollection<Document> collection = (MongoCollection<Document>) changeContext
            .get(TagsIndex.COLLECTION_KEY);
        Document index = new Document();
        index.put(TAGS_KEY, 1);
        collection.createIndex(index);
    }
}
