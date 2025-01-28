package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
