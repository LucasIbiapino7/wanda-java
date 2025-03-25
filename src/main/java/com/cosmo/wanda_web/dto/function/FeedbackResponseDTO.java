package com.cosmo.wanda_web.dto.function;

import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;

public class FeedbackResponseDTO {
    private Boolean valid;
    private String feedback;

    public FeedbackResponseDTO() {
    }

    public FeedbackResponseDTO(ValidateResponseDTO dto) {
        this.valid = dto.getValid();
        this.feedback = dto.getAnswer();
    }

    public FeedbackResponseDTO(Boolean valid, String feedback) {
        this.valid = valid;
        this.feedback = feedback;
    }

    public Boolean getValid() {
        return valid;
    }

    public String getFeedback() {
        return feedback;
    }
}
