package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.config.SecurityFilter;
import com.cosmo.wanda_web.dto.function.FeedbackResponseDTO;
import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.function.FunctionResponseDto;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.infra.GameEngine;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import com.cosmo.wanda_web.repositories.*;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.exceptions.DatabaseException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.AssistantStyle;
import com.cosmo.wanda_web.services.utils.InteractionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FunctionService {

    private static final Logger log = LoggerFactory.getLogger(FunctionService.class);

    @Autowired
    private PythonClient pythonClient;

    @Autowired
    private FunctionHistoryRepository functionHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private LogAnswersAgentsRepository logAnswersAgentsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MatchOrchestrator matchOrchestrator;

    @Transactional
    public FeedbackResponseDTO feedback(FunctionRequestDTO dto) {
        User user = userService.authenticated();
        log.info("Feedback solicitado. game={}, functionName={}, assistantStyle={}",
                dto.getGameName(), dto.getFunctionName(), dto.getAssistantStyle());
        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("Jogo ao encontrado!")
        );
        ValidateResponseDTO response = pythonClient.feedback(dto);
        log.info("Feedback retornado. valid={}, game={}, functionName={}",
                response.getValid(), dto.getGameName(), dto.getFunctionName());
        Long feedbackId = saveLogAnswer(dto, response, user, game, InteractionType.FEEDBACK);
        return new FeedbackResponseDTO(response, feedbackId);
    }

    @Transactional
    public FeedbackResponseDTO runTests(FunctionRequestDTO dto) {
        User user = userService.authenticated();
        log.info("Run tests iniciado. game={}, functionName={}", dto.getGameName(), dto.getFunctionName());
        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("Jogo nao encontrado!")
        );
        ValidateResponseDTO response = pythonClient.run(dto);
        log.info("Run tests concluído. valid={}, game={}, functionName={}",
                response.getValid(), dto.getGameName(), dto.getFunctionName());
        Long feedbackId = saveLogAnswer(dto, response, user, game, InteractionType.RUN);
        return new FeedbackResponseDTO(response, feedbackId);
    }

    // Método que de fato salva uma função no BD
    @Transactional
    public FeedbackResponseDTO validate(FunctionRequestDTO dto){
        // Verifica se usuário existe pelo contexto
        User user = userService.authenticated();
        log.info("Validação iniciada. game={}, functionName={}", dto.getGameName(), dto.getFunctionName());
        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("Jogo ao encontrado!")
        );
        // Validar a função com o microservice Python
        ValidateResponseDTO response = pythonClient.validate(dto);
        Long feedbackId = saveLogAnswer(dto, response, user, game, InteractionType.SUBMIT);
        // Verifica se a função é válida
        if (!response.getValid()){
            log.info("Função inválida. game={}, functionName={}", dto.getGameName(), dto.getFunctionName());
            return new FeedbackResponseDTO(response, feedbackId); // Early return caso a funcao seja invalida
        }
        log.info("Função válida, salvando. game={}, functionName={}", dto.getGameName(), dto.getFunctionName());
        // Adiciona no Banco de dados a função aprovada
        saveOrUpdateFunction(dto, user, game);
        return new FeedbackResponseDTO(response, feedbackId);
    }

    @Transactional
    private void saveOrUpdateFunction(FunctionRequestDTO dto, User user, Game game) {
        Optional<Function> result = functionRepository.findByUserIdAndName(user.getId(), dto.getFunctionName());
        Function function;
        if (result.isPresent()) {
            function = result.get();

            // Salva o código atual como histórico antes de sobrescrever
            int nextVersion = functionHistoryRepository
                    .findMaxVersionByFunctionId(function.getId())
                    .map(v -> v + 1)
                    .orElse(1);

            FunctionHistory history = new FunctionHistory();
            history.setFunction(function);
            history.setPlayer(user);
            history.setGame(game);
            history.setCode(function.getFunction());
            history.setSubmittedAt(LocalDateTime.now());
            history.setVersionNumber(nextVersion);
            functionHistoryRepository.save(history);

            function.setFunction(dto.getCode());
            function.setUpdatedAt(LocalDateTime.now());
        } else {
            function = new Function(dto.getFunctionName(), dto.getCode(), user, game);
            function.setCreatedAt(LocalDateTime.now());
        }
        functionRepository.save(function);
    }

    private ValidateResponseDTO validateMock(FunctionRequestDTO dto) {
        return new ValidateResponseDTO(true, "ok");
    }

    @Transactional(readOnly = true)
    public FunctionResponseDto getFunctionByGameName(String gameName) {
        User user = userService.authenticated();
        FunctionResponseDto response = functionRepository.findByUserIdAndGameName(user.getId(), gameName).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));
        return response;
    }

    @Transactional(readOnly = true)
    public FunctionRequestDTO findJokenpo1ByUser() {
        User user = userService.authenticated();

        Function function = functionRepository.findJokenpo1ByPlayerId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));

        return new FunctionRequestDTO(function.getFunction());
    }

    @Transactional(readOnly = true)
    public FunctionRequestDTO findJokenpo2ByUser() {
        User user = userService.authenticated();

        Function function = functionRepository.findJokenpo2ByPlayerId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));

        return new FunctionRequestDTO(function.getFunction());
    }

    @Transactional(readOnly = true)
    public FunctionRequestDTO findById(Long id) {
        Function function = functionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));
        return new FunctionRequestDTO(function.getFunction());
    }

    @Transactional
    public FunctionRequestDTO update(FunctionRequestDTO dto) {
        User user = userService.authenticated();
        Function function = functionRepository.findJokenpo1ByPlayerId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));
        function.setFunction(dto.getCode());
        function = functionRepository.save(function);
        return new FunctionRequestDTO(function.getFunction());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete() {
        User user = userService.authenticated();
        Function function = functionRepository.findJokenpo1ByPlayerId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));
        try {
            functionRepository.delete(function);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    public boolean verifyFunctionsByGame(User user, String gameName) {
        GameEngine engine = matchOrchestrator.getEngine(gameName);
        return engine.hasAllFunctions(user.getId(), functionRepository);
    }

    @Transactional
    private Long saveLogAnswer(FunctionRequestDTO dto, ValidateResponseDTO response, User user, Game game, InteractionType interactionType) {
        LogAnswersAgents log = new LogAnswersAgents();
        log.setCode(dto.getCode());
        log.setAnswer(response.getAnswer());
        String thought = (!response.getThought().equals("")) ? response.getThought() : null;
        log.setThought(thought);
        log.setValid(response.getValid());
        log.setAssistantStyle(AssistantStyle.valueOf(dto.getAssistantStyle()));
        log.setMoment(LocalDateTime.now());
        log.setUser(user);
        log.setFunctionName(dto.getFunctionName());
        log.setGame(game);
        log.setInteractionType(interactionType);
        LogAnswersAgents save = logAnswersAgentsRepository.save(log);
        return save.getId();
    }
}
