package com.unidev.polydata;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Storage for tags
 */
public class TagStorage extends AbstractPolyStorage {

  public static final String TAGS_COLLECTION = "tags";

  public TagStorage(MongoClient mongoClient, String mongoDatabase) {
    super(mongoClient, mongoDatabase);
  }

  private MongoCollection<Document> fetchTagsCollection(String poly) {
    return mongoClient.getDatabase(mongoDatabase).getCollection(poly + "." + TAGS_COLLECTION);
  }


}
