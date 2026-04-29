package com.cosmo.wanda_web.dto.classroom;

import jakarta.validation.constraints.Size;

public class ClassroomUpdateDTO {
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    private String name;

    private String course;
    private String description;
    private String mural;
    private String institution;
    private String city;
    private String state;

    public ClassroomUpdateDTO() {}

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
}
