package com.unidev.polydata;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Optional;
import org.bson.Document;

/**
 * Abstract storage for polydata information.
 */
public abstract class AbstractPolyStorage {

  protected MongoClient mongoClient;
  protected String mongoDatabase;

  public AbstractPolyStorage(MongoClient mongoClient, String mongoDatabase) {
    this.mongoClient = mongoClient;
    this.mongoDatabase = mongoDatabase;
  }

  protected boolean exist(MongoCollection<Document> collection, String id) {
    long count = collection.count(eq("_id", id));
    return count != 0;
  }

  protected BasicPoly save(MongoCollection<Document> collection, BasicPoly basicPoly) {
    Document document = new Document(basicPoly);
    collection.insertOne(document);
    return basicPoly;
  }

  protected Optional<BasicPoly> fetchPoly(MongoCollection<Document> collection, String id) {
    Document document = collection.find(eq("_id", id)).first();
    if (document == null) {
      return Optional.empty();
    }
    BasicPoly basicPoly = new BasicPoly();
    basicPoly.putAll(document);
    return Optional.of(basicPoly);
  }

  protected Optional<Document> fetchRawDocument(MongoCollection<Document> collection, String id) {
    Document document = collection.find(eq("_id", id)).first();
    return Optional.ofNullable(document);
  }

  protected boolean removePoly(MongoCollection<Document> collection, String id) {
    if (!exist(collection, id)) {
      return false;
    }
    DeleteResult deleteResult = collection.deleteOne(eq("_id", id));
    return deleteResult.getDeletedCount() == 1L;
  }

}
