package com.cosmo.wanda_web.config;

import com.cosmo.wanda_web.entities.ProfileType;
import com.cosmo.wanda_web.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationBean {
    public boolean isInstructorOrAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        Object principal = authentication.getPrincipal();

        if (principal instanceof User u) {
            // 1) Admin global via ROLE_ADMIN
            boolean isAdmin = u.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // 2) checagem via ProfileType
            boolean isInstructor = u.getProfileType() == ProfileType.INSTRUCTOR;

            return isAdmin || isInstructor;
        }

        // fallback: só checa admin pelas roles
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
