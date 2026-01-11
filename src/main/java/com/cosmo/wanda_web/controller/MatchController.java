package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.match.*;
import com.cosmo.wanda_web.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
//    @PostMapping
//    public ResponseEntity<Long> RunMatch(@RequestBody PlayedMatchDTO dto){
//        Long result = matchService.RunMatch(dto);
//        return ResponseEntity.ok(result);
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ReplayDto> getReplayById(@PathVariable Long id) {
        ReplayDto dto = matchService.getReplayById(id);
        return ResponseEntity.ok(dto);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping()
    public ResponseEntity<Page<MatchMinDTO>> getAll(Pageable pageable){
        Page<MatchMinDTO> result = matchService.getAll(pageable);
        return ResponseEntity.ok(result);
    }
}
