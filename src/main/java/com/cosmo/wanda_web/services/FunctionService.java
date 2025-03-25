package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.function.FeedbackResponseDTO;
import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.exceptions.DatabaseException;
import com.cosmo.wanda_web.services.exceptions.InvalidFunctionException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.AssistantStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class FunctionService {

    @Autowired
    private PythonClient pythonClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserService userService;

    public FeedbackResponseDTO feedback(FunctionRequestDTO dto) {
//        AssistantStyle style = AssistantStyle.valueOf(dto.getAssistantStyle());
        ValidateResponseDTO response = pythonClient.feedback(dto);
        System.out.println("RESPOSTA: ");
        System.out.println("Valid: " + response.getValid());
        System.out.println("Pensamento: " + response.getThought());
        System.out.println("Resposta:" + response.getAnswer());
        return new FeedbackResponseDTO(response);
    }

    @Transactional
    public FunctionRequestDTO insert(FunctionRequestDTO dto){
        // Validar a função com o microservice Python

        ValidateResponseDTO response = pythonClient.validate(dto);

        // Verifica se a função é válida
        if (!response.getValid()){
            throw new InvalidFunctionException(response.getAnswer());
        }

        // Verifica se usuário existe pelo contexto
        User user = userService.authenticated();

        // Adiciona no Banco de dados a função aprovada
        Function function = new Function("jokenpo", dto.getCode(), user);
        function = functionRepository.save(function);

        return new FunctionRequestDTO(function.getFunction());
    }

    private ValidateResponseDTO validateMock(FunctionRequestDTO dto) {
        return new ValidateResponseDTO(true, "ok");
    }

    @Transactional(readOnly = true)
    public FunctionRequestDTO findByUser() {
        User user = userService.authenticated();

        Function function = functionRepository.findByPlayerId(user.getId()).orElseThrow(
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
        Function function = functionRepository.findByPlayerId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));
        function.setFunction(dto.getCode());
        function = functionRepository.save(function);
        return new FunctionRequestDTO(function.getFunction());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete() {
        User user = userService.authenticated();
        Function function = functionRepository.findByPlayerId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Function Not Found"));
        try {
            functionRepository.delete(function);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}
