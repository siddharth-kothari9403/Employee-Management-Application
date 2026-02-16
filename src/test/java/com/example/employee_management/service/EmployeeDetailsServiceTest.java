package com.example.employee_management.service;
import com.example.employee_management.entity.EmployeeDetails;
import com.example.employee_management.exceptions.EmployeeNotFoundException;
import com.example.employee_management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeDetailsServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeDetailsService employeeDetailsService;

    private EmployeeDetails employeeDetails1;
    private List<EmployeeDetails> employeeDetailsList;
    private List<EmployeeDetails> engineeringEmployeeDetailsList;

    @BeforeEach
    public void setUp(){

        this.employeeDetails1 = new EmployeeDetails(9, "John", "Doe", 30, "M", "john.doe@company.com", "1234567890", "Engineering");
        EmployeeDetails employeeDetails2 = new EmployeeDetails(10, "Alice", "Baker", 40, "F", "alice.baker@company.com", "9876543210", "HR");

        this.employeeDetailsList = new ArrayList<>(List.of(employeeDetails1, employeeDetails2));
        this.engineeringEmployeeDetailsList = new ArrayList<>(List.of(employeeDetails1));
    }

    @Test
    public void givenEmployeeToAddShouldReturnAddedEmployee(){
        when(employeeRepository.save(any())).thenReturn(employeeDetails1);
        EmployeeDetails employeeDetails = employeeDetailsService.addEmployee(employeeDetails1);
        assertThat(employeeDetails).isEqualTo(employeeDetails1);
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    public void givenValidIdReturnsEmployeeInfo(){
        when(employeeRepository.findById(9)).thenReturn(Optional.of(employeeDetails1));
        EmployeeDetails employeeDetails = employeeDetailsService.findById(9);
        assertThat(employeeDetails).isEqualTo(employeeDetails1);
        verify(employeeRepository,times(1)).findById(9);
    }

    @Test
    public void givenInvalidIdThrowsException(){
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeDetailsService.findById(1));
        verify(employeeRepository,times(1)).findById(1);
    }

    @Test
    public void givenEmployeeInfoWillBeUpdated(){
        when(employeeRepository.save(any())).thenReturn(employeeDetails1);
        EmployeeDetails employeeDetails = employeeDetailsService.updateEmployee(employeeDetails1);
        assertThat(employeeDetails).isEqualTo(employeeDetails1);
        verify(employeeRepository,times(1)).save(any());
    }

    @Test
    public void returnAllEmployees(){
        when(employeeRepository.findAll()).thenReturn(employeeDetailsList);
        List<EmployeeDetails> employees1 = employeeDetailsService.findAll();
        assertThat(employees1).isEqualTo(employeeDetailsList);
        verify(employeeRepository,times(1)).findAll();
    }

    @Test
    public void returnAllEmployeesByDepartment(){
        when(employeeRepository.findByDepartment(any())).thenReturn(engineeringEmployeeDetailsList);
        List<EmployeeDetails> employees1 = employeeDetailsService.findByDepartment("Engineering");
        assertThat(employees1).isEqualTo(engineeringEmployeeDetailsList);
        verify(employeeRepository,times(1)).findByDepartment(any());
    }
}

