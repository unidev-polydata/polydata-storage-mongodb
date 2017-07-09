package com.unidev.polydata;

import com.unidev.platform.j2ee.common.WebUtils;
import com.unidev.polydata.api.PolyNotFoundException;
import com.unidev.polydata.domain.BasicPoly;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class APICore {


    @Autowired
    private MongodbStorage mongodbStorage;

    @Autowired
    private WebUtils webUtils;

    public PolyInfo fetchPolyInfo(String storageId) {
        return mongodbStorage.getPolyInfoStorage().polyInfo(storageId)
            .orElseThrow(PolyNotFoundException::new);
    }

    public List<BasicPoly> fetchTags(String storageId) {
        return mongodbStorage.getTagStorage().listTags(storageId);
    }

}
