package com.unidev.polydata.changes.generic;


import static com.unidev.polydata.MongodbStorage.DATE_KEY;

import com.mongodb.client.MongoCollection;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.polydata.changes.MongodbChange;
import org.bson.Document;

/**
 * Change for adding index on tag count field for poly tags.
 */
public class DateIndex extends MongodbChange {

  public DateIndex() {
    super(101L, "date-index-index");
  }

  @Override
  public void execute(ChangeContext changeContext) {
    MongoCollection<Document> collection = (MongoCollection<Document>) changeContext
        .get(DateIndex.COLLECTION_KEY);
    Document index = new Document();
    index.put(DATE_KEY, 1);
    collection.createIndex(index);
  }
}
