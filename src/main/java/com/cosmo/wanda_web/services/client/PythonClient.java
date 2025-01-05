package com.cosmo.wanda_web.services.client;

import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "python-client", url = "http://localhost:8000/api")
public interface PythonClient {

    @PostMapping("/validate")
    ValidateResponseDTO validate(@RequestBody FunctionRequestDTO dto);
}
