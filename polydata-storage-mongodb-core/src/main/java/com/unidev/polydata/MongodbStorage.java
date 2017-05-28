package com.unidev.polydata;


import com.mongodb.MongoClient;


/**
 * Storage for mongodb records
 */
public class MongodbStorage {

  private final PolyInfoStorage polyInfoStorage;
  private final PolyRecordStorage polyRecordStorage;
  private final TagStorage tagStorage;
  private final TagIndexStorage tagIndexStorage;
  private MongoClient mongoClient;
  private String database;

  public MongodbStorage(MongoClient mongoClient, String database) {
    this.mongoClient = mongoClient;
    this.database = database;

    this.polyInfoStorage = new PolyInfoStorage(mongoClient, database);
    this.polyRecordStorage = new PolyRecordStorage(mongoClient, database);
    this.tagStorage = new TagStorage(mongoClient, database);
    this.tagIndexStorage = new TagIndexStorage(mongoClient, database);
  }

  public void migrate(String poly) {
    polyInfoStorage.migrate(poly);
    polyRecordStorage.migrate(poly);
    tagStorage.migrate(poly);
    tagIndexStorage.migrate(poly);
  }

  public MongoClient getMongoClient() {
    return mongoClient;
  }

  public String getDatabase() {
    return database;
  }

  public PolyInfoStorage getPolyInfoStorage() {
    return polyInfoStorage;
  }

  public PolyRecordStorage getPolyRecordStorage() {
    return polyRecordStorage;
  }

  public TagStorage getTagStorage() {
    return tagStorage;
  }

  public TagIndexStorage getTagIndexStorage() {
    return tagIndexStorage;
  }
}
