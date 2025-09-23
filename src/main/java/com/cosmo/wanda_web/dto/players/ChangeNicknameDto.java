package com.cosmo.wanda_web.dto.players;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChangeNicknameDto {
    @NotBlank(message = "O nickname é obrigatório")
    @Size(min = 3, max = 20, message = "O nickname deve ter entre 3 e 20 caracteres")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ0-9_ ]+$",
            message = "O nickname deve conter apenas letras, números, espaços ou _"
    )
    private String nickname;

    public ChangeNicknameDto() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
