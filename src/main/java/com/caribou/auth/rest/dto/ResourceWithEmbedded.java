package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;

public abstract class ResourceWithEmbedded extends ResourceSupport {

    @JsonUnwrapped
    // The @JsonUnwrapped annotation is required as EmbeddedWrappers are by default serialised in a "_embedded" container,
    // that has to be added directly into the top level object
    private Resources<EmbeddedWrapper> embeddeds;

    public Resources<EmbeddedWrapper> getEmbeddeds() {
        return embeddeds;
    }

    public void setEmbeddeds(Resources<EmbeddedWrapper> embeddeds) {
        this.embeddeds = embeddeds;
    }

}
