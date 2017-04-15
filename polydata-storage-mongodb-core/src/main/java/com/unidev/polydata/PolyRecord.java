package com.unidev.polydata;

import com.unidev.polydata.domain.BasicPoly;
import org.bson.Document;

import java.util.Collection;

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
