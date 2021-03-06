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
import java.util.Collection;
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
    public void testCustomTagStorage() {
        String poly = "tomato_" + new Random().nextInt(256);
        String tags_storage = "categories";

        mongodbStorage.migrate(poly);

        TagStorage tagStorage = mongodbStorage.getTagStorage();
        tagStorage.migrate(poly, tags_storage);

        List<BasicPoly> emptyTagList = tagStorage.listTags(poly, tags_storage);
        assertThat(emptyTagList.size(), is(0));

        BasicPoly tag1 = BasicPoly.newPoly("tag1");
        BasicPoly tag1_1 = BasicPoly.newPoly("tag1");
        BasicPoly tag2 = BasicPoly.newPoly("tag2");

        tagStorage.addTag(poly, tags_storage, tag1);
        tagStorage.addTag(poly, tags_storage, tag1_1);
        tagStorage.addTag(poly, tags_storage, tag2);

        List<BasicPoly> tags = tagStorage.listTags(poly, tags_storage);
        assertThat(tags.size(), is(2));

        Optional<BasicPoly> optionalDbTag1 = tagStorage.fetchPoly(poly, tags_storage, "tag1");
        assertThat(optionalDbTag1.isPresent(), is(true));
        BasicPoly dbTag1 = optionalDbTag1.get();
        assertThat(dbTag1.fetch(COUNT_KEY), is(2));
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

        PolyQuery allPolyQuery = new PolyQuery();
        allPolyQuery.setPage(0);
        allPolyQuery.setItemPerPage(40);
        Collection<PolyRecord> allPolyList = mongodbStorage.fetchRecords(poly, allPolyQuery);
        assertThat(allPolyList.size(), is(3));

        PolyQuery pagedQuery = new PolyQuery();
        pagedQuery.setPage(0);
        pagedQuery.setItemPerPage(2);
        Collection<PolyRecord> pagedList = mongodbStorage.fetchRecords(poly, pagedQuery);
        assertThat(pagedList.size(), is(2));

        PolyQuery polyQuery = new PolyQuery();
        polyQuery.setTag("tag1");
        Collection<PolyRecord> polyRecords = mongodbStorage.fetchRecords(poly, polyQuery);
        assertThat(polyRecords.size(), is(1));

        PolyQuery polyQueryTag3 = new PolyQuery();
        polyQueryTag3.setTag("tag3");
        Collection<PolyRecord> polyRecordsTag3 = mongodbStorage.fetchRecords(poly, polyQueryTag3);
        assertThat(polyRecordsTag3.size(), is(3));
        for (PolyRecord record : polyRecordsTag3) {
            Collection<BasicPoly> tags = record.fetchTags();
            boolean hasTag3 = false;
            for (BasicPoly tag : tags) {
                if (tag._id().equals("tag3")) {
                    hasTag3 = true;
                    break;
                }
            }
            assertThat(hasTag3, is(true));
        }

        PolyQuery randomOrderQuery = new PolyQuery();
        randomOrderQuery.setRandomOrder(true);
        randomOrderQuery.setItemPerPage(10);
        Collection<PolyRecord> randomRecords = mongodbStorage.fetchRecords(poly, randomOrderQuery);
        assertThat(randomRecords.size(), is(3));

        long countRecords = mongodbStorage.countRecords(poly, polyQuery);
        assertThat(countRecords, is(1L));

        for (int iteration = 1; iteration <= 3; iteration++) {
            mongodbStorage.removePoly(poly, "test" + iteration);
        }

        long polyCount = mongodbStorage.getPolyRecordStorage().fetchCollection(poly).count();
        assertThat(polyCount, is(0L));

        long countAfterRemove = mongodbStorage.getTagStorage().fetchCollection(poly).count();
        assertThat(countAfterRemove, is(0L));

        polyRecords = mongodbStorage.fetchRecords(poly, polyQuery);
        assertThat(polyRecords.size(), is(0));

        countRecords = mongodbStorage.countRecords(poly, polyQuery);
        assertThat(countRecords, is(0L));

    }


}
