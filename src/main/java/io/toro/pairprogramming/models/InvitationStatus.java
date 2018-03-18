package io.toro.pairprogramming.models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum InvitationStatus {

    @JsonProperty("pending") PENDING(1),

    @JsonProperty("accept") ACCEPT(2),

    @JsonProperty("decline") DECLINE(3);

    private final Integer code;

    InvitationStatus(Integer code) {
            this.code = code;
    }

    public Integer getCode() {
            return code;
    }

    private static final Map<Integer,InvitationStatus> lookup = new HashMap<Integer,InvitationStatus>();

    static {
            for (InvitationStatus status : InvitationStatus.values())
            lookup.put(status.getCode(), status);
    }

    public static InvitationStatus getKey(Integer value) {
            return lookup.get(value);
    }
}
