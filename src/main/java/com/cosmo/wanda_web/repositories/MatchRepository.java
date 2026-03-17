package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
}
