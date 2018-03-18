package io.toro.pairprogramming.integration.utils;

import java.sql.Timestamp;

import io.toro.pairprogramming.models.WorkShift;
import io.toro.pairprogramming.models.request.WorkShiftRequest;

public class WorkShiftUtils {

    public static WorkShift createWorkShift(){
      WorkShift workShift = new WorkShift();
      workShift.setTimeZone( "Asia/Manila" );
      workShift.setStartTime( Timestamp.valueOf("2011-12-03 10:15:30") );
      workShift.setEndTime( Timestamp.valueOf("2011-12-03 10:15:30") );
      return workShift;
    }

    public static WorkShiftRequest createWorkShiftRequest(){
        WorkShiftRequest workShiftRequest = new WorkShiftRequest();
        workShiftRequest.setTimeZone( "Asia/Manila" );
        workShiftRequest.setStartTime( "2011-12-03 10:15:30" );
        workShiftRequest.setEndTime( "2011-12-03 10:15:30" );
        return workShiftRequest;
    }

}
