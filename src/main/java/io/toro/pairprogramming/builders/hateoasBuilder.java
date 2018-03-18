package io.toro.pairprogramming.builders;

import io.toro.pairprogramming.models.pojo.LinkPojo;

import java.util.ArrayList;
import java.util.List;

public class hateoasBuilder {

    List<LinkPojo> links = new ArrayList<>();

    public hateoasBuilder addLink(LinkPojo link) {
        this.links.add(link);
        return this;
    }
    public List<LinkPojo> build() {
        return this.links;
    }
}
