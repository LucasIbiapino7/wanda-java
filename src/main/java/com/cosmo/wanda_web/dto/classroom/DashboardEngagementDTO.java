package com.cosmo.wanda_web.dto.classroom;

import com.cosmo.wanda_web.entities.StudentEngagementStatus;

public class DashboardEngagementDTO {

    private Long userId;
    private String userName;
    private Long feedback;
    private Long run;
    private Long submit;
    private Long validCount;
    private Long invalidCount;
    private boolean hasSubmitted;
    private StudentEngagementStatus status;

    public DashboardEngagementDTO(Long userId, String userName, Long feedback, Long run, Long submit, Long validCount, Long invalidCount,
                                  boolean hasSubmitted, StudentEngagementStatus status) {
        this.userId = userId;
        this.userName = userName;
        this.feedback = feedback;
        this.run = run;
        this.submit = submit;
        this.validCount = validCount;
        this.invalidCount = invalidCount;
        this.hasSubmitted = hasSubmitted;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getFeedback() {
        return feedback;
    }

    public void setFeedback(Long feedback) {
        this.feedback = feedback;
    }

    public Long getRun() {
        return run;
    }

    public void setRun(Long run) {
        this.run = run;
    }

    public Long getSubmit() {
        return submit;
    }

    public void setSubmit(Long submit) {
        this.submit = submit;
    }

    public Long getValidCount() {
        return validCount;
    }

    public void setValidCount(Long validCount) {
        this.validCount = validCount;
    }

    public Long getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(Long invalidCount) {
        this.invalidCount = invalidCount;
    }

    public boolean isHasSubmitted() {
        return hasSubmitted;
    }

    public void setHasSubmitted(boolean hasSubmitted) {
        this.hasSubmitted = hasSubmitted;
    }

    public StudentEngagementStatus getStatus() {
        return status;
    }

    public void setStatus(StudentEngagementStatus status) {
        this.status = status;
    }
}