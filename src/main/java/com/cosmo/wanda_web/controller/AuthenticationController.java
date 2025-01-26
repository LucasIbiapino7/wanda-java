package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.auth.AccessTokenDTO;
import com.cosmo.wanda_web.dto.auth.AuthenticationDTO;
import com.cosmo.wanda_web.dto.auth.RegisterDTO;
import com.cosmo.wanda_web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenDTO> login(@RequestBody AuthenticationDTO dto){
        AccessTokenDTO result = userService.login(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDTO dto){
        userService.register(dto);
        return ResponseEntity.ok().build();
    }
}
