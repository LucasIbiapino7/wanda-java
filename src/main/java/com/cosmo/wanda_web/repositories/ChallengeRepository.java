package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.ChallengeStatus;
import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query("SELECT obj " +
            "FROM Challenge obj " +
            "WHERE obj.challenger.id = :userChallengerId AND obj.challenged.id = :userChallengedId AND obj.status = 'PENDING'")
    Optional<Challenge> checkIfChallengePendingExists(Long userChallengerId, Long userChallengedId);

    @Query(nativeQuery = true, value = """
            SELECT TB_CHALLENGE.id, TB_CHALLENGE.challenger_id AS challengerId, TB_CHALLENGE.created_at AS createdAt, challenged.name AS challengedName, challenger.name AS challengerName
            FROM TB_CHALLENGE
            INNER JOIN TB_USER as challenged ON challenged .id = TB_CHALLENGE.challenged_id
            INNER JOIN TB_USER as challenger ON challenger .id = TB_CHALLENGE.challenger_id
            WHERE status = 'PENDING' AND TB_CHALLENGE.challenged_id = :userChallengedId
            """)
    Page<FindAllPendingChallengerProjection> findAllPending(Long userChallengedId, Pageable pageable);

    @Modifying
    @Query("UPDATE Challenge obj " +
            "SET obj.status = :status, obj.match = :match " +
            "WHERE obj.id = :challengeId")
    void updateChallenge(Long challengeId, ChallengeStatus status, Match match);
}
