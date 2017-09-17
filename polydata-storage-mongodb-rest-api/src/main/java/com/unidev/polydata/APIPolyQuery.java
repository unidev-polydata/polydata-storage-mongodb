package com.unidev.polydata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIPolyQuery {

    private int page = 0;
    private String startKey;

    private boolean random = false;
    private int skip = 0;
    private int limit = 0;

}

