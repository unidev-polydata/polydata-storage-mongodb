package com.unidev.polydata;


import static com.unidev.polydata.MongodbStorage.COUNT_KEY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.mongodb.MongoClient;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    Optional<PolyRecord> basicPoly = polyRecordStorage.fetchPoly("tomato", id);
    assertFalse(basicPoly.isPresent());

    BasicPoly polyToSave = new BasicPoly();
    polyToSave._id(id);

    polyRecordStorage.save("tomato", polyToSave);

    boolean existing = polyRecordStorage.existPoly("tomato", id);
    assertThat(existing, is(true));

    Optional<PolyRecord> basicPoly2 = polyRecordStorage.fetchPoly("tomato", id);
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
    assertThat(dbTag1.fetch(COUNT_KEY), is(2));

    Optional<BasicPoly> optionalDbTag2 = tagStorage.fetchPoly(poly, "tag2");
    assertThat(optionalDbTag2.isPresent(), is(true));
    BasicPoly dbTag2 = optionalDbTag2.get();
    assertThat(dbTag2.fetch(COUNT_KEY), is(1));

    tagStorage.removeTag(poly, "tag1");

    optionalDbTag1 = tagStorage.fetchPoly(poly, "tag1");
    assertThat(optionalDbTag1.isPresent(), is(true));
    dbTag1 = optionalDbTag1.get();
    assertThat(dbTag1.fetch(COUNT_KEY), is(1));

    tagStorage.removeTag(poly, "tag1");

    optionalDbTag1 = tagStorage.fetchPoly(poly, "tag1");
    assertThat(optionalDbTag1.isPresent(), is(false));

    tagStorage.fetchCollection(poly).drop();
  }

  @Test
  public void testTagIndexOperations() {
      String poly = "tomato_" + new Random().nextInt(256);
      mongodbStorage.migrate(poly);

      String tagIndex = "test_tag";
      String id = "_id";
      TagIndexStorage tagIndexStorage = mongodbStorage.getTagIndexStorage();
      assertThat(tagIndexStorage.exist( poly, tagIndex, id), is(false));

      BasicPoly savedPolyIndex = BasicPoly.newPoly(id);
      savedPolyIndex.put("key", "value");
      tagIndexStorage.addPolyIndex(poly, tagIndex, savedPolyIndex);

      Optional<BasicPoly> polyById = tagIndexStorage.fetchPoly(poly, tagIndex, id);
      assertThat(polyById.isPresent(), is(true));

      BasicPoly basicPoly = polyById.get();
      assertThat(basicPoly._id(), is(id));
      assertThat(basicPoly.get("key"), is("value"));

  }

  @Test
  public void testPolyRecordPersisting() {
    String poly = "polys_" + new Random().nextInt(256);
    mongodbStorage.migrate(poly);

    for (int iteration = 1; iteration <= 3; iteration++) {
      PolyRecord polyRecord = new PolyRecord();
      polyRecord._id("test" + iteration);
      polyRecord.put("data", "testqwe");
      polyRecord.addTag(BasicPoly.newPoly("tag" + iteration));
      polyRecord.addTag(BasicPoly.newPoly("tag" + (iteration + 1)));

      BasicPoly tag3 = BasicPoly.newPoly("tag" + (iteration + 2));
      tag3.put("label", "Tag" + (iteration + 2));
      polyRecord.addTag(tag3);

      mongodbStorage.storePoly(poly, polyRecord);
    }

    long count = mongodbStorage.getTagStorage().fetchCollection(poly).count();
    assertThat(count, is(5L));

    Map<String, PolyRecord> recordMap = mongodbStorage.getPolyRecordStorage()
        .fetchPoly(poly, Arrays.asList("test1", "test2", "test666"));
    assertThat(recordMap.size(), is(2));
    assertThat(recordMap.containsKey("test1"), is(true));
    assertThat(recordMap.get("test1")._id(), is("test1"));
    assertThat(recordMap.containsKey("test2"), is(true));
    assertThat(recordMap.containsKey("test666"), is(false));

    Optional<PolyRecord> polyRecord = mongodbStorage.getPolyRecordStorage()
        .fetchPoly(poly, "test1");
    assertThat(polyRecord.isPresent(), is(true));
    assertThat(polyRecord.get().fetchTags().size(), is(3));

    for (int iteration = 1; iteration <= 3; iteration++) {
      mongodbStorage.removePoly(poly, "test" + iteration);
    }

    long polyCount = mongodbStorage.getPolyRecordStorage().fetchCollection(poly).count();
    assertThat(polyCount, is(0L));

    long countAfterRemove = mongodbStorage.getTagStorage().fetchCollection(poly).count();
    assertThat(countAfterRemove, is(0L));

    long tagIndexCount = mongodbStorage.getTagIndexStorage().fetchCollection(poly, "tag1").count();
    assertThat(tagIndexCount, is(0L));
  }


}
