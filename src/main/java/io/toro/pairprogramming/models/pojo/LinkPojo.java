package io.toro.pairprogramming.models.pojo;

import lombok.Data;

@Data
public class LinkPojo {
    private String method;
    private String rel;
    private String href;
    private String description;
}
