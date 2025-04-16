package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.function.FeedbackResponseDTO;
import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.LogAnswersAgents;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.LogAnswersAgentsRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.exceptions.DatabaseException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.AssistantStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FunctionService {

    @Autowired
    private PythonClient pythonClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private LogAnswersAgentsRepository logAnswersAgentsRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public FeedbackResponseDTO feedback(FunctionRequestDTO dto) {
        User user = userService.authenticated();
        ValidateResponseDTO response = pythonClient.feedback(dto);
        Long feedbackId = saveLogAnswer(dto, response, user);
        return new FeedbackResponseDTO(response, feedbackId);
    }

    @Transactional
    public FeedbackResponseDTO runTests(FunctionRequestDTO dto) {
        User user = userService.authenticated();
        ValidateResponseDTO response = pythonClient.run(dto);
        Long feedbackId = saveLogAnswer(dto, response, user);
        return new FeedbackResponseDTO(response, feedbackId);
    }

    @Transactional
    public FeedbackResponseDTO validate(FunctionRequestDTO dto){
        // Validar a função com o microservice Python
        ValidateResponseDTO response = pythonClient.validate(dto);
        // Verifica se usuário existe pelo contexto
        User user = userService.authenticated();
        Long feedbackId = saveLogAnswer(dto, response, user);
        // Verifica se a função é válida
        if (!response.getValid()){
            return new FeedbackResponseDTO(response, feedbackId); // Early return caso a funcao seja invalida
        }
        // Adiciona no Banco de dados a função aprovada
        saveOrUpdateFunction(dto, user);
        return new FeedbackResponseDTO(response, feedbackId);
    }

    @Transactional
    private void saveOrUpdateFunction(FunctionRequestDTO dto, User user) {
        Optional<Function> result = functionRepository.findByUserIdAndName(user.getId(), dto.getFunctionName());
        Function function;
        if (result.isPresent()){
            function = result.get();
            function.setFunction(dto.getCode());
        }else {
            function = new Function(dto.getFunctionName(), dto.getCode(), user);
        }
        functionRepository.save(function);
    }

    private ValidateResponseDTO validateMock(FunctionRequestDTO dto) {
        return new ValidateResponseDTO(true, "ok");
    }

    @Transactional(readOnly = true)
    public FunctionRequestDTO findJokenpo1ByUser() {
        User user = userService.authenticated();

        Function function = functionRepository.findJokenpo1ByPlayerId(user.getId()).orElseThrow(
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

    @Transactional
    private Long saveLogAnswer(FunctionRequestDTO dto, ValidateResponseDTO response, User user) {
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
        LogAnswersAgents save = logAnswersAgentsRepository.save(log);
        return save.getId();
    }

}
