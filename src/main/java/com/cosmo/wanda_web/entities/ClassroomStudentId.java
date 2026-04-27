package com.cosmo.wanda_web.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClassroomStudentId implements Serializable {
    private Long classroomId;
    private Long studentId;

    public ClassroomStudentId() {
    }

    public ClassroomStudentId(Long classroomId, Long studentId) {
        this.classroomId = classroomId;
        this.studentId = studentId;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassroomStudentId that = (ClassroomStudentId) o;
        return Objects.equals(classroomId, that.classroomId) &&
                Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classroomId, studentId);
    }
}