package com.cosmo.wanda_web.dto.classroom;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClassroomAddStudentDTO {
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    public ClassroomAddStudentDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
