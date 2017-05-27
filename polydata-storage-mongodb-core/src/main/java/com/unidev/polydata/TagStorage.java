package com.unidev.polydata;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Optional;
import org.bson.Document;

/**
 * Storage for tags
 */
public class TagStorage extends AbstractPolyStorage {

  public static final String TAGS_COLLECTION = "tags";

  public TagStorage(MongoClient mongoClient, String mongoDatabase) {
    super(mongoClient, mongoDatabase);
  }

  private MongoCollection<Document> fetchCollection(String poly) {
    return mongoClient.getDatabase(mongoDatabase).getCollection(poly + "." + TAGS_COLLECTION);
  }

  public boolean exist(String poly, String id) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return exist(collection, id);
  }

  public BasicPoly save(String poly, BasicPoly basicPoly) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return save(collection, basicPoly);
  }

  public Optional<BasicPoly> fetchPoly(String poly, String id) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return fetchPoly(collection, id);
  }

  public boolean removePoly(String poly, String id) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return removePoly(collection, id);
  }

}
