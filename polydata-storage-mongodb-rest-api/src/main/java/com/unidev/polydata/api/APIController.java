package com.unidev.polydata.api;


import com.mongodb.client.MongoCollection;
import com.unidev.polydata.APICore;
import com.unidev.polydata.MongodbStorage;
import com.unidev.polydata.PolyInfo;
import com.unidev.polydata.domain.BasicPoly;
import java.util.List;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class APIController {

    @Autowired
    private MongodbStorage mongodbStorage;

    @Autowired
    private APICore apiCore;

    @RequestMapping("storage/{storageId}")
    public PolyInfo storageInfo(@PathVariable("storageId") String storageId) {
        return apiCore.fetchPolyInfo(storageId);
    }

    @RequestMapping("storage/{storageId}/tags")
    public List<BasicPoly> storageTags(@PathVariable("storageId") String storageId) {
        return apiCore.fetchTags(storageId);
    }

    @RequestMapping(value = "storage/{storageId}/tag/{tag}", method = RequestMethod.POST)
    public List<BasicPoly> storageTagsRecords(@PathVariable("storageId") String storageId,
        @PathVariable("tag") String tag, @RequestBody Query query) {
        mongodbStorage.getPolyInfoStorage().polyInfo(storageId)
            .orElseThrow(PolyNotFoundException::new);

        MongoCollection<Document> collection = mongodbStorage.getTagIndexStorage()
            .fetchCollection(storageId, tag);

        return mongodbStorage.getTagStorage().listTags(storageId);
    }

}
