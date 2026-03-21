package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.auditoria.*;
import com.cosmo.wanda_web.projections.auditoria.AgenteSummaryProjection;
import com.cosmo.wanda_web.projections.auditoria.FuncaoSummaryProjection;
import com.cosmo.wanda_web.projections.auditoria.JogoSummaryProjection;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.LogAnswersAgentsRepository;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private UserRepository userRepository;
    private FunctionRepository functionRepository;
    private MatchRepository matchRepository;
    private LogAnswersAgentsRepository logAnswersAgentsRepository;

    public AuditoriaService(UserRepository userRepository, FunctionRepository functionRepository, MatchRepository matchRepository, LogAnswersAgentsRepository logAnswersAgentsRepository) {
        this.userRepository = userRepository;
        this.functionRepository = functionRepository;
        this.matchRepository = matchRepository;
        this.logAnswersAgentsRepository = logAnswersAgentsRepository;
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

    @Transactional(readOnly = true)
    public Page<AuditMatchDTO> findMatches(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return matchRepository.findForAudit(from, to, pageable).map(AuditMatchDTO::new);
    }

    @Transactional(readOnly = true)
    public AuditAgentesDTO findAgentes(LocalDateTime from, LocalDateTime to) {
        Long totalInteracoes   = logAnswersAgentsRepository.countByPeriod(from, to);
        Long totalAlunosAtivos = logAnswersAgentsRepository.countAlunosAtivos(from, to);

        List<AgenteSummaryProjection> porAgenteRaw = logAnswersAgentsRepository.groupByAgente(from, to);
        List<JogoSummaryProjection> porJogoRaw = logAnswersAgentsRepository.groupByJogo(from, to);
        List<FuncaoSummaryProjection> porFuncaoRaw = logAnswersAgentsRepository.groupByFuncao(from, to);

        List<AgenteDTO> porAgente = porAgenteRaw.stream()
                .map(p -> new AgenteDTO(p, totalInteracoes))
                .toList();

        List<JogoDTO> porJogo = porJogoRaw.stream()
                .map(p -> new JogoDTO(p, totalInteracoes))
                .toList();

        List<FuncaoDTO> porFuncao = porFuncaoRaw.stream()
                .map(p -> new FuncaoDTO(p, totalInteracoes))
                .toList();

        return new AuditAgentesDTO(totalInteracoes, totalAlunosAtivos, porAgente, porJogo, porFuncao);
    }
}
