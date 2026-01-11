package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.auth.AccessTokenDTO;
import com.cosmo.wanda_web.dto.auth.AuthenticationDTO;
import com.cosmo.wanda_web.dto.auth.RegisterDTO;
import com.cosmo.wanda_web.dto.users.UpdateProfileTypeDto;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.dto.users.UserMinDto;
import com.cosmo.wanda_web.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDTO dto){
        userService.register(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<UserMinDto>> findAll(@RequestParam(value = "q", required = false, defaultValue = "") String q, Pageable pageable){
        Page<UserMinDto> response = userService.findAll(q, pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("updateProfile")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateProfileTypeDto dto){
        userService.update(dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserMinDto> getMe(){
        UserMinDto result = userService.getMe();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    @GetMapping(value = "/test")
    public String test(){
        return "deu certo!";
    }
}
