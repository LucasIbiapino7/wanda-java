package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.notification.NotificationResponseDTO;
import com.cosmo.wanda_web.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/unseen")
    public ResponseEntity<List<NotificationResponseDTO>> getUnseen() {
        List<NotificationResponseDTO> response = notificationService.getUnseen();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/seen")
    public ResponseEntity<Void> markAllSeen() {
        notificationService.markAllSeen();
        return ResponseEntity.noContent().build();
    }
}