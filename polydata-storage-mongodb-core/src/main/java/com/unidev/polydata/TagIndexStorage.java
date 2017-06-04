package com.unidev.polydata;

import static com.unidev.polydata.MongodbStorage.TAG_INDEX_COLLECTION;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Optional;
import org.bson.Document;

/**
 * Tag index storage.
 */
public class TagIndexStorage extends AbstractPolyStorage {


  public TagIndexStorage(MongoClient mongoClient, String mongoDatabase) {
    super(mongoClient, mongoDatabase);
  }

  public MongoCollection<Document> fetchCollection(String poly, String tagIndex) {
    return mongoClient.getDatabase(mongoDatabase)
        .getCollection(poly + "." + TAG_INDEX_COLLECTION + "." + tagIndex);
  }

  public boolean exist(String poly, String tagIndex, String id) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    return exist(collection, id);
  }

  public BasicPoly save(String poly, String tagIndex, BasicPoly basicPoly) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    return save(collection, basicPoly);
  }

  public Optional<BasicPoly> fetchPoly(String poly, String tagIndex, String id) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    return fetchPoly(collection, id);
  }

  public boolean removePoly(String poly, String tagIndex, String id) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    return removePoly(collection, id);
  }

  protected void migrate(String poly) {

  }


}
