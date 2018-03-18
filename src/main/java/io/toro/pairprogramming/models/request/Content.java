package io.toro.pairprogramming.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class Content extends ResourceSupport {

    public enum ContentType {
      @JsonProperty("file") FILE,

      @JsonProperty("directory") DIRECTORY;
    }

    private String name;

    private String parent;

    private ContentType type;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public ContentType getType() {
      return type;
    }

    public void setType(ContentType type) {
      this.type = type;
    }
}
