package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.function.FeedbackResponseDTO;
import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.services.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/jokenpo")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/feedback")
    public ResponseEntity<FeedbackResponseDTO> feedback(@RequestBody FunctionRequestDTO dto){
        FeedbackResponseDTO result = functionService.feedback(dto);
        return ResponseEntity.status(201).body(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping()
    public ResponseEntity<FeedbackResponseDTO> validate(@RequestBody FunctionRequestDTO dto){
        FeedbackResponseDTO result = functionService.validate(dto);
        return ResponseEntity.status(201).body(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/function")
    public ResponseEntity<FunctionRequestDTO> findByUser(){
        FunctionRequestDTO result = functionService.findByUser();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/function/{id}")
    public ResponseEntity<FunctionRequestDTO> findById(@PathVariable(name = "id") Long id){
        FunctionRequestDTO result = functionService.findById(id);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/function")
    public ResponseEntity<FunctionRequestDTO> update(@RequestBody FunctionRequestDTO dto){
        FunctionRequestDTO result = functionService.update(dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping("/function")
    public ResponseEntity<Void> delete(){
        functionService.delete();
        return ResponseEntity.noContent().build();
    }

}
