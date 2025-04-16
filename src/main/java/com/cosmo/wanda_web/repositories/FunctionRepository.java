package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FunctionRepository extends JpaRepository<Function, Long> {

    @Query("SELECT obj " +
            "FROM Function obj " +
            "WHERE obj.player.id = :id and obj.name = 'jokenpo1'"
    )
    Optional<Function>findJokenpo1ByPlayerId(Long id);

    @Query("SELECT obj FROM Function obj WHERE obj.player.id = :userId AND obj.name = :name")
    Optional<Function> findByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);

}
