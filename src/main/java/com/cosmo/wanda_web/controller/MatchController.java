package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.match.PlayedMatchDTO;
import com.cosmo.wanda_web.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/jokenpo/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public void RunMatch(@RequestBody PlayedMatchDTO dto){
        matchService.RunMatch(dto);
    }
}
