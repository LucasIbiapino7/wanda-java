package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String course;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassroomStatus status;
    @Column(name = "access_code", nullable = false, length = 6)
    private String accessCode;
    @Column(columnDefinition = "TEXT")
    private String mural;
    private String institution;
    private String city;
    private String state;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public Classroom() {
    }

    public Classroom(Long id, String name, String course, String description, ClassroomStatus status,String accessCode, String mural, String institution, String city, String state,
                     LocalDateTime createdAt, User instructor, Game game) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.description = description;
        this.status = status;
        this.accessCode = accessCode;
        this.mural = mural;
        this.institution = institution;
        this.city = city;
        this.state = state;
        this.createdAt = createdAt;
        this.instructor = instructor;
        this.game = game;
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

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equals(id, classroom.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}