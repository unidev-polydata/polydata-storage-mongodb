package com.unidev.polydata;


import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

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

}
