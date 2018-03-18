package io.toro.pairprogramming.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkShiftRequest {
    private String timeZone;
    private String startTime;
    private String endTime;
}
