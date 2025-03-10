package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
