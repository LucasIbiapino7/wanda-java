package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Classroom;
import com.cosmo.wanda_web.entities.ClassroomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    // Todas as turmas de um instructor (com filtro opcional de status)
    @Query("""
           SELECT c
           FROM Classroom c
           WHERE c.instructor.id = :instructorId
             AND (:status IS NULL OR c.status = :status)
           ORDER BY c.createdAt DESC
           """)
    Page<Classroom> findAllByInstructor(@Param("instructorId") Long instructorId,@Param("status") ClassroomStatus status,Pageable pageable);

    // Turmas onde o aluno participa
    @Query("""
           SELECT cs.classroom
           FROM ClassroomStudent cs
           WHERE cs.student.id = :studentId
             AND (:status IS NULL OR cs.classroom.status = :status)
           ORDER BY cs.joinedAt DESC
           """)
    Page<Classroom> findAllByStudent(@Param("studentId") Long studentId, @Param("status") ClassroomStatus status, Pageable pageable);

    // Busca por codigo de acesso entre turmas ativas
    @Query("""
           SELECT c
           FROM Classroom c
           WHERE c.accessCode = :accessCode
             AND c.status = com.cosmo.wanda_web.entities.ClassroomStatus.ACTIVE
           """)
    Optional<Classroom> findByAccessCode(@Param("accessCode") String accessCode);

    // Verifica se código já existe entre turmas ativas (para garantir unicidade)
    @Query("""
           SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
           FROM Classroom c
           WHERE c.accessCode = :accessCode
             AND c.status = com.cosmo.wanda_web.entities.ClassroomStatus.ACTIVE
           """)
    boolean existsActiveByAccessCode(@Param("accessCode") String accessCode);

    // Torneios ativos vinculados a turma (usado no arquivamento)
    @Query("""
           SELECT COUNT(t)
           FROM Tournament t
           WHERE t.classroom.id = :classroomId
             AND t.status IN (
                 com.cosmo.wanda_web.entities.TournamentStatus.OPEN,
                 com.cosmo.wanda_web.entities.TournamentStatus.RUNNING
             )
           """)
    Long countActiveTournamentsByClassroom(@Param("classroomId") Long classroomId);
}