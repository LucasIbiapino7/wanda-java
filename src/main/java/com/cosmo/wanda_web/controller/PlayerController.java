package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.players.PlayerInformationDTO;
import com.cosmo.wanda_web.dto.players.ProfileDTO;
import com.cosmo.wanda_web.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/jokenpo")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> findProfileByUser(){
        ProfileDTO result = playerService.findProfileByUser();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/findByName")
    public ResponseEntity<Page<PlayerInformationDTO>> findByName(@RequestParam(name = "name", defaultValue = "") String name, Pageable pageable){
        Page<PlayerInformationDTO> result = playerService.findByName(name, pageable);
        return ResponseEntity.ok(result);
    }
}
