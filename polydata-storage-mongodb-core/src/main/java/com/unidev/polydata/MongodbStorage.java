package com.unidev.polydata;


import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;


/**
 * Storage for mongodb records
 */
public class MongodbStorage {

  public static final String TAGS_COLLECTION = "tags";
  public static final String TAG_INDEX_COLLECTION = "tagindex";

  private MongoClient mongoClient;
  private String database;

  private PolyInfoStorage polyInfoStorage;
  private PolyRecordStorage polyDataStorage;

  public MongodbStorage(MongoClient mongoClient, String database) {
    this.mongoClient = mongoClient;
    this.database = database;

    this.polyInfoStorage = new PolyInfoStorage(mongoClient, database);
    this.polyDataStorage = new PolyRecordStorage(mongoClient, database);
  }

  public boolean existTag(String tagId) {
    MongoCollection<Document> tagsCollection = fetchTagsCollection(tagId);
    return exist(tagsCollection, tagId);
  }

  boolean exist(MongoCollection<Document> collection, String id) {
    long count = collection.count(eq("_id", id));
    if (count == 0) {
      return false;
    }
    return true;
  }

  public MongoCollection<Document> fetchTagsCollection(String poly) {
    return mongoClient.getDatabase(database).getCollection(poly + "." + TAGS_COLLECTION);
  }

  public MongoCollection<Document> fetchTagIndexCollection(String poly, String tagIndex) {
    return mongoClient.getDatabase(database)
        .getCollection(poly + "." + TAG_INDEX_COLLECTION + "." + tagIndex);
  }


}
