package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("""
           SELECT COUNT(obj)
           FROM Tournament obj
           WHERE obj.creator.id = :creatorId AND obj.status = 'OPEN'
           """)
    Long countOpenTournaments(@Param("creatorId") Long creatorId);

    // Buscar torneios para um participantes
    // Retorna torneios gerais abertos e torneios abertos das suas turmas
    @Query(
            value = """
            SELECT DISTINCT t
            FROM Tournament t
            LEFT JOIN t.classroom c
            LEFT JOIN ClassroomStudent cs
                  ON cs.classroom.id = c.id
                  AND cs.student.id = :userId
            WHERE lower(t.name) LIKE lower(concat('%', :searchTerm, '%'))
              AND t.status = com.cosmo.wanda_web.entities.TournamentStatus.OPEN
              AND (
                  t.classroom IS NULL
                  OR (
                      c.status = com.cosmo.wanda_web.entities.ClassroomStatus.ACTIVE
                      AND (
                          :isAdmin = true
                          OR c.instructor.id = :userId
                          OR cs.student.id IS NOT NULL
                      )
                  )
              )
            ORDER BY t.asPrivate ASC, t.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT t)
            FROM Tournament t
            LEFT JOIN t.classroom c
            LEFT JOIN ClassroomStudent cs
                  ON cs.classroom.id = c.id
                  AND cs.student.id = :userId
            WHERE lower(t.name) LIKE lower(concat('%', :searchTerm, '%'))
              AND t.status = com.cosmo.wanda_web.entities.TournamentStatus.OPEN
              AND (
                  t.classroom IS NULL
                  OR (
                      c.status = com.cosmo.wanda_web.entities.ClassroomStatus.ACTIVE
                      AND (
                          :isAdmin = true
                          OR c.instructor.id = :userId
                          OR cs.student.id IS NOT NULL
                      )
                  )
              )
            """
    )
    Page<Tournament> findAvailableOpenTournaments(
            @Param("searchTerm") String searchTerm,
            @Param("userId") Long userId,
            @Param("isAdmin") boolean isAdmin,
            Pageable pageable
    );

    @Query("""
           SELECT obj
           FROM Tournament obj
           WHERE obj.id = :id
           """)
    Tournament findByIdWithParticipants(@Param("id")Long id);

    @Query(
            value = """
        SELECT DISTINCT t
          FROM Tournament t
          LEFT JOIN t.users u
         WHERE u.id = :userId
            OR t.creator.id = :userId
         ORDER BY t.createdAt DESC
      """,
            countQuery = """
        SELECT COUNT(DISTINCT t)
          FROM Tournament t
          LEFT JOIN t.users u
         WHERE u.id = :userId
            OR t.creator.id = :userId
      """
    )
    Page<Tournament> findAllByUser(
            @Param("userId") Long userId, Pageable pageable
    );

    @Query("""
      SELECT CASE WHEN COUNT(obj)>0 THEN true ELSE false END
      FROM Tournament obj
      JOIN obj.users u
      WHERE obj.id = :tournamentId
        AND u.id = :userId
    """)
    boolean isUserInTournament(@Param("tournamentId") Long tournamentId, @Param("userId") Long userId);

    List<Tournament> findByStatusAndStartTimeLessThanEqual(TournamentStatus status, LocalDateTime time);

    @Modifying
    @Query("""
        UPDATE Tournament t
           SET t.status = com.cosmo.wanda_web.entities.TournamentStatus.RUNNING
         WHERE t.id = :id
           AND t.status = com.cosmo.wanda_web.entities.TournamentStatus.OPEN
      """)
    int tryStart(@Param("id") Long id);

    @Modifying
    @Query("""
    UPDATE Tournament t
    SET t.currentParticipants = t.currentParticipants + 1
    WHERE t.id = :id
    AND t.currentParticipants < t.maxParticipants
    AND t.status = 'OPEN'
""")
    int trySubscribe(@Param("id") Long id);

    // Torneios de uma turma específica
    @Query("""
       SELECT t
       FROM Tournament t
       WHERE t.classroom.id = :classroomId
       ORDER BY t.createdAt DESC
       """)
    Page<Tournament> findByClassroomId(@Param("classroomId") Long classroomId, Pageable pageable);

    // Cancela torneios ativos de uma turma (chamado no arquivamento)
    @Modifying
    @Query("""
       UPDATE Tournament t
       SET t.status = com.cosmo.wanda_web.entities.TournamentStatus.CANCELLED
       WHERE t.classroom.id = :classroomId
         AND t.status IN (
             com.cosmo.wanda_web.entities.TournamentStatus.OPEN,
             com.cosmo.wanda_web.entities.TournamentStatus.RUNNING
         )
       """)
    int cancelActiveByClassroom(@Param("classroomId") Long classroomId);
}
