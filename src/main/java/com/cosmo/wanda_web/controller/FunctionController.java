package com.cosmo.wanda_web.controller;

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
    @PostMapping()
    public ResponseEntity<FunctionRequestDTO> insert(@RequestBody FunctionRequestDTO dto){
        FunctionRequestDTO result = functionService.insert(dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/function")
    public ResponseEntity<FunctionRequestDTO> getFunctionByUser(){
        FunctionRequestDTO result = functionService.getFunctionByUser();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/function/{id}")
    public ResponseEntity<FunctionRequestDTO> getFunctionById(@PathVariable(name = "id") Long id){
        FunctionRequestDTO result = functionService.getFunctionById(id);
        return ResponseEntity.ok(result);
    }

}
