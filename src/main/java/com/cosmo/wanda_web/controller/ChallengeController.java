package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.services.ChallengeService;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
