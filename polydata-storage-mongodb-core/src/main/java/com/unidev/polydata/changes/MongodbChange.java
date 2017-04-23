package com.unidev.polydata.changes;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mongodb.MongoClient;
import com.unidev.changesexecutor.model.AbstractChange;
import com.unidev.changesexecutor.model.ChangeContext;


/**
 * Mongodb persisted change
 */
public class MongodbChange extends AbstractChange {

    public static final String MONGO_CLIENT_KEY = "mongoClient";
    public static final String DOCUMENT_KEY = "database";
    public static final String COLLECTION_KEY = "collection";

    public MongodbChange(long changeOrder, String changeName) {
        super(changeOrder, changeName);
    }

    public MongoClient fetchMongoClient(ChangeContext changeContext) {
        return changeContext.fetch(MONGO_CLIENT_KEY, null);
    }

    public String fetchDatabase(ChangeContext changeContext) {
        return changeContext.fetch(DOCUMENT_KEY, null);
    }

    public String fetchCollection(ChangeContext changeContext) {
        return changeContext.fetch(COLLECTION_KEY, null);
    }


    @Override
    public void execute(ChangeContext changeContext) {

    }
}
