package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.tournament.*;
import com.cosmo.wanda_web.services.TournamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> run(@PathVariable(name = "id") Long id){
        tournamentService.run(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<BracketTournament> getTournamentBracketById(@PathVariable(name = "id") Long id){
        BracketTournament result = tournamentService.getTournamentBracketById(id);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<TournamentCreateDTO> create(@Valid @RequestBody TournamentCreateDTO dto){
        TournamentCreateDTO result = tournamentService.create(dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<TournamentMinDTO>> findAll(@RequestParam(name = "searchTerm", defaultValue = "") String searchTerm, Pageable pageable){
        Page<TournamentMinDTO> result = tournamentService.findAll(searchTerm, pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/subscribe")
    public ResponseEntity<TournamentMinDTO> subscribeTournament(@RequestBody SubscribeTournamentDTO dto){
        TournamentMinDTO result = tournamentService.subscribeTournament(dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/participating")
    public ResponseEntity<Page<TournamentMinDTO>> findAllParticipating(Pageable pageable){
        Page<TournamentMinDTO> result = tournamentService.findAllParticipating(pageable);
        return ResponseEntity.ok(result);
    }

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
//    @GetMapping("/{id}")
//    public ResponseEntity<TournamentWithParticipantsDTO> getById(@PathVariable(name = "id") Long id){
//        TournamentWithParticipantsDTO result = tournamentService.findByIdWithParticipants(id);
//        return ResponseEntity.ok(result);
//    }
}
