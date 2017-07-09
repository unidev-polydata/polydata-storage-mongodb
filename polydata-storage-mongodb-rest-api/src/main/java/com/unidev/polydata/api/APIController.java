package com.unidev.polydata.api;


import com.unidev.polydata.MongodbStorage;
import com.unidev.polydata.PolyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class APIController {

  @Autowired
  private MongodbStorage mongodbStorage;

  @RequestMapping("storage/{storageId}")
  public PolyInfo storageInfo(@PathVariable("storageId") String storageId) {
    return mongodbStorage.getPolyInfoStorage().polyInfo(storageId)
        .orElseThrow(PolyNotFoundException::new);
  }

}
