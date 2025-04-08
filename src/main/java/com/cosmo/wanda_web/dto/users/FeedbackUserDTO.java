package com.cosmo.wanda_web.dto.users;

public class FeedbackUserDTO {
    private Long feedbackId;
    private String feedbackUser;

    public FeedbackUserDTO() {
    }

    public FeedbackUserDTO(Long feedbackId, String feedbackUser) {
        this.feedbackId = feedbackId;
        this.feedbackUser = feedbackUser;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public String getFeedbackUser() {
        return feedbackUser;
    }
}
