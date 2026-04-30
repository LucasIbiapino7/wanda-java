package com.cosmo.wanda_web.repositories;

import com.cosmo.wanda_web.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
           SELECT n
           FROM Notification n
           WHERE n.user.id = :userId
             AND n.seen = false
           ORDER BY n.createdAt DESC
           """)
    List<Notification> findUnseenByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
           UPDATE Notification n
           SET n.seen = true
           WHERE n.user.id = :userId
             AND n.seen = false
           """)
    void markAllSeenByUserId(@Param("userId") Long userId);
}