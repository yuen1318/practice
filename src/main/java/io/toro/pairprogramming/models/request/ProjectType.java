package io.toro.pairprogramming.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public enum ProjectType {

    @JsonProperty("heroku") HEROKU(1),

    @JsonProperty("github") GITHUB(2);

    private final Integer code;

    ProjectType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    private static final Map<Integer,ProjectType> lookup = new HashMap<Integer,ProjectType>();

    static {
        for (ProjectType type : ProjectType.values())
            lookup.put(type.getCode(), type);
    }

    public static ProjectType getKey(Integer value) {
        return lookup.get(value);
    }
}
