package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.auditoria.AuditFunctionDTO;
import com.cosmo.wanda_web.dto.auditoria.AuditMatchDTO;
import com.cosmo.wanda_web.dto.auditoria.AuditUserDTO;
import com.cosmo.wanda_web.services.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/usuarios")
    public ResponseEntity<Page<AuditUserDTO>> findUsers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        Page<AuditUserDTO> result = auditoriaService.findUsers(from, to, pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/funcoes")
    public ResponseEntity<Page<AuditFunctionDTO>> findFunctionsByCreatedAt(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                          Pageable pageable){
        return ResponseEntity.ok(auditoriaService.findFunctionsByCreatedAt(from, to, pageable));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/funcoes/atualizadas")
    public ResponseEntity<Page<AuditFunctionDTO>> findFunctionsByUpdateAt(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                                Pageable pageable){
        return ResponseEntity.ok(auditoriaService.findFunctionsByUpdateAt(from, to, pageable));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/partidas")
    public ResponseEntity<Page<AuditMatchDTO>> findMatches(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.findMatches(from, to, pageable));
    }

}
