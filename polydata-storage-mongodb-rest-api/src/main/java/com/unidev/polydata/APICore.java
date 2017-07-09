package com.unidev.polydata;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.unidev.platform.j2ee.common.WebUtils;
import com.unidev.polydata.api.NotFoundException;
import com.unidev.polydata.api.PolyQuery;
import com.unidev.polydata.domain.BasicPoly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class APICore {

    public static final int DEFAULT_ITEM_PER_PAGE = 30;
    public static final String ITEM_PER_PAGE_KEY = "item_per_page";

    @Autowired
    private MongodbStorage mongodbStorage;

    @Autowired
    private WebUtils webUtils;

    public PolyInfo fetchPolyInfo(String storageId) {
        return mongodbStorage.getPolyInfoStorage().polyInfo(storageId)
            .orElseThrow(() -> new NotFoundException("Not found storage " + storageId));
    }

    public List<BasicPoly> fetchTags(String storageId) {
        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        return mongodbStorage.getTagStorage().listTags(poly);
    }

    public Collection<PolyRecord> fetchRecords(String storageId, String tag, PolyQuery polyQuery) {
        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        int itemPerPage = polyInfo.fetch(ITEM_PER_PAGE_KEY, DEFAULT_ITEM_PER_PAGE);

        if (StringUtils.isEmpty(tag)) {
            MongoCollection<Document> collection = mongodbStorage.getPolyRecordStorage()
                .fetchCollection(poly);
            FindIterable<Document> result = collection.find()
                .sort(new BasicDBObject().append(MongodbStorage.DATE_KEY, -1)).skip(
                    polyQuery.getPage() * itemPerPage).limit(itemPerPage);
            List<PolyRecord> list = new ArrayList<>();
            result.iterator().forEachRemaining(document -> list.add(new PolyRecord(document)));
            return list;
        } else {
            MongoCollection<Document> collection = mongodbStorage.getTagIndexStorage()
                .fetchCollection(poly, tag);
            FindIterable<Document> result = collection.find()
                .sort(new BasicDBObject().append(MongodbStorage.DATE_KEY, -1)).skip(
                    polyQuery.getPage() * itemPerPage).limit(itemPerPage);
            List<String> ids = new ArrayList<>();
            result.iterator().forEachRemaining(document -> ids.add(document.get("_id") + ""));
            return mongodbStorage.getPolyRecordStorage().fetchPoly(poly, ids).values();
        }
    }


    public Map<String, PolyRecord> fetchPoly(String storageId, Collection<String> ids) {
        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        return mongodbStorage.getPolyRecordStorage().fetchPoly(poly, ids);
    }
}
