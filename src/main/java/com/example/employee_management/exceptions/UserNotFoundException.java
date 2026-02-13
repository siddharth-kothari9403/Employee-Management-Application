package com.example.employee_management.exceptions;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    @Serial
    private static final long serialVersionUID = 3L;
}
