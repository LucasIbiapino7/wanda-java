package com.cosmo.wanda_web.services.exceptions;

public class ClassroomAccessDeniedException extends RuntimeException {
    public ClassroomAccessDeniedException(String message) {
        super(message);
    }
}
