package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.ClassroomStudent;
import com.cosmo.wanda_web.entities.ClassroomStudentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassroomStudentRepository extends JpaRepository<ClassroomStudent, ClassroomStudentId> {

    // Todos os membros de uma turma com joinedAt
    @Query("""
       SELECT cs
       FROM ClassroomStudent cs
       JOIN FETCH cs.student
       WHERE cs.classroom.id = :classroomId
       ORDER BY cs.joinedAt ASC
       """)
    List<ClassroomStudent> findAllByClassroom(@Param("classroomId") Long classroomId);

    // IDs de todos os alunos de uma turma, usado como filtro nos dashboards
    @Query(
            value = """
            SELECT cs
            FROM ClassroomStudent cs
            JOIN FETCH cs.student
            WHERE cs.classroom.id = :classroomId
            ORDER BY cs.joinedAt ASC
            """,
            countQuery = """
                 SELECT COUNT(cs)
                 FROM ClassroomStudent cs
                 WHERE cs.classroom.id = :classroomId
                 """
    )
    Page<ClassroomStudent> findAllByClassroom(@Param("classroomId") Long classroomId, Pageable pageable);

    // Verifica se aluno já é membro de uma turma
    @Query("""
           SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END
           FROM ClassroomStudent cs
           WHERE cs.classroom.id = :classroomId
             AND cs.student.id = :studentId
           """)
    boolean existsByClassroomAndStudent(@Param("classroomId") Long classroomId, @Param("studentId") Long studentId);

    // Remove membro de uma turma
    @Modifying
    @Query("""
           DELETE FROM ClassroomStudent cs
           WHERE cs.classroom.id = :classroomId
             AND cs.student.id = :studentId
           """)
    void deleteByClassroomAndStudent(@Param("classroomId") Long classroomId, @Param("studentId") Long studentId);
}