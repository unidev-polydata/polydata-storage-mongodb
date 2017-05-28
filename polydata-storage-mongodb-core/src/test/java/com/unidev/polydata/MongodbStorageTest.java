package com.unidev.polydata;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.mongodb.MongoClient;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

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
    PolyInfoStorage polyInfoStorage = mongodbStorage.getPolyInfoStorage();
    Optional<PolyInfo> tomato = polyInfoStorage.polyInfo(randomId);
    assertFalse(tomato.isPresent());

    PolyInfo polyInfo = new PolyInfo();
    polyInfo._id(randomId);
    mongodbStorage.getPolyInfoStorage().savePolyInfo(polyInfo);

    Optional<PolyInfo> polyInfoById = polyInfoStorage.polyInfo(randomId);
    assertTrue(polyInfoById.isPresent());
    assertEquals(randomId, polyInfoById.get()._id());
  }

  @Test
  public void testPolyRecordsOperations() {
    String id = "qwe_" + System.currentTimeMillis();
    PolyRecordStorage polyRecordStorage = mongodbStorage.getPolyRecordStorage();
    boolean notExisting = polyRecordStorage.existPoly("tomato", id);
    assertThat(notExisting, is(false));

    Optional<BasicPoly> basicPoly = polyRecordStorage.fetchPoly("tomato", id);
    assertFalse(basicPoly.isPresent());

    BasicPoly polyToSave = new BasicPoly();
    polyToSave._id(id);

    polyRecordStorage.save("tomato", polyToSave);

    boolean existing = polyRecordStorage.existPoly("tomato", id);
    assertThat(existing, is(true));

    Optional<BasicPoly> basicPoly2 = polyRecordStorage.fetchPoly("tomato", id);
    assertTrue(basicPoly2.isPresent());

    boolean result = polyRecordStorage.removePoly("tomato", id);
    assertThat(result, is(true));

    boolean falseRemovalResult = polyRecordStorage.removePoly("tomato", id);
    assertThat(falseRemovalResult, is(false));

    boolean falseResultForRemovedPoly = polyRecordStorage.existPoly("tomato", id);
    assertThat(falseResultForRemovedPoly, is(false));
  }


}
