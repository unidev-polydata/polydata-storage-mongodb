package com.unidev.polydata.api;


import com.google.common.collect.ImmutableSet;
import com.unidev.polydata.APICore;
import com.unidev.polydata.PolyInfo;
import com.unidev.polydata.PolyRecord;
import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping(value = "storage/{storageId}/query")
    public Collection<PolyRecord> queryStorage(@PathVariable("storageId") String storageId,
        @RequestBody PolyQuery query) {
        return apiCore.fetchRecords(storageId, null, query);
    }

    @PostMapping(value = "storage/{storageId}/tag/{tag}")
    public Collection<PolyRecord> storageTagsRecords(@PathVariable("storageId") String storageId,
        @PathVariable("tag") String tag, @RequestBody PolyQuery query) {
        return apiCore.fetchRecords(storageId, tag, query);
    }

    @RequestMapping(value = "storage/{storageId}/poly/{polyId}")
    public PolyRecord poly(@PathVariable("storageId") String storageId,
        @PathVariable("polyId") String polyId) {
        Map<String, PolyRecord> recordMap = apiCore
            .fetchPoly(storageId, ImmutableSet.of(polyId));
        if (recordMap.isEmpty()) {
            throw new NotFoundException("Poly not found");
        }
        return recordMap.values().iterator().next();
    }

    @PostMapping(value = "storage/{storageId}/poly")
    public Map<String, PolyRecord> poly(@PathVariable("storageId") String storageId,
        @RequestBody List<String> polyIds) {
        return apiCore.fetchPoly(storageId, polyIds);
    }

}
