package com.unidev.polydata;

import static com.unidev.polydata.MongodbStorage.COUNT_KEY;
import static com.unidev.polydata.MongodbStorage.TAGS_COLLECTION;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.unidev.changesexecutor.core.ChangesCore;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.polydata.changes.MongoChangesResultStorage;
import com.unidev.polydata.changes.MongodbChange;
import com.unidev.polydata.changes.tags.TagsCountIndex;
import com.unidev.polydata.domain.BasicPoly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bson.Document;

/**
 * Storage for tags
 */
public class TagStorage extends AbstractPolyStorage {

  public TagStorage(MongoClient mongoClient, String mongoDatabase) {
    super(mongoClient, mongoDatabase);
  }

  protected void migrate(String poly) {
    MongoChangesResultStorage mongoChangesResultStorage = new MongoChangesResultStorage(mongoClient,
        mongoDatabase, poly + "." + TAGS_COLLECTION + ".changes");
    ChangesCore changesCore = new ChangesCore(mongoChangesResultStorage);
    changesCore.addChange(new TagsCountIndex());

    ChangeContext changeContext = new ChangeContext();
    changeContext.put(MongodbChange.MONGO_CLIENT_KEY, mongoClient);
    changeContext.put(MongodbChange.DATABASE_KEY, mongoDatabase);
    changeContext.put(TagsCountIndex.COLLECTION_KEY, fetchCollection(poly));

    changesCore.executeChanges(changeContext);

  }

  public String fetchCollectionName(String poly) {
    return poly + "." + TAGS_COLLECTION;
  }

  public MongoCollection<Document> fetchCollection(String poly) {
    return mongoClient.getDatabase(mongoDatabase).getCollection(fetchCollectionName(poly));
  }

  public List<BasicPoly> listTags(String poly) {
    MongoCollection<Document> documentMongoCollection = fetchCollection(poly);
    List<BasicPoly> tags = new ArrayList<>();
    documentMongoCollection.find().iterator().forEachRemaining(document -> {
      BasicPoly tag = new BasicPoly();
      tag.putAll(document);
      tags.add(tag);
    });

    return tags;
  }

  /**
   * Save and increment tag count
   *
   * @return Saved poly tag
   */
  public BasicPoly addTag(String poly, BasicPoly tag) {
    if (!exist(poly, tag._id())) {
      tag.put(COUNT_KEY, 1);
      return save(poly, tag);
    }
    BasicPoly storedTag = fetchPoly(poly, tag._id()).get();
    int count = storedTag.fetch(COUNT_KEY, 1);
    storedTag.putAll(tag);
    storedTag.put(COUNT_KEY, count + 1);
    return update(poly, storedTag);
  }

  public void addTag(String poly, Collection<BasicPoly> tags) {
    tags.forEach(tag -> addTag(poly, tag));
  }

  /**
   * Remove tag from counting, decrementing tag count, if count reaches 0 - remove poly.
   *
   * @return Updated poly tag
   */
  public Optional<BasicPoly> removeTag(String poly, String id) {
    if (!exist(poly, id)) {
      return Optional.empty();
    }

    BasicPoly storedTag = fetchPoly(poly, id).get();
    int count = storedTag.fetch(COUNT_KEY, 1);
    count--;
    storedTag.put(COUNT_KEY, count);
    if (count == 0) {
      removePoly(poly, id);
      return Optional.of(storedTag);
    }
    return Optional.of(update(poly, storedTag));

  }

  public void removeTag(String poly, Collection<BasicPoly> tags) {
    tags.stream().map(tag -> tag._id()).forEach(id -> removeTag(poly, id));
  }

  public boolean exist(String poly, String id) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return exist(collection, id);
  }

  protected BasicPoly save(String poly, BasicPoly basicPoly) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return save(collection, basicPoly);
  }

  protected BasicPoly update(String poly, BasicPoly basicPoly) {
    MongoCollection<Document> collection = fetchCollection(poly);
    return update(collection, basicPoly);
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
