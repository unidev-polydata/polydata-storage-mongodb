package com.unidev.polydata;

import com.unidev.polydata.domain.BasicPoly;
import java.util.Collection;
import org.bson.Document;

/**
 * Record to store polydata details
 */
public class PolyRecord extends BasicPoly {

  public static final String TAGS_KEY = "tags";

  public PolyRecord() {
  }

  public PolyRecord(Document document) {
    super.putAll(document);
  }

  public Collection<String> fetchTags() {
    return fetch(TAGS_KEY);
  }
}
