package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.match.MatchResponseDTO;
import com.cosmo.wanda_web.dto.match.PlayedMatchDTO;
import com.cosmo.wanda_web.services.MatchService;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/jokenpo/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<MatchResponseDTO> RunMatch(@RequestBody PlayedMatchDTO dto){
        MatchResponseDTO result = matchService.RunMatch(dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> getReplayById(@PathVariable(name = "id") Long id){
        MatchResponseDTO result = matchService.getReplayById(id);
        return ResponseEntity.ok(result);
    }
}
