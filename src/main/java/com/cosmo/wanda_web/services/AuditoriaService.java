package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.auditoria.AuditFunctionDTO;
import com.cosmo.wanda_web.dto.auditoria.AuditUserDTO;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private UserRepository userRepository;
    private FunctionRepository functionRepository;

    public AuditoriaService(UserRepository userRepository, FunctionRepository functionRepository) {
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
    }

    @Transactional(readOnly = true)
    public Page<AuditUserDTO> findUsers(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return userRepository.findForAudit(from, to, pageable).map(AuditUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<AuditFunctionDTO> findFunctionsByCreatedAt(LocalDateTime from, LocalDateTime to, Pageable pageable){
        return functionRepository.findForAuditByCreatedAt(from, to, pageable).map(f -> new AuditFunctionDTO(f));
    }

    @Transactional(readOnly = true)
    public Page<AuditFunctionDTO> findFunctionsByUpdateAt(LocalDateTime from, LocalDateTime to, Pageable pageable){
        return functionRepository.findForAuditByUpdateAt(from, to, pageable).map(f -> new AuditFunctionDTO(f));
    }
}
