package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.classroom.*;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.repositories.*;
import com.cosmo.wanda_web.services.exceptions.ClassroomAccessDeniedException;
import com.cosmo.wanda_web.services.exceptions.ClassroomException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassroomService {

    private static final Logger log = LoggerFactory.getLogger(ClassroomService.class);

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private ClassroomStudentRepository classroomStudentRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ClassroomResponseDTO create(ClassroomCreateDTO dto) {
        User instructor = userService.authenticated();

        Game game = gameRepository.findById(dto.getGameId()).orElseThrow(
                () -> new ResourceNotFoundException("Jogo não encontrado")
        );

        String accessCode = generateUniqueAccessCode();

        Classroom classroom = new Classroom();
        classroom.setName(dto.getName());
        classroom.setCourse(dto.getCourse());
        classroom.setDescription(dto.getDescription());
        classroom.setMural(dto.getMural());
        classroom.setInstitution(dto.getInstitution());
        classroom.setCity(dto.getCity());
        classroom.setState(dto.getState());
        classroom.setStatus(ClassroomStatus.ACTIVE);
        classroom.setAccessCode(accessCode);
        classroom.setCreatedAt(LocalDateTime.now());
        classroom.setInstructor(instructor);
        classroom.setGame(game);

        classroom = classroomRepository.save(classroom);

        log.info("Turma criada. turmaId={}, instructorId={}, jogo={}",
                classroom.getId(), instructor.getId(), game.getName());

        return new ClassroomResponseDTO(classroom);
    }

    @Transactional
    public ClassroomResponseDTO update(Long id, ClassroomUpdateDTO dto) {
        User instructor = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        if (!classroom.getInstructor().getId().equals(instructor.getId())) {
            throw new ClassroomAccessDeniedException("Você não tem permissão para editar esta turma");
        }

        if (classroom.getStatus() != ClassroomStatus.ACTIVE) {
            throw new ClassroomException("Não é possível editar uma turma arquivada");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            classroom.setName(dto.getName());
        }
        if (dto.getCourse() != null) classroom.setCourse(dto.getCourse());
        if (dto.getDescription() != null) classroom.setDescription(dto.getDescription());
        if (dto.getMural() != null) classroom.setMural(dto.getMural());
        if (dto.getInstitution() != null) classroom.setInstitution(dto.getInstitution());
        if (dto.getCity() != null) classroom.setCity(dto.getCity());
        if (dto.getState() != null) classroom.setState(dto.getState());

        classroom = classroomRepository.save(classroom);

        log.info("Turma atualizada. turmaId={}, instructorId={}", classroom.getId(), instructor.getId());

        return new ClassroomResponseDTO(classroom);
    }

    @Transactional
    public void archive(Long id) {
        User user = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        boolean isOwner = classroom.getInstructor().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new ClassroomAccessDeniedException("Você não tem permissão para arquivar esta turma");
        }

        if (classroom.getStatus() == ClassroomStatus.ARCHIVED) {
            throw new ClassroomException("Esta turma já está arquivada");
        }

        int torneiosCancelados = tournamentRepository.cancelActiveByClassroom(id);
        if (torneiosCancelados > 0) {
            log.info("Torneios cancelados no arquivamento. turmaId={}, quantidade={}", id, torneiosCancelados);
        }

        classroom.setStatus(ClassroomStatus.ARCHIVED);
        classroomRepository.save(classroom);

        log.info("Turma arquivada. turmaId={}, usuarioId={}", id, user.getId());
    }

    @Transactional(readOnly = true)
    public Page<ClassroomResponseDTO> findAllByInstructor(ClassroomStatus status, Pageable pageable) {
        User instructor = userService.authenticated();
        Page<Classroom> classrooms = classroomRepository.findAllByInstructor(instructor.getId(), status, pageable);
        return classrooms.map(ClassroomResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ClassroomResponseDTO> findAllByStudent(ClassroomStatus status, Pageable pageable) {
        User student = userService.authenticated();
        Page<Classroom> classrooms = classroomRepository.findAllByStudent(student.getId(), status, pageable);
        return classrooms.map(ClassroomResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public ClassroomResponseDTO findById(Long id) {
        User user = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = classroom.getInstructor().getId().equals(user.getId());
        boolean isMember = classroomStudentRepository.existsByClassroomAndStudent(id, user.getId());

        if (!isAdmin && !isOwner && !isMember) {
            throw new ClassroomAccessDeniedException("Você não tem acesso a esta turma");
        }

        return new ClassroomResponseDTO(classroom);
    }

    @Transactional
    public ClassroomResponseDTO regenerateAccessCode(Long id) {
        User instructor = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        if (!classroom.getInstructor().getId().equals(instructor.getId())) {
            throw new ClassroomAccessDeniedException("Você não tem permissão para regenerar o código desta turma");
        }

        if (classroom.getStatus() != ClassroomStatus.ACTIVE) {
            throw new ClassroomException("Não é possível regenerar o código de uma turma arquivada");
        }

        String newCode = generateUniqueAccessCode();
        classroom.setAccessCode(newCode);
        classroomRepository.save(classroom);

        log.info("Código de acesso regenerado. turmaId={}, instructorId={}", id, instructor.getId());

        return new ClassroomResponseDTO(classroom);
    }

    @Transactional
    public void addStudentByEmail(Long id, ClassroomAddStudentDTO dto) {
        User instructor = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        if (!classroom.getInstructor().getId().equals(instructor.getId())) {
            throw new ClassroomAccessDeniedException("Você não tem permissão para adicionar alunos nesta turma");
        }

        if (classroom.getStatus() != ClassroomStatus.ACTIVE) {
            throw new ClassroomException("Não é possível adicionar alunos em uma turma arquivada");
        }

        User student = userRepository.findByEmail(dto.getEmail().toLowerCase().trim());
        if (student == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com o email informado");
        }

        if (student.getId().equals(instructor.getId())) {
            throw new ClassroomException("O instructor não pode ser adicionado como aluno da própria turma");
        }

        if (classroomStudentRepository.existsByClassroomAndStudent(id, student.getId())) {
            throw new ClassroomException("Este aluno já é membro da turma");
        }

        ClassroomStudent member = new ClassroomStudent(classroom, student, LocalDateTime.now());
        classroomStudentRepository.save(member);
        notificationService.create(student.getId(), NotificationType.NEW_CLASSROOM, id);

        log.info("Aluno adicionado à turma. turmaId={}, studentId={}, instructorId={}",
                id, student.getId(), instructor.getId());
    }

    @Transactional
    public void removeStudent(Long id, Long studentId) {
        User instructor = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        if (!classroom.getInstructor().getId().equals(instructor.getId())) {
            throw new ClassroomAccessDeniedException("Você não tem permissão para remover alunos desta turma");
        }

        if (classroom.getStatus() != ClassroomStatus.ACTIVE) {
            throw new ClassroomException("Não é possível remover alunos de uma turma arquivada");
        }

        if (!classroomStudentRepository.existsByClassroomAndStudent(id, studentId)) {
            throw new ResourceNotFoundException("Este aluno não é membro da turma");
        }

        classroomStudentRepository.deleteByClassroomAndStudent(id, studentId);

        log.info("Aluno removido da turma. turmaId={}, studentId={}, instructorId={}",
                id, studentId, instructor.getId());
    }

    @Transactional
    public ClassroomResponseDTO joinByCode(ClassroomJoinDTO dto) {
        User student = userService.authenticated();

        Classroom classroom = classroomRepository.findByAccessCode(dto.getAccessCode().toUpperCase().trim()).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada ou código inválido")
        );

        if (classroom.getInstructor().getId().equals(student.getId())) {
            throw new ClassroomException("O instructor não pode entrar na própria turma como aluno");
        }

        if (classroomStudentRepository.existsByClassroomAndStudent(classroom.getId(), student.getId())) {
            throw new ClassroomException("Você já é membro desta turma");
        }

        ClassroomStudent member = new ClassroomStudent(classroom, student, LocalDateTime.now());
        classroomStudentRepository.save(member);

        log.info("Aluno entrou na turma pelo código. turmaId={}, studentId={}",
                classroom.getId(), student.getId());

        return new ClassroomResponseDTO(classroom);
    }

    @Transactional(readOnly = true)
    public Page<ClassroomMemberDTO> findMembers(Long id, Pageable pageable) {
        User user = userService.authenticated();

        Classroom classroom = classroomRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada")
        );

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = classroom.getInstructor().getId().equals(user.getId());
        boolean isMember = classroomStudentRepository.existsByClassroomAndStudent(id, user.getId());

        if (!isAdmin && !isOwner && !isMember) {
            throw new ClassroomAccessDeniedException("Você não tem acesso a esta turma");
        }

        Page<ClassroomStudent> members = classroomStudentRepository.findAllByClassroom(id, pageable);

        // Pega os IDs da página atual — evita N+1
        List<Long> userIds = members.getContent().stream()
                .map(cs -> cs.getStudent().getId())
                .toList();

        // Busca de uma vez quais já submeteram
        List<Long> submittedIds = functionRepository.findUserIdsWithFunctionByGame(
                userIds, classroom.getGame().getId()
        );

        return members.map(cs -> new ClassroomMemberDTO(cs, submittedIds.contains(cs.getStudent().getId())));
    }

    private String generateUniqueAccessCode() {
        String code;
        do {
            code = generateCode();
        } while (classroomRepository.existsActiveByAccessCode(code));
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

}
