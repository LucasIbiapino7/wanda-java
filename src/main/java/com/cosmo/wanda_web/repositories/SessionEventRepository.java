package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.SessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionEventRepository extends JpaRepository<SessionEvent, Long> {
}