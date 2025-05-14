package com.cosmo.wanda_web.services.client;

import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.RoundRequestDTO;
import com.cosmo.wanda_web.dto.python.TurnResponseDTO;
import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "python-client",
        url  = "${PYTHON_BASE_URL:http://localhost:8000/api}"
)
public interface PythonClient {

    @PostMapping("/feedback")
    ValidateResponseDTO feedback(@RequestBody FunctionRequestDTO dto);

    @PostMapping("/validate")
    ValidateResponseDTO validate(@RequestBody FunctionRequestDTO dto);

    @PostMapping("/run")
    ValidateResponseDTO run(@RequestBody FunctionRequestDTO dto);

    @PostMapping("/round")
    TurnResponseDTO round(@RequestBody RoundRequestDTO dto);
}
