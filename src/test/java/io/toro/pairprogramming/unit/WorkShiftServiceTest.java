package io.toro.pairprogramming.unit;

import static io.toro.pairprogramming.integration.utils.WorkShiftUtils.createWorkShift;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.toro.pairprogramming.models.WorkShift;
import io.toro.pairprogramming.repositories.WorkShiftRepository;

@RunWith(SpringRunner.class)
public class WorkShiftServiceTest {

    @Before
    public void setUp(){
        workShiftRepository.save(createWorkShift());
    }

    @MockBean
    private WorkShiftRepository workShiftRepository;

    @Test
    public void shouldAddWorkShift() throws Exception {
        WorkShift workShift = createWorkShift();
        when(workShiftRepository.save(workShift))
                .thenReturn(workShift);
    }

    @Test
    public void shouldUpdateWorkShift() throws Exception {
        WorkShift workShift = createWorkShift();
        workShift.setId( 1L );
        workShiftRepository.save(workShift);

        WorkShift workShiftUpdated = createWorkShift();
        workShiftUpdated.setId( 1L );
        workShiftUpdated.setTimeZone( "yuen" );

        when(workShiftRepository.findOne( workShiftUpdated.getIds() ))
                .thenReturn(workShiftUpdated);
    }

    @Test
    public void shouldGetAllWorkShifts() throws Exception {
        List<WorkShift> workShifts = new ArrayList<>();

        for(int i=0; i<10; i++){
            workShifts.add(createWorkShift());
        }

        List<WorkShift> result = workShiftRepository.findAll();
        when(workShiftRepository.findAll())
                .thenReturn( result );
    }

    @Test
    public void shouldGetWorkShiftById() throws Exception {
        WorkShift result = workShiftRepository.findOne( 1L );
        when(workShiftRepository.findOne( 1L ))
                .thenReturn( result );
    }
}
