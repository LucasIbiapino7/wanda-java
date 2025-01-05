package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.services.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/jokenpo")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @PostMapping()
    public ResponseEntity<FunctionRequestDTO> insert(@RequestBody FunctionRequestDTO dto){
        FunctionRequestDTO result = functionService.insert(dto);
        return ResponseEntity.ok(result);
    }

}
