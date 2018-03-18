package io.toro.pairprogramming.builders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiErrorBuilder {

    private Map<String, Object> errorResponse = new HashMap<>();

    public ApiErrorBuilder addCode(Integer code) {
        this.errorResponse.put("code", code);
        return this;
    }

    public ApiErrorBuilder addMessage(String message) {
        this.errorResponse.put("message", message);
        return this;
    }

    public ApiErrorBuilder addErrorFields(List<?> errors) {
        this.errorResponse.put("errors", errors);
        return this;
    }

    public Map<String, Object> get() {
        return errorResponse;
    }
}
