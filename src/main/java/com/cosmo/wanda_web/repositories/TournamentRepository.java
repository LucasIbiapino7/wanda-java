package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("""
           SELECT COUNT(obj)
           FROM Tournament obj
           WHERE obj.creatorId = :creatorId AND obj.status = 'OPEN'
           """)
    Long countOpenTournaments(@Param("creatorId") Long creatorId);
}
