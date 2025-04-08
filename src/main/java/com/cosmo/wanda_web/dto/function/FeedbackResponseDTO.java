package com.cosmo.wanda_web.dto.function;

import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;

public class FeedbackResponseDTO {
    private Boolean valid;
    private String feedback;
    private Long feedbackId;

    public FeedbackResponseDTO() {
    }

    public FeedbackResponseDTO(ValidateResponseDTO dto) {
        this.valid = dto.getValid();
        this.feedback = dto.getAnswer();
    }

    public FeedbackResponseDTO(ValidateResponseDTO dto, Long feedbackId) {
        this.valid = dto.getValid();
        this.feedback = dto.getAnswer();
        this.feedbackId = feedbackId;
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

    public Long getFeedbackId() {
        return feedbackId;
    }
}
