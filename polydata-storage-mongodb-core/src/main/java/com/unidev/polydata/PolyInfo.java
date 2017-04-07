package com.unidev.polydata;

import com.unidev.polydata.domain.BasicPoly;
import org.bson.Document;

/**
 * Poly information object
 */
public class PolyInfo extends BasicPoly {

    public PolyInfo() {
    }

    public PolyInfo(Document document) {
        super.putAll(document);
    }
}
