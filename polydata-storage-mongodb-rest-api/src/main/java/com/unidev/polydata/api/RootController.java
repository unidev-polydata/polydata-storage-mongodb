package com.unidev.polydata.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.google.common.collect.ImmutableMap;
import com.unidev.polydata.Application;
import com.unidev.polydata.model.HateoasResource;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class RootController {

    @RequestMapping("/")
    @ResponseBody
    public HateoasResource root() {

        HateoasResource<Object> root = HateoasResource.builder().payload(
            ImmutableMap.of("saas-support", "polydata@universal-development.com", "api",
                Application.API_VERSION)
        ).build();

        HateoasResource resource = methodOn(RootController.class).root();
        Link link = linkTo(resource).withSelfRel();
        root.add(link);

        return root;
    }

}
