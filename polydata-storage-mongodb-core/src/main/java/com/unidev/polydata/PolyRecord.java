package com.unidev.polydata;

import static com.unidev.polydata.MongodbStorage.TAGS_KEY;

import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
import java.util.HashSet;
import org.bson.Document;

/**
 * Record to store polydata details
 */
public class PolyRecord extends BasicPoly {

  public PolyRecord() {
  }

  public PolyRecord(Document document) {
    super.putAll(document);
  }

  public Collection<BasicPoly> fetchTags() {
    return fetch(TAGS_KEY);
  }

  public void addTag(BasicPoly basicPoly) {
    if (!containsKey(TAGS_KEY)) {
      put(TAGS_KEY, new HashSet<BasicPoly>());
    }
    fetchTags().add(basicPoly);
  }

  public void removeTag(String id) {
    BasicPoly searchedPoly = null;
    for (BasicPoly poly : fetchTags()) {
      if (id.equalsIgnoreCase(poly._id())) {
        searchedPoly = poly;
        break;
      }
    }
    if (searchedPoly != null) {
      fetchTags().remove(searchedPoly);
    }
  }

}
