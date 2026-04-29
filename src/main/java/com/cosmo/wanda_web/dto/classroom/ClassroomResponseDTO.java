package com.cosmo.wanda_web.dto.classroom;

import com.cosmo.wanda_web.entities.Classroom;
import com.cosmo.wanda_web.entities.ClassroomStatus;

import java.time.LocalDateTime;

public class ClassroomResponseDTO {
    private Long id;
    private String name;
    private String course;
    private String description;
    private ClassroomStatus status;
    private String accessCode;
    private String mural;
    private String institution;
    private String city;
    private String state;
    private LocalDateTime createdAt;
    private Long gameId;
    private String gameName;
    private Long instructorId;
    private String instructorName;

    public ClassroomResponseDTO(Classroom c) {
        this.id = c.getId();
        this.name = c.getName();
        this.course = c.getCourse();
        this.description = c.getDescription();
        this.status = c.getStatus();
        this.accessCode = c.getAccessCode();
        this.mural = c.getMural();
        this.institution = c.getInstitution();
        this.city = c.getCity();
        this.state = c.getState();
        this.createdAt = c.getCreatedAt();
        this.gameId = c.getGame().getId();
        this.gameName = c.getGame().getName();
        this.instructorId = c.getInstructor().getId();
        this.instructorName = c.getInstructor().getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClassroomStatus getStatus() {
        return status;
    }

    public void setStatus(ClassroomStatus status) {
        this.status = status;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getMural() {
        return mural;
    }

    public void setMural(String mural) {
        this.mural = mural;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}
