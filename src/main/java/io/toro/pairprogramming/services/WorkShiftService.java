package io.toro.pairprogramming.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.toro.pairprogramming.handlers.workshift.WorkShiftAlreadyExistException;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.WorkShift;
import io.toro.pairprogramming.models.request.WorkShiftRequest;
import io.toro.pairprogramming.repositories.UserRepository;
import io.toro.pairprogramming.repositories.WorkShiftRepository;

@Service
public class WorkShiftService {

    private WorkShiftRepository workShiftRepository;
    private UserRepository userRepository;

    @Autowired
    public WorkShiftService( WorkShiftRepository workShiftRepository,
            UserRepository userRepository ) {
        this.workShiftRepository = workShiftRepository;
        this.userRepository = userRepository;
    }

    public WorkShift addWorkShift(Long id, WorkShiftRequest workShiftRequest){
        WorkShift workShift = buildWorkShift(workShiftRequest );
        Optional<User> userOptional = Optional.ofNullable( userRepository.findOne( id ) );
        Optional<WorkShift> workShiftOptional = Optional.ofNullable( workShiftRepository.findOne( id ) );
        if ( workShiftOptional.isPresent() )
            throw new WorkShiftAlreadyExistException();
        if(userOptional.isPresent()){
            workShift.setUser( userRepository.findOne( id ));
            return workShiftRepository.save( workShift );
        }
        throw new NullPointerException( "no existing user at id: " + id );
    }

    public WorkShift updateWorkShift (Long id, WorkShiftRequest workShiftRequest){
        Optional<WorkShift> workShiftOptional = Optional.ofNullable( workShiftRepository.findOne( id ) );
        if(workShiftOptional.isPresent())
            return buildWorkShiftOptional( workShiftRequest, workShiftOptional );
        throw new NullPointerException("trying to update non-existing data");
    }

    private WorkShift buildWorkShiftOptional( WorkShiftRequest workShiftRequest,
            Optional<WorkShift> workShiftOptional ) {
        WorkShift workShift = workShiftOptional.get();
        workShift.setStartTime( Timestamp.valueOf( workShiftRequest.getStartTime() ) );
        workShift.setStartTime( Timestamp.valueOf( workShiftRequest.getStartTime() ) );
        workShift.setTimeZone( workShiftRequest.getTimeZone() );
        return workShiftRepository.save( workShift );
    }

    public List<WorkShift> findAllWorkShift(){
        return workShiftRepository.findAll();
    }

    public WorkShift findWorkShiftById(Long id){
        Optional<WorkShift> workShiftOptional = Optional.ofNullable( workShiftRepository.findOne( id ) );
        return workShiftOptional.orElseThrow(() -> new NullPointerException("no data found"));
    }

    private WorkShift buildWorkShift( WorkShiftRequest workShiftRequest ) {
            WorkShift workShift = new WorkShift();
            workShift.setStartTime( Timestamp.valueOf( workShiftRequest.getStartTime() ));
            workShift.setEndTime( Timestamp.valueOf( workShiftRequest.getEndTime() ));
            workShift.setTimeZone( workShiftRequest.getTimeZone() );
            return workShift;
    }

}
