package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.projections.dashboard.UserCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT obj " +
            "FROM Match obj " +
            "WHERE obj.player1.id = :userId OR obj.player2.id = :userId " +
            "ORDER BY obj.moment DESC"
            )
    Page<Match> searchAllById(Long userId, Pageable pageable);

    @Query("""
    SELECT m FROM Match m
    WHERE (CAST(:from AS timestamp) IS NULL OR m.moment >= :from)
    AND (CAST(:to AS timestamp) IS NULL OR m.moment <= :to)
    ORDER BY m.moment DESC
    """)
    Page<Match> findForAudit(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    // Partidas entre membros da turma para o ranking interno
    @Query("""
       SELECT m
       FROM Match m
       WHERE m.player1.id IN :userIds
         AND m.player2.id IN :userIds
         AND m.game.id = :gameId
       ORDER BY m.moment DESC
       """)
    Page<Match> findByUsersAndGame(@Param("userIds") List<Long> userIds, @Param("gameId") Long gameId, Pageable pageable);

    // Contagem de vitórias por aluno dentro da turma
    @Query("""
       SELECT m.winner.id AS userId, COUNT(m) AS total
       FROM Match m
       WHERE m.player1.id IN :userIds
         AND m.player2.id IN :userIds
         AND m.game.id = :gameId
         AND m.winner IS NOT NULL
       GROUP BY m.winner.id
       ORDER BY COUNT(m) DESC
       """)
    List<UserCountProjection> countWinsByUserIds(@Param("userIds") List<Long> userIds, @Param("gameId") Long gameId);
}
