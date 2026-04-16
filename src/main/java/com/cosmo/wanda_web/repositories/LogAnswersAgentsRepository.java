package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.LogAnswersAgents;
import com.cosmo.wanda_web.projections.auditoria.AgenteSummaryProjection;
import com.cosmo.wanda_web.projections.auditoria.FuncaoSummaryProjection;
import com.cosmo.wanda_web.projections.auditoria.InteractionTypeSummaryProjection;
import com.cosmo.wanda_web.projections.auditoria.JogoSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogAnswersAgentsRepository extends JpaRepository<LogAnswersAgents, Long> {
    @Query("""
        SELECT COUNT(l) 
        FROM LogAnswersAgents l
        WHERE (CAST(:from AS timestamp) IS NULL OR l.moment >= :from) AND (CAST(:from AS timestamp) IS NULL OR l.moment <= :to)
    """)
    Long countByPeriod(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT COUNT(DISTINCT l.user.id) 
        FROM LogAnswersAgents l
        WHERE (CAST(:from AS timestamp) IS NULL OR l.moment >= :from) AND (CAST(:from AS timestamp) IS NULL OR l.moment <= :to)
    """)
    Long countAlunosAtivos( @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT l.assistantStyle AS assistantStyle,
               COUNT(l) AS total,
               SUM(CASE WHEN l.feedbackUser = 'like' THEN 1 ELSE 0 END) AS likes,
               SUM(CASE WHEN l.feedbackUser = 'dislike' THEN 1 ELSE 0 END) AS dislikes
        FROM LogAnswersAgents l
        WHERE (CAST(:from AS timestamp) IS NULL OR l.moment >= :from) AND (CAST(:from AS timestamp) IS NULL OR l.moment <= :to)
        GROUP BY l.assistantStyle
        ORDER BY total DESC
    """)
    List<AgenteSummaryProjection> groupByAgente(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT g.name AS gameName, COUNT(l) AS total
        FROM LogAnswersAgents l
        JOIN l.game g
        WHERE (CAST(:from AS timestamp) IS NULL OR l.moment >= :from) AND (CAST(:from AS timestamp) IS NULL OR l.moment <= :to)
        GROUP BY g.name
        ORDER BY total DESC
    """)
    List<JogoSummaryProjection> groupByJogo(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT l.functionName AS functionName, COUNT(l) AS total
        FROM LogAnswersAgents l
        WHERE (CAST(:from AS timestamp) IS NULL OR l.moment >= :from) AND (CAST(:from AS timestamp) IS NULL OR l.moment <= :to)
        GROUP BY l.functionName
        ORDER BY total DESC
    """)
    List<FuncaoSummaryProjection> groupByFuncao(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    @Query("""
    SELECT l.interactionType AS interactionType, COUNT(l) AS total
    FROM LogAnswersAgents l
    WHERE l.interactionType IS NOT NULL
    AND (CAST(:from AS timestamp) IS NULL OR l.moment >= :from)
    AND (CAST(:from AS timestamp) IS NULL OR l.moment <= :to)
    GROUP BY l.interactionType
    ORDER BY total DESC
""")
    List<InteractionTypeSummaryProjection> groupByInteractionType(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
