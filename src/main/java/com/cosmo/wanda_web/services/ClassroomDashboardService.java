package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.classroom.DashboardEngagementDTO;
import com.cosmo.wanda_web.dto.classroom.DashboardMatchDTO;
import com.cosmo.wanda_web.dto.classroom.DashboardOverviewDTO;
import com.cosmo.wanda_web.dto.classroom.DashboardRankingDTO;
import com.cosmo.wanda_web.entities.Classroom;
import com.cosmo.wanda_web.entities.ClassroomStudent;
import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.entities.StudentEngagementStatus;
import com.cosmo.wanda_web.projections.dashboard.UserCountProjection;
import com.cosmo.wanda_web.projections.dashboard.UserInteractionTypeProjection;
import com.cosmo.wanda_web.projections.dashboard.UserValidityProjection;
import com.cosmo.wanda_web.repositories.*;
import com.cosmo.wanda_web.services.exceptions.ClassroomAccessDeniedException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.InteractionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassroomDashboardService {

    private static final Logger log = LoggerFactory.getLogger(ClassroomDashboardService.class);

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ClassroomStudentRepository classroomStudentRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private LogAnswersAgentsRepository logAnswersAgentsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchRepository matchRepository;

    private static final int TRAVADO = 10;

    @Transactional(readOnly = true)
    public Page<DashboardEngagementDTO> getEngagement(Long classroomId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        List<Long> studentIds = getStudentIdsAndValidateAccess(classroomId);

        if (studentIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Classroom classroom = classroomRepository.findByIdWithDetails(classroomId).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        // Busca membros paginados
        Page<ClassroomStudent> members = classroomStudentRepository.findAllByClassroom(classroomId, pageable);

        // IDs da página atual
        List<Long> pageUserIds = members.getContent().stream()
                .map(cs -> cs.getStudent().getId())
                .toList();

        // Interações por tipo por aluno
        List<UserInteractionTypeProjection> interactions = logAnswersAgentsRepository
                .groupByUserAndInteractionType(pageUserIds, from, to);

        // Valid/invalid por aluno (só RUN e SUBMIT)
        List<UserValidityProjection> validity = logAnswersAgentsRepository
                .groupByUserAndValidity(pageUserIds, from, to);

        // Quem submeteu
        List<Long> submittedIds = functionRepository
                .findUserIdsWithFunctionByGame(pageUserIds, classroom.getGame().getId());

        return members.map(cs -> {
            Long userId = cs.getStudent().getId();
            String userName = cs.getStudent().getName();

            long feedback = interactions.stream()
                    .filter(i -> i.getUserId().equals(userId) &&
                            InteractionType.FEEDBACK.equals(i.getInteractionType()))
                    .mapToLong(UserInteractionTypeProjection::getTotal)
                    .sum();

            long run = interactions.stream()
                    .filter(i -> i.getUserId().equals(userId) &&
                            InteractionType.RUN.equals(i.getInteractionType()))
                    .mapToLong(UserInteractionTypeProjection::getTotal)
                    .sum();

            long submit = interactions.stream()
                    .filter(i -> i.getUserId().equals(userId) &&
                            InteractionType.SUBMIT.equals(i.getInteractionType()))
                    .mapToLong(UserInteractionTypeProjection::getTotal)
                    .sum();

            long validCount = validity.stream()
                    .filter(v -> v.getUserId().equals(userId) && Boolean.TRUE.equals(v.getValid()))
                    .mapToLong(UserValidityProjection::getTotal)
                    .sum();

            long invalidCount = validity.stream()
                    .filter(v -> v.getUserId().equals(userId) && Boolean.FALSE.equals(v.getValid()))
                    .mapToLong(UserValidityProjection::getTotal)
                    .sum();

            boolean hasSubmitted = submittedIds.contains(userId);

            long totalInteracoes = feedback + run + submit;

            StudentEngagementStatus status;
            if (hasSubmitted) {
                status = StudentEngagementStatus.SUBMITTED;
            } else if (totalInteracoes == 0) {
                status = StudentEngagementStatus.INACTIVE;
            } else if (totalInteracoes >= TRAVADO) {
                status = StudentEngagementStatus.STUCK;
            } else {
                status = StudentEngagementStatus.ACTIVE;
            }

            return new DashboardEngagementDTO(userId, userName, feedback, run, submit,
                    validCount, invalidCount, hasSubmitted, status);
        });
    }

    @Transactional(readOnly = true)
    public DashboardOverviewDTO getOverview(Long classroomId, LocalDateTime from, LocalDateTime to) {
        List<Long> studentIds = getStudentIdsAndValidateAccess(classroomId);

        if (studentIds.isEmpty()) {
            return new DashboardOverviewDTO(0L, 0L, 0L, 0L);
        }

        Classroom classroom = classroomRepository.findByIdWithDetails(classroomId).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        Long totalAlunos = (long) studentIds.size();

        Long totalSubmeteram = (long) functionRepository
                .findUserIdsWithFunctionByGame(studentIds, classroom.getGame().getId())
                .size();

        Long totalAtivos = (long) logAnswersAgentsRepository
                .findActiveUserIds(studentIds, from, to)
                .size();

        Long totalInteracoes = logAnswersAgentsRepository
                .countByUserIds(studentIds, from, to)
                .stream()
                .mapToLong(UserCountProjection::getTotal)
                .sum();

        log.info("Dashboard overview gerado. turmaId={}, totalAlunos={}, submeteram={}, ativos={}, interacoes={}",
                classroomId, totalAlunos, totalSubmeteram, totalAtivos, totalInteracoes);

        return new DashboardOverviewDTO(totalAlunos, totalSubmeteram, totalAtivos, totalInteracoes);
    }

    @Transactional(readOnly = true)
    public List<DashboardRankingDTO> getRanking(Long classroomId) {
        List<Long> studentIds = getStudentIdsAndValidateAccess(classroomId);

        if (studentIds.isEmpty()) {
            return List.of();
        }

        Classroom classroom = classroomRepository.findByIdWithDetails(classroomId).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        // Busca todos os membros com nome
        List<ClassroomStudent> members = classroomStudentRepository.findAllByClassroom(classroomId);

        // Busca vitórias
        List<UserCountProjection> wins = matchRepository.countWinsByUserIds(classroomId);

        // Monta mapa userId -> wins para lookup rápido
        Map<Long, Long> winsMap = wins.stream()
                .collect(Collectors.toMap(UserCountProjection::getUserId, UserCountProjection::getTotal));

        // Monta lista com todos os membros, 0 vitórias para quem não tem
        List<DashboardRankingDTO> ranking = new ArrayList<>();
        for (ClassroomStudent cs : members) {
            Long userId = cs.getStudent().getId();
            Long userWins = winsMap.getOrDefault(userId, 0L);
            ranking.add(new DashboardRankingDTO(0, userId, cs.getStudent().getName(), userWins));
        }

        // Ordena por vitórias decrescente
        ranking.sort((a, b) -> Long.compare(b.getWins(), a.getWins()));

        // Atribui posição
        for (int i = 0; i < ranking.size(); i++) {
            DashboardRankingDTO dto = ranking.get(i);
            ranking.set(i, new DashboardRankingDTO(i + 1, dto.getUserId(), dto.getUserName(), dto.getWins()));
        }

        log.info("Ranking gerado. turmaId={}, totalMembros={}", classroomId, ranking.size());

        return ranking;
    }
    @Transactional(readOnly = true)
    public Page<DashboardMatchDTO> getRecentMatches(Long classroomId, Pageable pageable) {
        getStudentIdsAndValidateAccess(classroomId);
        Page<Match> matches = matchRepository.findByClassroomId(classroomId, pageable);
        return matches.map(DashboardMatchDTO::new);
    }


    private List<Long> getStudentIdsAndValidateAccess(Long classroomId) {
        var user = userService.authenticated();
        Classroom classroom = classroomRepository.findByIdWithDetails(classroomId).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = classroom.getInstructor().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ClassroomAccessDeniedException("Você não tem acesso ao dashboard desta turma");
        }

        return classroomStudentRepository.findStudentIdsByClassroom(classroomId);
    }

}