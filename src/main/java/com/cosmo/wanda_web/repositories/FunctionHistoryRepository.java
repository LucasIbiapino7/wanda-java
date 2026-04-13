package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.FunctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FunctionHistoryRepository extends JpaRepository<FunctionHistory, Long> {

    @Query("""
        SELECT MAX(fh.versionNumber)
        FROM FunctionHistory fh
        WHERE fh.function.id = :functionId
    """)
    Optional<Integer> findMaxVersionByFunctionId(@Param("functionId") Long functionId);
}