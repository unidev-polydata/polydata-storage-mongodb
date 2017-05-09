package com.unidev.polydata;


import com.mongodb.MongoClient;
import com.unidev.polydata.domain.BasicPoly;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MongodbStorageTest {

    MongodbStorage mongodbStorage;

    @Before
    public void init() {
        MongoClient mongoClient = new MongoClient("mongodb-dev");
        mongodbStorage = new MongodbStorage(mongoClient, "polydata-storage-test");
    }

    @Test
    public void testPolyInfoFetching() {
        String randomId = "potato_" + System.currentTimeMillis();
        Optional<PolyInfo> tomato = mongodbStorage.polyInfo(randomId);
        assertFalse(tomato.isPresent());

        PolyInfo polyInfo = new PolyInfo();
        polyInfo._id(randomId);
        mongodbStorage.savePolyInfo(polyInfo);

        Optional<PolyInfo> polyInfoById = mongodbStorage.polyInfo(randomId);
        assertTrue(polyInfoById.isPresent());
        assertEquals(randomId, polyInfoById.get()._id());
    }

    @Test
    public void testPolyRecordsOperations() {
        String id = "qwe_" + System.currentTimeMillis();

        boolean notExisting = mongodbStorage.existPoly("tomato", id);
        assertThat(notExisting, is(false));

        Optional<BasicPoly> basicPoly = mongodbStorage.fetchPoly("tomato", id);
        assertFalse(basicPoly.isPresent());

        BasicPoly polyToSave = new BasicPoly();
        polyToSave._id(id);

        mongodbStorage.save("tomato", polyToSave);

        boolean existing = mongodbStorage.existPoly("tomato", id);
        assertThat(existing, is(true));

        Optional<BasicPoly> basicPoly2 = mongodbStorage.fetchPoly("tomato", id);
        assertTrue(basicPoly2.isPresent());

        boolean result = mongodbStorage.removePoly("tomato", id);
        assertThat(result, is(true));

        boolean falseRemovalResult = mongodbStorage.removePoly("tomato", id);
        assertThat(falseRemovalResult, is(false));

        boolean falseResultForRemovedPoly = mongodbStorage.existPoly("tomato", id);
        assertThat(falseResultForRemovedPoly, is(false));
    }


}
