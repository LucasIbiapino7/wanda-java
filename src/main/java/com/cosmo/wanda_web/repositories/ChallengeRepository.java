package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.ChallengeStatus;
import com.cosmo.wanda_web.entities.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query("SELECT obj " +
            "FROM Challenge obj " +
            "WHERE obj.challenger.id = :userChallengerId AND obj.challenged.id = :userChallengedId AND obj.status = 'PENDING'")
    Optional<Challenge> checkIfChallengePendingExists(Long userChallengerId,
                                            Long userChallengedId);
}
