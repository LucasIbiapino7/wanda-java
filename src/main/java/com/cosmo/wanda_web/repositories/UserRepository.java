package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
