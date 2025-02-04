package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByName(String name);
}
