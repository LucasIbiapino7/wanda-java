package com.cosmo.wanda_web.dto.function;

import com.cosmo.wanda_web.dto.python.ValidateResponseDTO;

public class FeedbackResponseDTO {
    private Boolean valid;
    private String feedback;

    public FeedbackResponseDTO() {
    }

    public FeedbackResponseDTO(ValidateResponseDTO dto) {
        this.valid = dto.getValid();
        if (dto.getErrors().size() > 1){
            StringBuilder aux = new StringBuilder();
            for (String error : dto.getErrors()) {
                aux.append(error);
                aux.append("\n");
            }
            this.feedback = aux.toString();
        }else {
            this.feedback = dto.getErrors().get(0);
        }
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
