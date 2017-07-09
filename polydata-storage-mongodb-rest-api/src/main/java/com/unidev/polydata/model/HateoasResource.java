package com.unidev.polydata.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

@Builder
@Getter
@Setter
public class HateoasResource<T> extends ResourceSupport {

    private T payload;

}
