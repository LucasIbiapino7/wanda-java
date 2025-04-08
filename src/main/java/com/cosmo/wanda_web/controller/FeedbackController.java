package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.users.FeedbackUserDTO;
import com.cosmo.wanda_web.services.FeedbackUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/jokenpo")
public class FeedbackController {

    @Autowired
    private FeedbackUserService feedbackUserService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/feedback-user")
    public ResponseEntity<Void> feedbackByUser(@RequestBody FeedbackUserDTO dto){
        feedbackUserService.feedbackByUser(dto);
        return ResponseEntity.noContent().build();
    }
}
