package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT obj " +
            "FROM Player obj " +
            "JOIN FETCH obj.user u " +
            "WHERE UPPER(u.name) LIKE UPPER(CONCAT(:name, '%')) AND u.id <> :currentUserId")
    Page<Player> searchByName(String name, Long currentUserId, Pageable pageable);

    @Query("SELECT obj " +
            "FROM Player obj " +
            "JOIN FETCH obj.user u " +
            "LEFT JOIN FETCH u.functions " +
            "LEFT JOIN FETCH u.badges " +
            "WHERE obj IN :players")
    List<Player> findPlayersFunctionsAndBadges(List<Player> players);
}
