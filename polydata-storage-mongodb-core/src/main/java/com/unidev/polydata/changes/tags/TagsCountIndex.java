package com.unidev.polydata.changes.tags;


import static com.unidev.polydata.MongodbStorage.COUNT_KEY;

import com.mongodb.client.MongoCollection;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.polydata.changes.MongodbChange;
import org.bson.Document;

/**
 * Change for adding index on tag count field for poly tags.
 */
public class TagsCountIndex extends MongodbChange {

  public TagsCountIndex() {
    super(100L, "tag-count-index");
  }

  @Override
  public void execute(ChangeContext changeContext) {
    MongoCollection<Document> collection = (MongoCollection<Document>) changeContext
        .get(TagsCountIndex.COLLECTION_KEY);
    Document index = new Document();
    index.put(COUNT_KEY, 1);
    collection.createIndex(index);
  }
}
