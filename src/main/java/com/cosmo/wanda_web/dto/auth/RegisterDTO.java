package com.cosmo.wanda_web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
    @NotBlank(message = "Campo Requerido!")
    @Size(min = 2, max = 30, message = "O nome deve conter entre 1 e 30 caracteres!")
    private String name;
    @NotBlank(message = "Campo Requerido!")
    @Email(message = "Email Inválido!")
    private String email;
    @NotBlank(message = "Campo Requerido!")
    @Size(min = 6, max = 50, message = "A senha deve conter entre 6 e 50 caracteres")
    private String password;

    public RegisterDTO() {
    }

    public RegisterDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
