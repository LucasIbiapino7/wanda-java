package com.cosmo.wanda_web.dto.python;

import java.util.List;

public class ValidateResponseDTO {
    private Boolean valid;
    private String answer;
    private String thought;

    public ValidateResponseDTO() {
    }

    public ValidateResponseDTO(Boolean valid, String answer) {
        this.valid = valid;
        this.answer = answer;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    @Override
    public String toString() {
        return "ValidateResponseDTO{" +
                "valid=" + valid +
                ", answer='" + answer + '\'' +
                ", thought='" + thought + '\'' +
                '}';
    }
}
