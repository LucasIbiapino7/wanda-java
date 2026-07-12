package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.ChallengeStatus;
import com.cosmo.wanda_web.entities.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("""
       SELECT c
       FROM Challenge c
       WHERE c.challenger.id = :challengerId
         AND c.challenged.id = :challengedId
         AND c.game.id = :gameId
         AND c.status = 'PENDING'
       """)
    Optional<Challenge> checkIfChallengePendingExists(
            @Param("challengerId") Long challengerId,
            @Param("challengedId") Long challengedId,
            @Param("gameId") Long gameId);

    @Query("""
       SELECT c
       FROM Challenge c
       WHERE c.status = 'PENDING'
         AND c.challenged.id = :userChallengedId
       ORDER BY c.createdAt DESC
       """)
    Page<Challenge> findAllPending(@Param("userChallengedId") Long userChallengedId, Pageable pageable);

    @Modifying
    @Query("""
       UPDATE Challenge obj
       SET obj.status = :status,
           obj.match = :match,
           obj.answeredAt = :answeredAt
       WHERE obj.id = :challengeId
       """)
    void updateChallenge(
            @Param("challengeId") Long challengeId,
            @Param("status") ChallengeStatus status,
            @Param("match") Match match,
            @Param("answeredAt") LocalDateTime answeredAt);

    @Query("""
       SELECT c
       FROM Challenge c
       WHERE c.challenger.id = :userId
          OR c.challenged.id = :userId
       ORDER BY c.createdAt DESC
       """)
    Page<Challenge> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("""
       SELECT c
       FROM Challenge c
       WHERE c.classroom.id = :classroomId
       ORDER BY c.createdAt DESC
       """)
    Page<Challenge> findByClassroomId(@Param("classroomId") Long classroomId, Pageable pageable);

    @Query("""
       SELECT c
       FROM Challenge c
       WHERE c.classroom.id = :classroomId
         AND (c.challenger.id = :userId OR c.challenged.id = :userId)
       ORDER BY c.createdAt DESC
       """)
    Page<Challenge> findByClassroomAndUser(@Param("classroomId") Long classroomId, @Param("userId") Long userId, Pageable pageable);
}
