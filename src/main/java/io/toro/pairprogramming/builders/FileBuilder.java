package io.toro.pairprogramming.builders;

import io.toro.pairprogramming.models.pojo.LinkPojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBuilder {

    Map<String, Object> files = new HashMap<>();

    public FileBuilder addFileName(String fileName) {
        this.files.put("filename", fileName);
        return this;
    }

    public FileBuilder addType(String type) {
        this.files.put("type", type);
        return this;
    }

    public FileBuilder addSize(Long size) {
        this.files.put("size", size);
        return this;
    }

    public FileBuilder addPath(String path) {
        this.files.put("path", path);
        return this;
    }

    public FileBuilder addLinks(List<LinkPojo> links) {
        this.files.put("_links", links);
        return this;
    }

    public Map<String, Object> build() {
        return this.files;
    }
}
