package com.unidev.polydata;

import static com.unidev.polydata.MongodbStorage.TAG_INDEX_COLLECTION;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
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

  public BasicPoly addPolyIndex(String poly, String tagIndex, BasicPoly basicPoly) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    if (exist(collection, basicPoly._id())) {
      return update(collection, basicPoly);
    }
    return save(collection, basicPoly);
  }

  public void addPolyIndex(String poly, String tagIndex, Collection<BasicPoly> polys) {
    polys.stream().forEach(record -> addPolyIndex(poly, tagIndex, record));
  }

  public Optional<BasicPoly> fetchPoly(String poly, String tagIndex, String id) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    return fetchPoly(collection, id);
  }

  public boolean removePolyIndex(String poly, String tagIndex, String id) {
    MongoCollection<Document> collection = fetchCollection(poly, tagIndex);
    return removePoly(collection, id);
  }

  public void removePolyIndex(String poly, String tagIndex, Collection<BasicPoly> polys) {
    polys.stream().forEach(record -> removePolyIndex(poly, tagIndex, record._id()));
  }

  protected void migrate(String poly) {

  }


}
