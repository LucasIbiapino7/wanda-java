package com.cosmo.wanda_web.dto.classroom;

import jakarta.validation.constraints.NotBlank;

public class ClassroomJoinDTO {
    @NotBlank(message = "O código de acesso é obrigatório")
    private String accessCode;

    public ClassroomJoinDTO() {
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
}
