package com.cosmo.wanda_web.services.client;

import com.cosmo.wanda_web.dto.bits.RoundBitsRequestDTO;
import com.cosmo.wanda_web.dto.function.FunctionRequestDTO;
import com.cosmo.wanda_web.dto.python.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/round/bits")
    TurnResponseDTO roundBits(@RequestBody RoundBitsRequestDTO dto);

    @PostMapping("/session")
    SessionCreateResponse createSession(@RequestBody SessionCreateRequest dto);

    @PostMapping("/session/execute")
    SessionExecuteResponse executeRound(@RequestBody SessionExecuteRequest dto);

    @DeleteMapping("/session/{sessionId}")
    void closeSession(@PathVariable("sessionId") String sessionId);
}
