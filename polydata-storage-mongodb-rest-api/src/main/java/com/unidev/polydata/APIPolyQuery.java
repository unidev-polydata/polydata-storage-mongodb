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

    private Integer page = 0;
    private String startKey;
    private Integer itemPerPage;
    private Boolean randomOrder = false;
    private Integer skip = 0;
    private Integer limit = 0;

}

