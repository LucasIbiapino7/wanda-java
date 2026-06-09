package com.cosmo.wanda_web.config;

import com.cosmo.wanda_web.entities.ProfileType;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.ClassroomRepository;
import com.cosmo.wanda_web.repositories.ClassroomStudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationBean {

    private final ClassroomRepository classroomRepository;
    private final ClassroomStudentRepository classroomStudentRepository;

    public AuthorizationBean(ClassroomRepository classroomRepository, ClassroomStudentRepository classroomStudentRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomStudentRepository = classroomStudentRepository;
    }

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

    public boolean isClassroomInstructor(Authentication authentication, Long classroomId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        Object principal = authentication.getPrincipal();

        if (principal instanceof User u) {
            // Admin sempre pode
            boolean isAdmin = u.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) return true;

            // Verifica se é o instructor dono desta turma
            return classroomRepository.findById(classroomId)
                    .map(c -> c.getInstructor().getId().equals(u.getId()))
                    .orElse(false);
        }

        return false;
    }

    public boolean isClassroomMemberOrInstructor(Authentication authentication, Long classroomId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        Object principal = authentication.getPrincipal();

        if (principal instanceof User u) {
            boolean isAdmin = u.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) return true;

            boolean isOwner = classroomRepository.findById(classroomId)
                    .map(c -> c.getInstructor().getId().equals(u.getId()))
                    .orElse(false);
            if (isOwner) return true;

            return classroomStudentRepository.existsByClassroomAndStudent(classroomId, u.getId());
        }

        return false;
    }
}
