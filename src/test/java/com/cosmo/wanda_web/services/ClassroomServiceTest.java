package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.classroom.*;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.repositories.*;
import com.cosmo.wanda_web.services.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {

    @Mock private ClassroomRepository classroomRepository;
    @Mock private ClassroomStudentRepository classroomStudentRepository;
    @Mock private GameRepository gameRepository;
    @Mock private UserRepository userRepository;
    @Mock private FunctionRepository functionRepository;
    @Mock private TournamentRepository tournamentRepository;
    @Mock private UserService userService;

    @InjectMocks
    private ClassroomService classroomService;

    private User instructor;
    private User student;
    private User otherUser;
    private Game game;
    private Classroom activeClassroom;
    private Classroom archivedClassroom;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        instructor = new User();
        instructor.setId(1L);
        instructor.setName("Professor");
        instructor.setEmail("professor@test.com");
        instructor.setPassword("senha");
        instructor.setProfileType(ProfileType.INSTRUCTOR);

        student = new User();
        student.setId(2L);
        student.setName("Aluno");
        student.setEmail("aluno@test.com");
        student.setPassword("senha");
        student.setProfileType(ProfileType.STUDENT);

        otherUser = new User();
        otherUser.setId(3L);
        otherUser.setName("Outro");
        otherUser.setEmail("outro@test.com");
        otherUser.setPassword("senha");
        otherUser.setProfileType(ProfileType.STUDENT);

        game = new Game(1L, "jokenpo", "Jogo de Jokenpô");

        activeClassroom = new Classroom();
        activeClassroom.setId(10L);
        activeClassroom.setName("Turma A");
        activeClassroom.setCourse("Engenharia");
        activeClassroom.setStatus(ClassroomStatus.ACTIVE);
        activeClassroom.setAccessCode("ABC123");
        activeClassroom.setCreatedAt(LocalDateTime.now());
        activeClassroom.setInstructor(instructor);
        activeClassroom.setGame(game);

        archivedClassroom = new Classroom();
        archivedClassroom.setId(11L);
        archivedClassroom.setName("Turma Arquivada");
        archivedClassroom.setStatus(ClassroomStatus.ARCHIVED);
        archivedClassroom.setAccessCode("XYZ789");
        archivedClassroom.setCreatedAt(LocalDateTime.now());
        archivedClassroom.setInstructor(instructor);
        archivedClassroom.setGame(game);

        pageable = PageRequest.of(0, 20);
    }

    // ================================================================
    // create
    // ================================================================

    @Test
    void create_deveCriarTurmaComSucesso() {
        ClassroomCreateDTO dto = new ClassroomCreateDTO();
        dto.setName("Turma Nova");
        dto.setGameId(1L);
        dto.setCourse("Ciência da Computação");
        dto.setInstitution("UFMA");

        when(userService.authenticated()).thenReturn(instructor);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(classroomRepository.existsActiveByAccessCode(any())).thenReturn(false);
        when(classroomRepository.save(any())).thenAnswer(inv -> {
            Classroom c = inv.getArgument(0);
            c.setId(99L);
            return c;
        });

        ClassroomResponseDTO response = classroomService.create(dto);

        assertThat(response.getName()).isEqualTo("Turma Nova");
        assertThat(response.getCourse()).isEqualTo("Ciência da Computação");
        assertThat(response.getInstitution()).isEqualTo("UFMA");
        assertThat(response.getStatus()).isEqualTo(ClassroomStatus.ACTIVE);
        assertThat(response.getAccessCode()).isNotBlank();
        assertThat(response.getAccessCode()).hasSize(6);
        assertThat(response.getGameId()).isEqualTo(1L);
        assertThat(response.getInstructorId()).isEqualTo(instructor.getId());
        verify(classroomRepository).save(any(Classroom.class));
    }

    @Test
    void create_deveLancarExcecaoQuandoJogoNaoEncontrado() {
        ClassroomCreateDTO dto = new ClassroomCreateDTO();
        dto.setName("Turma");
        dto.setGameId(99L);

        when(userService.authenticated()).thenReturn(instructor);
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classroomService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Jogo não encontrado");

        verify(classroomRepository, never()).save(any());
    }

    @Test
    void create_deveGerarNovoCodigoQuandoPrimeiroJaExiste() {
        ClassroomCreateDTO dto = new ClassroomCreateDTO();
        dto.setName("Turma");
        dto.setGameId(1L);

        when(userService.authenticated()).thenReturn(instructor);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(classroomRepository.existsActiveByAccessCode(any()))
                .thenReturn(true)
                .thenReturn(false);
        when(classroomRepository.save(any())).thenAnswer(inv -> {
            Classroom c = inv.getArgument(0);
            c.setId(99L);
            return c;
        });

        classroomService.create(dto);

        verify(classroomRepository, atLeast(2)).existsActiveByAccessCode(any());
    }

    // ================================================================
    // update
    // ================================================================

    @Test
    void update_deveAtualizarComSucesso() {
        ClassroomUpdateDTO dto = new ClassroomUpdateDTO();
        dto.setName("Turma Atualizada");
        dto.setMural("Bem vindos!");

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ClassroomResponseDTO response = classroomService.update(10L, dto);

        assertThat(response.getName()).isEqualTo("Turma Atualizada");
        assertThat(response.getMural()).isEqualTo("Bem vindos!");
        assertThat(response.getCourse()).isEqualTo("Engenharia");
        verify(classroomRepository).save(activeClassroom);
    }

    @Test
    void update_deveLancarExcecaoQuandoTurmaNaoEncontrada() {
        when(classroomRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classroomService.update(99L, new ClassroomUpdateDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma não encontrada");
    }

    @Test
    void update_deveLancarExcecaoQuandoUsuarioNaoEhInstructor() {
        when(userService.authenticated()).thenReturn(otherUser);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));

        assertThatThrownBy(() -> classroomService.update(10L, new ClassroomUpdateDTO()))
                .isInstanceOf(ClassroomAccessDeniedException.class);
    }

    @Test
    void update_deveLancarExcecaoQuandoTurmaArquivada() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(11L)).thenReturn(Optional.of(archivedClassroom));

        assertThatThrownBy(() -> classroomService.update(11L, new ClassroomUpdateDTO()))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("arquivada");
    }

    // ================================================================
    // archive
    // ================================================================

    @Test
    void archive_deveArquivarComSucesso() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(tournamentRepository.cancelActiveByClassroom(10L)).thenReturn(0);

        classroomService.archive(10L);

        assertThat(activeClassroom.getStatus()).isEqualTo(ClassroomStatus.ARCHIVED);
        verify(classroomRepository).save(activeClassroom);
    }

    @Test
    void archive_deveCancelarTorneiosAtivos() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(tournamentRepository.cancelActiveByClassroom(10L)).thenReturn(2);

        classroomService.archive(10L);

        verify(tournamentRepository).cancelActiveByClassroom(10L);
    }

    @Test
    void archive_deveLancarExcecaoQuandoTurmaNaoEncontrada() {
        when(classroomRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classroomService.archive(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma não encontrada");
    }

    @Test
    void archive_deveLancarExcecaoQuandoUsuarioNaoTemPermissao() {
        when(userService.authenticated()).thenReturn(otherUser);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));

        assertThatThrownBy(() -> classroomService.archive(10L))
                .isInstanceOf(ClassroomAccessDeniedException.class);
    }

    @Test
    void archive_deveLancarExcecaoQuandoTurmaJaArquivada() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(11L)).thenReturn(Optional.of(archivedClassroom));

        assertThatThrownBy(() -> classroomService.archive(11L))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("já está arquivada");
    }

    // ================================================================
    // findById
    // ================================================================

    @Test
    void findById_deveRetornarQuandoEhInstructor() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));

        ClassroomResponseDTO response = classroomService.findById(10L);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Turma A");
    }

    @Test
    void findById_deveRetornarQuandoEhMembro() {
        when(userService.authenticated()).thenReturn(student);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, student.getId())).thenReturn(true);

        ClassroomResponseDTO response = classroomService.findById(10L);

        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    void findById_deveLancarExcecaoQuandoNaoTemAcesso() {
        when(userService.authenticated()).thenReturn(otherUser);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, otherUser.getId())).thenReturn(false);

        assertThatThrownBy(() -> classroomService.findById(10L))
                .isInstanceOf(ClassroomAccessDeniedException.class);
    }

    @Test
    void findById_deveLancarExcecaoQuandoTurmaNaoEncontrada() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classroomService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Turma não encontrada");
    }

    // ================================================================
    // regenerateAccessCode
    // ================================================================

    @Test
    void regenerateAccessCode_deveGerarNovoCodigoComSucesso() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomRepository.existsActiveByAccessCode(any())).thenReturn(false);
        when(classroomRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ClassroomResponseDTO response = classroomService.regenerateAccessCode(10L);

        assertThat(response.getAccessCode()).isNotBlank();
        assertThat(response.getAccessCode()).hasSize(6);
        assertThat(response.getAccessCode()).isNotEqualTo("ABC123");
    }

    @Test
    void regenerateAccessCode_deveLancarExcecaoQuandoNaoEhInstructor() {
        when(userService.authenticated()).thenReturn(otherUser);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));

        assertThatThrownBy(() -> classroomService.regenerateAccessCode(10L))
                .isInstanceOf(ClassroomAccessDeniedException.class);
    }

    @Test
    void regenerateAccessCode_deveLancarExcecaoQuandoTurmaArquivada() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(11L)).thenReturn(Optional.of(archivedClassroom));

        assertThatThrownBy(() -> classroomService.regenerateAccessCode(11L))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("arquivada");
    }

    // ================================================================
    // addStudentByEmail
    // ================================================================

    @Test
    void addStudentByEmail_deveAdicionarComSucesso() {
        ClassroomAddStudentDTO dto = new ClassroomAddStudentDTO();
        dto.setEmail("aluno@test.com");

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(userRepository.findByEmail("aluno@test.com")).thenReturn(student);
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, student.getId())).thenReturn(false);
        when(classroomStudentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        classroomService.addStudentByEmail(10L, dto);

        ArgumentCaptor<ClassroomStudent> captor = ArgumentCaptor.forClass(ClassroomStudent.class);
        verify(classroomStudentRepository).save(captor.capture());
        assertThat(captor.getValue().getStudent().getId()).isEqualTo(student.getId());
        assertThat(captor.getValue().getClassroom().getId()).isEqualTo(10L);
        assertThat(captor.getValue().getJoinedAt()).isNotNull();
    }

    @Test
    void addStudentByEmail_deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        ClassroomAddStudentDTO dto = new ClassroomAddStudentDTO();
        dto.setEmail("naoexiste@test.com");

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(null);

        assertThatThrownBy(() -> classroomService.addStudentByEmail(10L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void addStudentByEmail_deveLancarExcecaoQuandoJaEhMembro() {
        ClassroomAddStudentDTO dto = new ClassroomAddStudentDTO();
        dto.setEmail("aluno@test.com");

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(userRepository.findByEmail("aluno@test.com")).thenReturn(student);
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, student.getId())).thenReturn(true);

        assertThatThrownBy(() -> classroomService.addStudentByEmail(10L, dto))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("já é membro");

        verify(classroomStudentRepository, never()).save(any());
    }

    @Test
    void addStudentByEmail_deveLancarExcecaoQuandoTurmaArquivada() {
        ClassroomAddStudentDTO dto = new ClassroomAddStudentDTO();
        dto.setEmail("aluno@test.com");

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(11L)).thenReturn(Optional.of(archivedClassroom));

        assertThatThrownBy(() -> classroomService.addStudentByEmail(11L, dto))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("arquivada");
    }

    // ================================================================
    // removeStudent
    // ================================================================

    @Test
    void removeStudent_deveRemoverComSucesso() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, student.getId())).thenReturn(true);

        classroomService.removeStudent(10L, student.getId());

        verify(classroomStudentRepository).deleteByClassroomAndStudent(10L, student.getId());
    }

    @Test
    void removeStudent_deveLancarExcecaoQuandoAlunoNaoEhMembro() {
        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, otherUser.getId())).thenReturn(false);

        assertThatThrownBy(() -> classroomService.removeStudent(10L, otherUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("não é membro");

        verify(classroomStudentRepository, never()).deleteByClassroomAndStudent(any(), any());
    }

    @Test
    void removeStudent_deveLancarExcecaoQuandoNaoEhInstructor() {
        when(userService.authenticated()).thenReturn(otherUser);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));

        assertThatThrownBy(() -> classroomService.removeStudent(10L, student.getId()))
                .isInstanceOf(ClassroomAccessDeniedException.class);
    }

    // ================================================================
    // joinByCode
    // ================================================================

    @Test
    void joinByCode_deveEntrarNaTurmaComSucesso() {
        ClassroomJoinDTO dto = new ClassroomJoinDTO();
        dto.setAccessCode("ABC123");

        when(userService.authenticated()).thenReturn(student);
        when(classroomRepository.findByAccessCode("ABC123")).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, student.getId())).thenReturn(false);
        when(classroomStudentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ClassroomResponseDTO response = classroomService.joinByCode(dto);

        assertThat(response.getId()).isEqualTo(10L);
        verify(classroomStudentRepository).save(any(ClassroomStudent.class));
    }

    @Test
    void joinByCode_deveLancarExcecaoQuandoCodigoInvalido() {
        ClassroomJoinDTO dto = new ClassroomJoinDTO();
        dto.setAccessCode("INVALIDO");

        when(userService.authenticated()).thenReturn(student);
        when(classroomRepository.findByAccessCode("INVALIDO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classroomService.joinByCode(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("código inválido");
    }

    @Test
    void joinByCode_deveLancarExcecaoQuandoJaEhMembro() {
        ClassroomJoinDTO dto = new ClassroomJoinDTO();
        dto.setAccessCode("ABC123");

        when(userService.authenticated()).thenReturn(student);
        when(classroomRepository.findByAccessCode("ABC123")).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, student.getId())).thenReturn(true);

        assertThatThrownBy(() -> classroomService.joinByCode(dto))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("já é membro");

        verify(classroomStudentRepository, never()).save(any());
    }

    @Test
    void joinByCode_deveLancarExcecaoQuandoEhInstructorDaTurma() {
        ClassroomJoinDTO dto = new ClassroomJoinDTO();
        dto.setAccessCode("ABC123");

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByAccessCode("ABC123")).thenReturn(Optional.of(activeClassroom));

        assertThatThrownBy(() -> classroomService.joinByCode(dto))
                .isInstanceOf(ClassroomException.class)
                .hasMessageContaining("instructor");

        verify(classroomStudentRepository, never()).save(any());
    }

    // ================================================================
    // findMembers
    // ================================================================

    @Test
    void findMembers_deveRetornarMembrosComStatusDeSubmissao() {
        ClassroomStudent cs = new ClassroomStudent(activeClassroom, student, LocalDateTime.now());
        Page<ClassroomStudent> membersPage = new PageImpl<>(List.of(cs));

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.findAllByClassroom(eq(10L), any(Pageable.class))).thenReturn(membersPage);
        when(functionRepository.findUserIdsWithFunctionByGame(List.of(student.getId()), 1L))
                .thenReturn(List.of(student.getId()));

        Page<ClassroomMemberDTO> result = classroomService.findMembers(10L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(student.getId());
        assertThat(result.getContent().get(0).isHasSubmitted()).isTrue();
    }

    @Test
    void findMembers_deveRetornarHasSubmittedFalseQuandoNaoSubmeteu() {
        ClassroomStudent cs = new ClassroomStudent(activeClassroom, student, LocalDateTime.now());
        Page<ClassroomStudent> membersPage = new PageImpl<>(List.of(cs));

        when(userService.authenticated()).thenReturn(instructor);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.findAllByClassroom(eq(10L), any(Pageable.class))).thenReturn(membersPage);
        when(functionRepository.findUserIdsWithFunctionByGame(List.of(student.getId()), 1L))
                .thenReturn(Collections.emptyList());

        Page<ClassroomMemberDTO> result = classroomService.findMembers(10L, pageable);

        assertThat(result.getContent().get(0).isHasSubmitted()).isFalse();
    }

    @Test
    void findMembers_deveLancarExcecaoQuandoNaoTemAcesso() {
        when(userService.authenticated()).thenReturn(otherUser);
        when(classroomRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(activeClassroom));
        when(classroomStudentRepository.existsByClassroomAndStudent(10L, otherUser.getId())).thenReturn(false);

        assertThatThrownBy(() -> classroomService.findMembers(10L, pageable))
                .isInstanceOf(ClassroomAccessDeniedException.class);
    }
}