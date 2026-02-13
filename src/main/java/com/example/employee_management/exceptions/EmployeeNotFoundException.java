package com.example.employee_management.exceptions;

import java.io.Serial;

public class EmployeeNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(String message) {
        super("Employee not found");
    }
}
