package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.exceptions.InvalidFunctionException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        ValidateResponseDTO response = pythonClient.validate(dto);

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
}
