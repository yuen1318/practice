package io.toro.pairprogramming.controllers.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.toro.pairprogramming.models.request.WorkShiftRequest;
import io.toro.pairprogramming.services.WorkShiftService;

@RestController
@RequestMapping("api/v1/users")
public class UserWorkShiftController {

    private WorkShiftService workShiftService;

    @Autowired
    public UserWorkShiftController( WorkShiftService workShiftService ) {
        this.workShiftService = workShiftService;
    }

    @PostMapping("{id}/workshifts")
    public ResponseEntity addWorkShift(@PathVariable Long id, @Valid @RequestBody WorkShiftRequest workShiftRequest){
        return ResponseEntity.status( 201 ).body( workShiftService.addWorkShift( id, workShiftRequest) );
    }


}
