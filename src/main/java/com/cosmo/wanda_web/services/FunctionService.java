package com.cosmo.wanda_web.services;

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

    @Transactional
    public FunctionRequestDTO insert(FunctionRequestDTO dto){
        // Validar a função com o microservice Python
//        ValidateResponseDTO response = pythonClient.validate(dto);

        ValidateResponseDTO response = validateMock(dto);

        // Verifica se a função é válida
        if (!response.getValid()){
            throw new InvalidFunctionException(response.getErrors().get(0));
        }

        // Verifica se usuário existe pelo contexto
        User user = userService.authenticated();

        // Adiciona no Banco de dados a função aprovada
        Function function = new Function("jokenpo", dto.getCode(), user);
        function = functionRepository.save(function);

        return new FunctionRequestDTO(function.getFunction());
    }

    private ValidateResponseDTO validateMock(FunctionRequestDTO dto) {
        return new ValidateResponseDTO(true, new ArrayList<>());
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
