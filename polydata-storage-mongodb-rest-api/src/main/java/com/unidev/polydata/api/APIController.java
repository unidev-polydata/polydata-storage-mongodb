package com.unidev.polydata.api;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.unidev.polydata.APICore;
import com.unidev.polydata.APIPolyQuery;
import com.unidev.polydata.Application;
import com.unidev.polydata.PolyRecord;
import com.unidev.polydata.model.HateoasResource;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
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

    @RequestMapping
    public HateoasResource root() {

        HateoasResource<Object> root = HateoasResource.builder().payload(
            ImmutableMap.of("saas-support", "polydata@universal-development.com", "api",
                Application.API_VERSION)
        ).build();

        HateoasResource resource = methodOn(APIController.class).root();
        Link link = linkTo(resource).withSelfRel();
        root.add(link);

        return root;
    }

    @RequestMapping("storage/{storageId}")
    public HateoasResource storageInfo(@PathVariable("storageId") String storageId) {
        HateoasResource polyResource = HateoasResource.builder()
            .payload(apiCore.fetchPolyInfo(storageId)).build();

        HateoasResource resource = methodOn(APIController.class).storageInfo(storageId);
        Link link = linkTo(resource).withSelfRel();
        polyResource.add(link);
        return polyResource;
    }

    @RequestMapping("storage/{storageId}/tags")
    public HateoasResource storageTags(@PathVariable("storageId") String storageId) {
        HateoasResource collectionResource = HateoasResource.builder()
            .payload(apiCore.fetchTags(storageId))
            .build();

        HateoasResource resource = methodOn(APIController.class)
            .storageTags(storageId);
        Link link = linkTo(resource).withSelfRel();
        collectionResource.add(link);

        return collectionResource;
    }

    @PostMapping(value = "storage/{storageId}/query")
    public HateoasResource queryStorage(@PathVariable("storageId") String storageId,
        @RequestBody APIPolyQuery query) {
        HateoasResource collectionResource = HateoasResource.builder()
            .payload(apiCore.fetchRecords(storageId, null, query)).build();

        HateoasResource resource = methodOn(APIController.class)
            .queryStorage(storageId, query);
        Link link = linkTo(resource).withSelfRel();
        collectionResource.add(link);

        return collectionResource;
    }

    @PostMapping(value = "storage/{storageId}/tag/{tag}")
    public HateoasResource storageTagsRecords(@PathVariable("storageId") String storageId,
        @PathVariable("tag") String tag, @RequestBody APIPolyQuery query) {
        HateoasResource collectionResource = HateoasResource.builder()
            .payload(apiCore.fetchRecords(storageId, tag, query)).build();

        HateoasResource resource = methodOn(APIController.class)
            .storageTagsRecords(storageId, tag, query);
        Link link = linkTo(resource).withSelfRel();
        collectionResource.add(link);

        return collectionResource;
    }

    @RequestMapping(value = "storage/{storageId}/poly/{polyId}")
    public HateoasResource poly(@PathVariable("storageId") String storageId,
        @PathVariable("polyId") String polyId) {
        Map<String, PolyRecord> recordMap = apiCore
            .fetchPoly(storageId, ImmutableSet.of(polyId));
        if (recordMap.isEmpty()) {
            throw new NotFoundException("Poly not found");
        }
        HateoasResource poly = HateoasResource.builder()
            .payload(recordMap.values().iterator().next()).build();
        HateoasResource resource = methodOn(APIController.class)
            .poly(storageId, polyId);
        Link link = linkTo(resource).withSelfRel();
        poly.add(link);

        return poly;
    }

    @PostMapping(value = "storage/{storageId}/poly")
    public HateoasResource poly(@PathVariable("storageId") String storageId,
        @RequestBody List<String> polyIds) {
        HateoasResource<Object> polyMap = HateoasResource.builder()
            .payload(apiCore.fetchPoly(storageId, polyIds)).build();

        HateoasResource resource = methodOn(APIController.class)
            .poly(storageId, polyIds);
        Link link = linkTo(resource).withSelfRel();
        polyMap.add(link);

        return polyMap;
    }

}
