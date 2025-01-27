package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
