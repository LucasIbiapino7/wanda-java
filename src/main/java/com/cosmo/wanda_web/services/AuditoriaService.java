package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.auditoria.AuditUserDTO;
import com.cosmo.wanda_web.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private UserRepository userRepository;

    public AuditoriaService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<AuditUserDTO> findUsers(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return userRepository.findForAudit(from, to, pageable).map(AuditUserDTO::new);
    }
}
