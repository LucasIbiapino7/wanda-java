package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.entities.LogAnswersAgents;
import com.cosmo.wanda_web.entities.ProfileType;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.GameRepository;
import com.cosmo.wanda_web.repositories.LogAnswersAgentsRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.utils.InteractionType;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunctionServiceTest {

    @Mock
    private PythonClient pythonClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FunctionRepository functionRepository;

    @Mock
    private LogAnswersAgentsRepository logAnswersAgentsRepository;

    @Mock
    private UserService userService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private MatchOrchestrator matchOrchestrator;

    @InjectMocks
    private FunctionService functionService;

    private User user;
    private Game game;
    private FunctionRequestDTO dto;
    private ValidateResponseDTO responseValida;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Aluno");
        user.setEmail("aluno@test.com");
        user.setPassword("senha");
        user.setProfileType(ProfileType.STUDENT);

        game = new Game(1L, "jokenpo", "Teste");

        dto = new FunctionRequestDTO("def func(): pass", "SUCCINCT", "jokenpo1", "jokenpo");

        responseValida = new ValidateResponseDTO(true, "ok");
        responseValida.setThought("");

        when(userService.authenticated()).thenReturn(user);
        when(gameRepository.findByNameIgnoreCase("jokenpo")).thenReturn(Optional.of(game));
        when(logAnswersAgentsRepository.save(any())).thenAnswer(inv -> {
            LogAnswersAgents log = inv.getArgument(0);
            log.setId(1L);
            return log;
        });
    }

    // ------------------------------------------------------------------
    // feedback — deve gravar FEEDBACK
    // ------------------------------------------------------------------

    @Test
    void feedback_deveGravarInteractionTypeFeedback() {
        when(pythonClient.feedback(any())).thenReturn(responseValida);

        functionService.feedback(dto);

        ArgumentCaptor<LogAnswersAgents> captor = ArgumentCaptor.forClass(LogAnswersAgents.class);
        verify(logAnswersAgentsRepository).save(captor.capture());

        assertThat(captor.getValue().getInteractionType()).isEqualTo(InteractionType.FEEDBACK);
    }

    // ------------------------------------------------------------------
    // runTests — deve gravar RUN
    // ------------------------------------------------------------------

    @Test
    void runTests_deveGravarInteractionTypeRun() {
        when(pythonClient.run(any())).thenReturn(responseValida);

        functionService.runTests(dto);

        ArgumentCaptor<LogAnswersAgents> captor = ArgumentCaptor.forClass(LogAnswersAgents.class);
        verify(logAnswersAgentsRepository).save(captor.capture());

        assertThat(captor.getValue().getInteractionType()).isEqualTo(InteractionType.RUN);
    }

    // ------------------------------------------------------------------
    // validate — deve gravar SUBMIT
    // ------------------------------------------------------------------

    @Test
    void validate_deveGravarInteractionTypeSubmit() {
        when(pythonClient.validate(any())).thenReturn(responseValida);
        when(functionRepository.findByUserIdAndName(any(), any())).thenReturn(Optional.empty());
        when(functionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        functionService.validate(dto);

        ArgumentCaptor<LogAnswersAgents> captor = ArgumentCaptor.forClass(LogAnswersAgents.class);
        verify(logAnswersAgentsRepository).save(captor.capture());

        assertThat(captor.getValue().getInteractionType()).isEqualTo(InteractionType.SUBMIT);
    }

    @Test
    void validate_deveGravarSubmitMesmoQuandoFuncaoInvalida() {
        ValidateResponseDTO responseInvalida = new ValidateResponseDTO(false, "erro");
        responseInvalida.setThought("");
        when(pythonClient.validate(any())).thenReturn(responseInvalida);

        functionService.validate(dto);

        ArgumentCaptor<LogAnswersAgents> captor = ArgumentCaptor.forClass(LogAnswersAgents.class);
        verify(logAnswersAgentsRepository).save(captor.capture());

        assertThat(captor.getValue().getInteractionType()).isEqualTo(InteractionType.SUBMIT);
    }
}