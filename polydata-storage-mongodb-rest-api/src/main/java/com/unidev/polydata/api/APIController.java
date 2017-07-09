package com.unidev.polydata.api;


import com.unidev.polydata.APICore;
import com.unidev.polydata.PolyInfo;
import com.unidev.polydata.PolyRecord;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
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
    private APICore apiCore;

    @RequestMapping("storage/{storageId}")
    public PolyInfo storageInfo(@PathVariable("storageId") String storageId) {
        return apiCore.fetchPolyInfo(storageId);
    }

    @RequestMapping("storage/{storageId}/tags")
    public Collection<BasicPoly> storageTags(@PathVariable("storageId") String storageId) {
        return apiCore.fetchTags(storageId);
    }

    @RequestMapping(value = "storage/{storageId}/tag/{tag}", method = RequestMethod.POST)
    public Collection<PolyRecord> storageTagsRecords(@PathVariable("storageId") String storageId,
        @PathVariable("tag") String tag, @RequestBody PolyQuery query) {

        return apiCore.fetchRecords(storageId, tag, query);
    }

}
