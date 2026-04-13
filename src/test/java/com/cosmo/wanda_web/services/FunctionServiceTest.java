package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.repositories.*;
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
    private FunctionHistoryRepository functionHistoryRepository;

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

    @Test
    void validate_deveSalvarHistoricoQuandoFuncaoJaExiste() {
        Function funcaoExistente = new Function();
        funcaoExistente.setId(10L);
        funcaoExistente.setName("jokenpo1");
        funcaoExistente.setFunction("def func_antiga(): pass");
        funcaoExistente.setPlayer(user);
        funcaoExistente.setGame(game);

        when(pythonClient.validate(any())).thenReturn(responseValida);
        when(functionRepository.findByUserIdAndName(any(), any()))
                .thenReturn(Optional.of(funcaoExistente));
        when(functionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(functionHistoryRepository.findMaxVersionByFunctionId(10L))
                .thenReturn(Optional.empty()); // primeira versão
        when(functionHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        functionService.validate(dto);

        ArgumentCaptor<FunctionHistory> captor = ArgumentCaptor.forClass(FunctionHistory.class);
        verify(functionHistoryRepository).save(captor.capture());

        FunctionHistory historico = captor.getValue();
        assertThat(historico.getCode()).isEqualTo("def func_antiga(): pass"); // código antigo
        assertThat(historico.getVersionNumber()).isEqualTo(1); // primeira versão
        assertThat(historico.getPlayer()).isEqualTo(user);
        assertThat(historico.getGame()).isEqualTo(game);
        assertThat(historico.getFunction()).isEqualTo(funcaoExistente);
    }

    @Test
    void validate_deveIncrementarVersionNumberQuandoJaExisteHistorico() {
        Function funcaoExistente = new Function();
        funcaoExistente.setId(10L);
        funcaoExistente.setName("jokenpo1");
        funcaoExistente.setFunction("def func_v2(): pass");
        funcaoExistente.setPlayer(user);
        funcaoExistente.setGame(game);

        when(pythonClient.validate(any())).thenReturn(responseValida);
        when(functionRepository.findByUserIdAndName(any(), any()))
                .thenReturn(Optional.of(funcaoExistente));
        when(functionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(functionHistoryRepository.findMaxVersionByFunctionId(10L))
                .thenReturn(Optional.of(2)); // já existe versão 2
        when(functionHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        functionService.validate(dto);

        ArgumentCaptor<FunctionHistory> captor = ArgumentCaptor.forClass(FunctionHistory.class);
        verify(functionHistoryRepository).save(captor.capture());

        assertThat(captor.getValue().getVersionNumber()).isEqualTo(3); // incrementou
    }

    @Test
    void validate_naoDeveSalvarHistoricoQuandoFuncaoEhNova() {
        when(pythonClient.validate(any())).thenReturn(responseValida);
        when(functionRepository.findByUserIdAndName(any(), any()))
                .thenReturn(Optional.empty()); // função nova
        when(functionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        functionService.validate(dto);

        verify(functionHistoryRepository, never()).save(any());
    }

    @Test
    void validate_naoDeveSalvarHistoricoQuandoFuncaoInvalida() {
        ValidateResponseDTO responseInvalida = new ValidateResponseDTO(false, "erro");
        responseInvalida.setThought("");
        when(pythonClient.validate(any())).thenReturn(responseInvalida);

        functionService.validate(dto);

        verify(functionHistoryRepository, never()).save(any());
    }
}