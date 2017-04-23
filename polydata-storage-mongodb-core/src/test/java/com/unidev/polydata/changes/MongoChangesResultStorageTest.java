package com.unidev.polydata.changes;


import com.mongodb.MongoClient;
import com.unidev.changesexecutor.model.AbstractChange;
import com.unidev.changesexecutor.model.Change;
import com.unidev.changesexecutor.model.ChangeContext;
import com.unidev.changesexecutor.model.ChangeExecutionResult;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MongoChangesResultStorageTest {

    MongoChangesResultStorage changesResultStorage;

    @Before
    public void init() {
        MongoClient mongoClient = new MongoClient("mongodb1.prod.unidev");
        changesResultStorage = new MongoChangesResultStorage(mongoClient, "polydata-storage-test", "test-changes");
    }

    @Test
    public void testStorage() {
        String id = "Change " + System.currentTimeMillis();
        assertThat(changesResultStorage.existChangeResult(id), is(false));
        Change change = new MongodbChange(666, id) {
            @Override
            public void execute(ChangeContext changeContext) {

            }
        };

        ChangeExecutionResult changeExecutionResult = new ChangeExecutionResult(change, ChangeExecutionResult.Result.SUCCESS, "Test result");

        changesResultStorage.persistResult(changeExecutionResult);

        assertThat(changesResultStorage.existChangeResult(id), is(true));

        ChangeExecutionResult result = changesResultStorage.fetchResult(id);
        assertThat(result, is(notNullValue()));
    }

}
