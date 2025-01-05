package com.cosmo.wanda_web.dto.python;

import java.util.List;

public class ValidateResponseDTO {

    private Boolean valid;
    private List<String> errors;

    public ValidateResponseDTO() {
    }

    public ValidateResponseDTO(Boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ValidateResponseDTO{" +
                "valid=" + valid +
                ", errors=" + errors +
                '}';
    }
}
