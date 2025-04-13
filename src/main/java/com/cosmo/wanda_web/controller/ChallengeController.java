package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeFIndAllPendingDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeIsAcceptedDTO;
import com.cosmo.wanda_web.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/jokenpo/challenge")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<Void> challenge(@RequestBody ChallengeDTO dto){
        challengeService.challenge(dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/pending")
    public ResponseEntity<Page<ChallengeFIndAllPendingDTO>> findAllPending(Pageable pageable){
        Page<ChallengeFIndAllPendingDTO> result = challengeService.findAllPending(pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/isAccepted")
    public ResponseEntity<Long> isAccepted(@RequestBody ChallengeIsAcceptedDTO dto){
        Long result = challengeService.isAccepted(dto);
        return ResponseEntity.ok(result);
    }
}
