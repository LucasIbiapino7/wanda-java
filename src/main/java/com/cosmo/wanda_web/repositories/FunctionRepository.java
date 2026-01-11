package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.dto.function.FunctionResponseDto;
import com.cosmo.wanda_web.entities.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FunctionRepository extends JpaRepository<Function, Long> {

    @Query("SELECT obj " +
            "FROM Function obj " +
            "WHERE obj.player.id = :id and obj.name = 'jokenpo1'"
    )
    Optional<Function>findJokenpo1ByPlayerId(Long id);

    @Query("SELECT obj " +
            "FROM Function obj " +
            "WHERE obj.player.id = :id and obj.name = 'jokenpo2'"
    )
    Optional<Function> findJokenpo2ByPlayerId(Long id);

    @Query("SELECT obj " +
            "FROM Function obj " +
            "WHERE obj.player.id = :id and obj.name = 'bits'"
    )
    Optional<Function>findBitsByPlayerId(Long id);

    @Query("SELECT obj FROM Function obj WHERE obj.player.id = :userId AND obj.name = :name")
    Optional<Function> findByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);

    @Query("""
           SELECT new com.cosmo.wanda_web.dto.function.FunctionResponseDto(
               obj.function,
               obj.name,
               new com.cosmo.wanda_web.dto.game.GameDto(obj.game)
           )
           from Function obj
           where obj.player.id = :userId and obj.name = :name
           """)
    Optional<FunctionResponseDto> findByUserIdAndGameName(@Param("userId") Long userId, @Param("name") String name);

    @Query("""
           SELECT new com.cosmo.wanda_web.dto.function.FunctionResponseDto(
               obj.function,
               obj.name,
               new com.cosmo.wanda_web.dto.game.GameDto(obj.game.id, obj.game.name, obj.game.description)
           )
           FROM Function obj
           WHERE obj.player.id = :userId
           order by obj.game.name asc, obj.name asc
           """)
    List<FunctionResponseDto> findAllFunctionsByUser(@Param("userId") Long userId);
}
