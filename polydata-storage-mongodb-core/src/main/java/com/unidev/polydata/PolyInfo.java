package com.unidev.polydata;

import com.unidev.polydata.domain.BasicPoly;
import org.bson.Document;

/**
 * Poly information object
 */
public class PolyInfo extends BasicPoly {

    public static final String POLY_NAME_KEY = "poly";

  public PolyInfo() {
  }

  public PolyInfo(Document document) {
    super.putAll(document);
  }

    public String fetchPolyCollection() {
        return fetch(POLY_NAME_KEY);
    }
}
