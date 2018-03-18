package io.toro.pairprogramming.controllers.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.toro.pairprogramming.models.request.WorkShiftRequest;
import io.toro.pairprogramming.services.WorkShiftService;

@RequestMapping("api/v1/workshifts")
@RestController
public class WorkShiftController {

    private WorkShiftService workShiftService;

    @Autowired
    public WorkShiftController( WorkShiftService workShiftService ) {
        this.workShiftService = workShiftService;
    }

    @PutMapping("/{id}")
    public ResponseEntity updateWorkShift(@PathVariable Long id, @Valid @RequestBody WorkShiftRequest workShiftRequest){
        return ResponseEntity.status( 200 ).body( workShiftService.updateWorkShift(id, workShiftRequest) );
    }

    @GetMapping("")
    public ResponseEntity findAllWorkShift(){
        return ResponseEntity.status( 200 ).body( workShiftService.findAllWorkShift() );
    }

    @GetMapping("/{id}")
    public ResponseEntity findWorkShiftById(@PathVariable Long id){
        return ResponseEntity.status( 200 ).body( workShiftService.findWorkShiftById( id ) );
    }

}
