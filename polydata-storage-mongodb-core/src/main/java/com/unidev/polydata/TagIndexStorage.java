package com.unidev.polydata;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Tag index storage.
 */
public class TagIndexStorage extends AbstractPolyStorage {

  public static final String TAG_INDEX_COLLECTION = "tagindex";

  public TagIndexStorage(MongoClient mongoClient, String mongoDatabase) {
    super(mongoClient, mongoDatabase);
  }

  public MongoCollection<Document> fetchTagIndexCollection(String poly, String tagIndex) {
    return mongoClient.getDatabase(mongoDatabase)
        .getCollection(poly + "." + TAG_INDEX_COLLECTION + "." + tagIndex);
  }


}
