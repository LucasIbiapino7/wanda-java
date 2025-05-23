package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("""
           SELECT COUNT(obj)
           FROM Tournament obj
           WHERE obj.creatorId = :creatorId AND obj.status = 'OPEN'
           """)
    Long countOpenTournaments(@Param("creatorId") Long creatorId);

    @Query("""
    SELECT obj
    FROM Tournament obj
    WHERE lower(obj.name) LIKE lower(concat('%', :searchTerm, '%')) AND obj.status = 'OPEN'
    ORDER BY obj.asPrivate ASC, obj.createdAt DESC
    """)
    Page<Tournament> findByNameWithOrdering(@Param("searchTerm") String searchTerm, Pageable pageable);

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
            OR t.creatorId = :userId
         ORDER BY t.createdAt DESC
      """,
            countQuery = """
        SELECT COUNT(DISTINCT t)
          FROM Tournament t
          LEFT JOIN t.users u
         WHERE u.id = :userId
            OR t.creatorId = :userId
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
}
