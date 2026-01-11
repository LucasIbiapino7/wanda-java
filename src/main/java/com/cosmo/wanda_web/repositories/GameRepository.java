package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByNameIgnoreCase(String name);
}
