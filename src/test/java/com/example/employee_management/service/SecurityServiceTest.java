package com.example.employee_management.service;

import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.entity.LoginLogoutTimes;
import com.example.employee_management.model.CustomUserDetails;
import com.example.employee_management.repository.LoginLogoutTimesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    @Mock
    private LoginLogoutTimesRepository repository;

    @InjectMocks
    private SecurityService securityService;

    @Test
    void isEmployeeIdCorrectShouldReturnTrueWhenIdsMatch() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getEmployeeId()).thenReturn(3);
        boolean result = securityService.isEmployeeIdCorrect(3, userDetails);
        assertTrue(result);
    }

    @Test
    void isEmployeeIdCorrectShouldReturnFalseWhenPrincipalIsWrongType() {
        boolean result = securityService.isEmployeeIdCorrect(3, "just-a-string-principal");
        assertFalse(result);
    }

    @Test
    void isOwnerShouldReturnTrueWhenRecordBelongsToUser() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getEmployeeId()).thenReturn(10);

        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId(10);
        LoginLogoutTimes record = new LoginLogoutTimes();
        record.setEmployeeDetails(employeeDetails);

        when(repository.findById(1)).thenReturn(Optional.of(record));
        boolean result = securityService.isOwner(1, userDetails);

        assertTrue(result);
    }

    @Test
    void isOwnerShouldReturnFalseWhenRecordDoesNotBelongToUser() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getEmployeeId()).thenReturn(10);

        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId(99);
        LoginLogoutTimes record = new LoginLogoutTimes();
        record.setEmployeeDetails(employeeDetails);

        when(repository.findById(1)).thenReturn(Optional.of(record));
        boolean result = securityService.isOwner(1, userDetails);

        assertFalse(result);
    }

    @Test
    void isOwnerShouldReturnFalseWhenRecordDoesNotExist() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(repository.findById(99)).thenReturn(Optional.empty());
        boolean result = securityService.isOwner(99, userDetails);

        assertFalse(result);
    }
}
