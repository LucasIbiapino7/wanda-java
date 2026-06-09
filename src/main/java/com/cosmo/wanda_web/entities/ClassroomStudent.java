package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_classroom_student")
public class ClassroomStudent {

    @EmbeddedId
    private ClassroomStudentId id;

    @ManyToOne
    @MapsId("classroomId")
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "joined_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private LocalDateTime joinedAt;

    public ClassroomStudent() {
    }

    public ClassroomStudent(Classroom classroom, User student, LocalDateTime joinedAt) {
        this.id = new ClassroomStudentId(classroom.getId(), student.getId());
        this.classroom = classroom;
        this.student = student;
        this.joinedAt = joinedAt;
    }

    public ClassroomStudentId getId() {
        return id;
    }

    public void setId(ClassroomStudentId id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassroomStudent that = (ClassroomStudent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}