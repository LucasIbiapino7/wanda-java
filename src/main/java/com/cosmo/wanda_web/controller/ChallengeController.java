package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/jokenpo/challenge")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    public ResponseEntity<Void> challenge(ChallengeDTO dto){
        challengeService.challenge(dto);
        return ResponseEntity.noContent().build();
    }

}
