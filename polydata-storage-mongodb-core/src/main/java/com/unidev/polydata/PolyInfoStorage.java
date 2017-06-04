package com.unidev.polydata;

import static com.unidev.polydata.MongodbStorage.POLY_COLLECTION;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import java.util.Optional;
import org.bson.Document;

/**
 * Storage for polydata information records.
 */
public class PolyInfoStorage extends AbstractPolyStorage {

  public PolyInfoStorage(MongoClient mongoClient, String mongoDatabase) {
    super(mongoClient, mongoDatabase);
  }

  public Optional<PolyInfo> polyInfo(String poly) {
    MongoCollection<Document> collection = mongoClient.getDatabase(mongoDatabase)
        .getCollection(POLY_COLLECTION);

    Optional<Document> rawDocument = fetchRawDocument(collection, poly);
    return rawDocument.map(PolyInfo::new);
  }

  public PolyInfo savePolyInfo(PolyInfo polyInfo) {
    MongoCollection<Document> collection = mongoClient.getDatabase(mongoDatabase)
        .getCollection(POLY_COLLECTION);
    save(collection, polyInfo);
    return polyInfo;
  }

  protected void migrate(String poly) {

  }

}
