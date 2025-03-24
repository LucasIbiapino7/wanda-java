package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.tournament.TournamentDTO;
import com.cosmo.wanda_web.services.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<TournamentDTO> create(@Valid @RequestBody TournamentDTO dto){
        TournamentDTO result = tournamentService.create(dto);
        return ResponseEntity.ok(result);
    }

}
