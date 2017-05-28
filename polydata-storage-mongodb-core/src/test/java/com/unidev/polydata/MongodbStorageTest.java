package com.unidev.polydata;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.mongodb.MongoClient;
import com.unidev.polydata.domain.BasicPoly;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class MongodbStorageTest {

  MongodbStorage mongodbStorage;
  MongoClient mongoClient;

  @Before
  public void init() {
    mongoClient = new MongoClient("mongodb-dev");
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

  @Test
  public void testTagsOperations() {
    String poly = "tomato_" + new Random().nextInt(256);

    mongodbStorage.migrate(poly);

    TagStorage tagStorage = mongodbStorage.getTagStorage();

    List<BasicPoly> emptyTagList = tagStorage.listTags(poly);
    assertThat(emptyTagList.size(), is(0));

    BasicPoly tag1 = BasicPoly.newPoly("tag1");
    BasicPoly tag1_1 = BasicPoly.newPoly("tag1");
    BasicPoly tag2 = BasicPoly.newPoly("tag2");

    tagStorage.addTag(poly, tag1);
    tagStorage.addTag(poly, tag1_1);
    tagStorage.addTag(poly, tag2);

    List<BasicPoly> tags = tagStorage.listTags(poly);
    assertThat(tags.size(), is(2));

    Optional<BasicPoly> optionalDbTag1 = tagStorage.fetchPoly(poly, "tag1");
    assertThat(optionalDbTag1.isPresent(), is(true));
    BasicPoly dbTag1 = optionalDbTag1.get();
    assertThat(dbTag1.fetch(TagStorage.COUNT_KEY), is(2));

    Optional<BasicPoly> optionalDbTag2 = tagStorage.fetchPoly(poly, "tag2");
    assertThat(optionalDbTag2.isPresent(), is(true));
    BasicPoly dbTag2 = optionalDbTag2.get();
    assertThat(dbTag2.fetch(TagStorage.COUNT_KEY), is(1));

    tagStorage.removeTag(poly, "tag1");

    optionalDbTag1 = tagStorage.fetchPoly(poly, "tag1");
    assertThat(optionalDbTag1.isPresent(), is(true));
    dbTag1 = optionalDbTag1.get();
    assertThat(dbTag1.fetch(TagStorage.COUNT_KEY), is(1));

    tagStorage.removeTag(poly, "tag1");

    optionalDbTag1 = tagStorage.fetchPoly(poly, "tag1");
    assertThat(optionalDbTag1.isPresent(), is(false));

    tagStorage.fetchCollection(poly).drop();
  }


}
