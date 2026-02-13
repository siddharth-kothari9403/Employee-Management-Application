package com.example.employee_management.service;

import com.example.employee_management.model.CustomUserDetails;
import com.example.employee_management.repository.LoginLogoutTimesRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("securityService")
public class SecurityService {

    private final LoginLogoutTimesRepository repository;

    public SecurityService(LoginLogoutTimesRepository repository) {
        this.repository = repository;
    }

    public boolean isOwner(Integer recordId, Object principal) {
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return false;
        }

        Integer currentEmployeeId = userDetails.getEmployeeId();
        return repository.findById(recordId)
                .map(record -> record.getEmployeeDetails().getId().equals(currentEmployeeId))
                .orElse(false);
    }

    public boolean isEmployeeIdCorrect(Integer recordId, Object principal) {
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return false;
        }
        Integer currentEmployeeId = userDetails.getEmployeeId();
        return Objects.equals(recordId, currentEmployeeId);
    }
}
