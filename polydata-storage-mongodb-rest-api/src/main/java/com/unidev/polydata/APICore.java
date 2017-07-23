package com.unidev.polydata;

import com.unidev.platform.j2ee.common.WebUtils;
import com.unidev.polydata.api.NotFoundException;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class APICore {

    public static final int DEFAULT_ITEM_PER_PAGE = 30;
    public static final String ITEM_PER_PAGE_KEY = "item_per_page";

    @Autowired
    private MongodbStorage mongodbStorage;

    @Autowired
    private WebUtils webUtils;

    @Cacheable(value = "fetchPolyInfo", key = "#storageId")
    public PolyInfo fetchPolyInfo(String storageId) {
        return mongodbStorage.getPolyInfoStorage().polyInfo(storageId)
            .orElseThrow(() -> new NotFoundException("Not found storage " + storageId));
    }

    @Cacheable(value = "fetchTags", key = "#storageId")
    public List<BasicPoly> fetchTags(String storageId) {
        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        return mongodbStorage.getTagStorage().listTags(poly);
    }

    @Cacheable(value = "fetchTags", key = "#storageId + '-' + #tagStorage")
    public Object fetchTags(String storageId, String tagStorage) {
        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        return mongodbStorage.getTagStorage().listTags(poly, tagStorage);
    }

    @Cacheable(value = "fetchRecords", key = "#storageId + '-' + #tag + '-' + #apiPolyQuery")
    public Collection<PolyRecord> fetchRecords(String storageId, String tag,
        APIPolyQuery apiPolyQuery) {

        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        int itemPerPage = polyInfo.fetch(ITEM_PER_PAGE_KEY, DEFAULT_ITEM_PER_PAGE);

        PolyQuery polyQuery = new PolyQuery();
        polyQuery.setTag(tag);
        polyQuery.setItemPerPage(itemPerPage);
        polyQuery.setPage(apiPolyQuery.getPage());

        return mongodbStorage.fetchRecords(poly, polyQuery);
    }

    @Cacheable(value = "fetchPoly", key = "#storageId + '-' + #ids")
    public Map<String, PolyRecord> fetchPoly(String storageId, Collection<String> ids) {
        PolyInfo polyInfo = fetchPolyInfo(storageId);
        String poly = polyInfo.fetchPolyCollection();
        return mongodbStorage.getPolyRecordStorage().fetchPoly(poly, ids);
    }

}
