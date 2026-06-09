package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.notification.NotificationResponseDTO;
import com.cosmo.wanda_web.entities.Notification;
import com.cosmo.wanda_web.entities.NotificationType;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.NotificationRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void create(Long userId, NotificationType type, Long referenceId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado")
        );
        Notification notification = new Notification(user, type, referenceId);
        notificationRepository.save(notification);
        log.info("Notificação criada. userId={}, type={}, referenceId={}", userId, type, referenceId);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnseen() {
        User user = userService.authenticated();
        return notificationRepository.findUnseenByUserId(user.getId())
                .stream()
                .map(NotificationResponseDTO::new)
                .toList();
    }

    @Transactional
    public void markAllSeen() {
        User user = userService.authenticated();
        notificationRepository.markAllSeenByUserId(user.getId());
        log.info("Notificações marcadas como vistas. userId={}", user.getId());
    }
}