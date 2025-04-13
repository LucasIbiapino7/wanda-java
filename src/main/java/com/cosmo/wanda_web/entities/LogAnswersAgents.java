package com.cosmo.wanda_web.entities;

import com.cosmo.wanda_web.services.utils.AssistantStyle;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_log_answers_agents")
public class LogAnswersAgents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private AssistantStyle assistantStyle;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime moment;
    @Column(columnDefinition = "TEXT")
    private String code;
    @Column(columnDefinition = "TEXT")
    private String answer;
    @Column(columnDefinition = "TEXT")
    private String thought;
    private Boolean valid;
    private String feedbackUser;
    private String functionName;

    public LogAnswersAgents() {
    }

    public LogAnswersAgents(Long id, User user, AssistantStyle assistantStyle, LocalDateTime moment, String code, String answer, String thought, Boolean valid, String functionName) {
        this.id = id;
        this.user = user;
        this.assistantStyle = assistantStyle;
        this.moment = moment;
        this.code = code;
        this.answer = answer;
        this.thought = thought;
        this.valid = valid;
        this.functionName = functionName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public AssistantStyle getAssistantStyle() {
        return assistantStyle;
    }

    public void setAssistantStyle(AssistantStyle assistantStyle) {
        this.assistantStyle = assistantStyle;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }

    public String getFeedbackUser() {
        return feedbackUser;
    }

    public void setFeedbackUser(String feedbackUser) {
        this.feedbackUser = feedbackUser;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogAnswersAgents that = (LogAnswersAgents) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
