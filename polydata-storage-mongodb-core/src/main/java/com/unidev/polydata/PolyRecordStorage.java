package com.unidev.polydata;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Optional;
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
    MongoCollection<Document> collection = fetchCollection(poly);
    Optional<Document> document = fetchRawDocument(collection, id);
    if (document.isPresent()) {
      return Optional.of(new PolyRecord(document.get()));
    }
    return Optional.empty();
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

  }

}
